package com.kevalpatel.passcodeview.fingerprint;

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
 *
 * @author 'https://github.com/kevalpatel2106'
 */
class FingerPrintAuthHelper {
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
     * Private constructor.
     */
    private FingerPrintAuthHelper(@NonNull Context context, @NonNull FingerPrintAuthCallback callback) {
        mCallback = callback;
        mContext = context;
    }

    /**
     * Private constructor.
     */
    private FingerPrintAuthHelper() {
        throw new RuntimeException("Use getHelper() to initialize FingerPrintAuthHelper.");
    }

    /**
     * Get the {@link FingerPrintAuthHelper}
     *
     * @param context  instance of the caller.
     * @param callback {@link FingerPrintAuthCallback} to get notify whenever authentication success/fails.
     * @return {@link FingerPrintAuthHelper}
     */
    @SuppressWarnings("ConstantConditions")
    public static FingerPrintAuthHelper getHelper(@NonNull Context context, @NonNull FingerPrintAuthCallback callback) {
        if (context == null) {
            throw new IllegalArgumentException("Context cannot be null.");
        } else if (callback == null) {
            throw new IllegalArgumentException("FingerPrintAuthCallback cannot be null.");
        }

        return new FingerPrintAuthHelper(context, callback);
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

                // Device doesn't support fingerprint authentication
                mCallback.onNoFingerPrintHardwareFound();
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {

                // User hasn't enrolled any fingerprints to authenticate with
                mCallback.onNoFingerPrintRegistered();
                return false;
            }
            return true;
        } else {
            mCallback.onBelowMarshmallow();
            return false;
        }
    }

    /**
     * Generate authentication key.
     *
     * @return true if the key generated successfully.
     */
    @TargetApi(23)
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
     * Initialize the cipher.
     *
     * @return true if the initialization is successful.
     */
    @TargetApi(23)
    private boolean cipherInit() {
        boolean isKeyGenerated = generateKey();

        if (!isKeyGenerated) {
            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY);
            return false;
        }

        try {
            mCipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_GENERATE_KEY);
            return false;
        }

        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
            return false;
        }
    }

    @TargetApi(23)
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
    public void startAuth() {
        if (isScanning) stopAuth();

        //check if the device supports the finger print hardware?
        if (!checkFingerPrintAvailability(mContext)) return;

        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);

        FingerprintManager.CryptoObject cryptoObject = getCryptoObject();
        if (cryptoObject == null) {
            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, ERROR_FAILED_TO_INIT_CHIPPER);
        } else {
            mCancellationSignal = new CancellationSignal();
            //noinspection MissingPermission
            fingerprintManager.authenticate(cryptoObject,
                    mCancellationSignal,
                    0,
                    new FingerprintManager.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationError(int errMsgId, CharSequence errString) {
                            mCallback.onAuthFailed(AuthErrorCodes.NON_RECOVERABLE_ERROR, errString.toString());
                        }

                        @Override
                        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                            mCallback.onAuthFailed(AuthErrorCodes.RECOVERABLE_ERROR, helpString.toString());
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            mCallback.onAuthFailed(AuthErrorCodes.CANNOT_RECOGNIZE_ERROR, null);
                        }

                        @Override
                        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                            mCallback.onAuthSuccess(result.getCryptoObject());
                        }
                    }, null);
        }
    }

    /**
     * Stop the finger print authentication.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopAuth() {
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
}
