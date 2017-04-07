package com.kevalpatel.passcodeview.fingerprint;

/**
 * Created by Keval on 07-Oct-16.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class AuthErrorCodes {

    /**
     * Called when a recoverable error has been encountered during authentication.
     * The help string is provided to give the user guidance for what went wrong, such as "Sensor dirty, please clean it."
     * This error can be fixed by the user. Developer should display the error message to the screen to guide
     * user how to fix the error.
     *
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationHelp(int, java.lang.CharSequence)'
     */
    static final int RECOVERABLE_ERROR = 843;

    /**
     * Called when an unrecoverable error has been encountered and the operation is complete.
     * No further callbacks will be made on this object.
     * Developer can stop the finger print scanning whenever this error occur and display the message received in callback.
     * Developer should use any other way of authenticating the user, like pin or password to authenticate the user.
     *
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationError(int, java.lang.CharSequence)'
     */
    static final int NON_RECOVERABLE_ERROR = 566;

    /**
     * Called when a fingerprint is valid but not recognized.
     *
     * See:'https://developer.android.com/reference/android/hardware/fingerprint/FingerprintManager.AuthenticationCallback.html#onAuthenticationError(int, java.lang.CharSequence)'
     */
    static final int CANNOT_RECOGNIZE_ERROR = 456;
}
