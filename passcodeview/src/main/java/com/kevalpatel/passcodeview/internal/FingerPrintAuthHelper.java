/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by Keval on 07-Oct-16.<p>
 * This class will authenticate user with finger print.
 * This class is the extended version of {@link 'https://github.com/multidots/android-fingerprint-authentication/blob/master/fingerprint-auth/src/main/java/com/multidots/fingerprintauth/FingerPrintAuthHelper.java'}
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
final class FingerPrintAuthHelper {
    /**
     * Called when a recoverable error has been encountered during authentication.
     * The help string is provided to give the user guidance for what went wrong, such as "Sensor dirty, please clean it."
     * This error can be fixed by the user. Developer should display the error message to the screen to guide
     * user how to fix the error.
     * <p>
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationHelp(int, java.lang.CharSequence)'
     */
    static final int RECOVERABLE_ERROR = 843;

    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * No further callbacks will be made on this object.
     * Developer can stop the finger print scanning whenever this error occur and display the message received in callback.
     * Developer should use any other way of authenticating the user, like pin or password to authenticate the user.
     * <p>
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationError(int, java.lang.CharSequence)'
     */
    static final int NON_RECOVERABLE_ERROR = 566;

    /**
     * Called when a fingerprint is valid but not recognized.
     * <p>
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationError(int, java.lang.CharSequence)'
     */
    static final int CANNOT_RECOGNIZE_ERROR = 456;

    private static final String KEY_NAME = UUID.randomUUID().toString();

    //error messages
    private static final String ERROR_FAILED_TO_GENERATE_KEY = "Failed to generate secrete key for authentication.";
    private static final String ERROR_FAILED_TO_INIT_CHIPPER = "Failed to generate cipher key for authentication.";

    private KeyStore mKeyStore;
    private Cipher mCipher;

    /**
     * Instance of the caller class.
     */
    private Context mContext;

    /**
     * {@link FingerPrintAuthCallback} to notify the parent caller about the authentication status.
     */
    private FingerPrintAuthCallback mCallback;

    /**
     * {@link CancellationSignal} for finger print authentication.
     */
    private CancellationSignal mCancellationSignal;

    /**
     * Boolean to know if the finger print scanning is currently enabled.
     */
    private boolean isScanning;

    /**
     * Public constructor.
     *
     * @param context  instance of the caller.
     * @param callback {@link FingerPrintAuthCallback} to get notify whenever authentication success/fails.
     */
    FingerPrintAuthHelper(@NonNull Context context, @NonNull FingerPrintAuthCallback callback) {
        mCallback = callback;
        mContext = context;
    }

    /**
     * Private constructor.
     */
    private FingerPrintAuthHelper() {
    }

    /**
     * Check if the finger print hardware is available.
     *
     * @param context instance of the caller.
     * @return true if finger print authentication is supported.
     */
    @SuppressWarnings("MissingPermission")
    private boolean checkFingerPrintAvailability(@NonNull Context context) {
        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);

            if (!fingerprintManager.isHardwareDetected()) {
                return false;
            } else return fingerprintManager.hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    /**
     * Generate authentication key. This is for API @3 or above only.
     *
     * @return true if the key generated successfully.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean generateKey() {
        mKeyStore = null;
        KeyGenerator keyGenerator;

        //Get the instance of the key store.
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException |
                NoSuchProviderException e) {
            return false;
        } catch (KeyStoreException e) {
            return false;
        }

        //generate key.
        try {
            mKeyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

            return true;
        } catch (NoSuchAlgorithmException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException e) {
            return false;
        }
    }

    /**
     * Initialize the cipher. This is for API @3 or above only.
     *
     * @return true if the initialization is successful.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private boolean cipherInit() {
        boolean isKeyGenerated = generateKey();

        if (!isKeyGenerated) {
            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY);
            return false;
        }

        try {
            mCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY);
            return false;
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    private FingerprintManager.CryptoObject getCryptoObject() {
        return cipherInit() ? new FingerprintManager.CryptoObject(mCipher) : null;
    }

    /**
     * Start the finger print authentication by enabling the finger print sensor.
     * Note: Use this function in the onResume() of the activity/fragment. Never forget to call {@link #stopAuth()}
     * in onPause() of the activity/fragment.
     */
    @TargetApi(Build.VERSION_CODES.M)
    void startAuth() {
        if (isScanning) stopAuth();

        //check if the device supports the finger print hardware?
        if (!checkFingerPrintAvailability(mContext)) return;

        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);

        FingerprintManager.CryptoObject cryptoObject = getCryptoObject();
        if (cryptoObject == null) {
            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
        } else {
            mCancellationSignal = new CancellationSignal();
            //noinspection MissingPermission
            fingerprintManager.authenticate(cryptoObject,
                    mCancellationSignal,
                    0,
                    new FingerprintManager.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            mCallback.onFingerprintAuthFailed(NON_RECOVERABLE_ERROR, errString.toString());
                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            mCallback.onFingerprintAuthFailed(RECOVERABLE_ERROR, helpString.toString());
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            mCallback.onFingerprintAuthFailed(CANNOT_RECOGNIZE_ERROR, "Cannot recognize the fingerprint.");
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                            mCallback.onFingerprintAuthSuccess(result.getCryptoObject());
                        }
                    }, null);
        }
    }

    /**
     * Stop the finger print authentication.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void stopAuth() {
        if (mCancellationSignal != null) {
            isScanning = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    /**
     * @return true if currently listening the for the finger print.
     */
    public boolean isScanning() {
        return isScanning;
    }

    /**
     * This is the callback listener to notify the finger print authentication result to the parent.
     */
    interface FingerPrintAuthCallback {
        /**
         * This method will occur whenever  user authentication is successful.
         *
         * @param cryptoObject {@link FingerprintManager.CryptoObject} associated with the scanned finger print.
         */
        void onFingerprintAuthSuccess(FingerprintManager.CryptoObject cryptoObject);

        /**
         * This method will execute whenever any error occurs during the authentication.
         *
         * @param errorCode    Error code for the error occurred. These error code will be from error codes.
         * @param errorMessage A human-readable error string that can be shown in UI. This may be null.
         */
        void onFingerprintAuthFailed(int errorCode, @Nullable String errorMessage);
    }
}
