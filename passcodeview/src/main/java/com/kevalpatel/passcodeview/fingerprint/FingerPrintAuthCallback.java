package com.kevalpatel.passcodeview.fingerprint;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by Keval on 07-Oct-16.
 * This is the callback listener to notify the finger print authentication result to the parent.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

interface FingerPrintAuthCallback {

    /**
     * This method will notify the user whenever there is no finger print hardware found on the device.
     * Developer should use any other way of authenticating the user, like pin or password to authenticate the user.
     */
    void onNoFingerPrintHardwareFound();

    /**
     * This method will execute whenever device supports finger print authentication but does not
     * have any finger print registered.
     * Developer should notify user to add finger prints in the settings by opening security settings
     * by using {@link FingerPrintUtils#openSecuritySettings(Context)}.
     */
    void onNoFingerPrintRegistered();

    /**
     * This method will be called if the device is running on android below API 23. As starting from the
     * API 23, android officially got the finger print hardware support, for below marshmallow devices
     * developer should authenticate user by other ways like pin, password etc.
     */
    void onBelowMarshmallow();

    /**
     * This method will occur whenever  user authentication is successful.
     *
     * @param cryptoObject {@link FingerprintManager.CryptoObject} associated with the scanned finger print.
     */
    void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject);

    /**
     * This method will execute whenever any error occurs during the authentication.
     *
     * @param errorCode    Error code for the error occurred. These error code will be from {@link AuthErrorCodes}.
     * @param errorMessage A human-readable error string that can be shown in UI
     * @see AuthErrorCodes
     */
    void onAuthFailed(int errorCode, String errorMessage);
}
