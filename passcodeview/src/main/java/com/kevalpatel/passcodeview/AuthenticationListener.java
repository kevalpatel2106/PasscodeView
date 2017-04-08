package com.kevalpatel.passcodeview;

/**
 * Created by Keval on 06-Apr-17.
 */

public interface AuthenticationListener {

    void onAuthenticationSuccessful();

    void onAuthenticationFailed();
}
