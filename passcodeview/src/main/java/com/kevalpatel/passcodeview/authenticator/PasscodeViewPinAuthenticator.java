/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.authenticator;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;

/**
 * Created by Keval on 14/04/18.
 *
 * @author [kevalpatel2106](https : / / github.com / kevalpatel2106)
 */
public final class PasscodeViewPinAuthenticator implements PinAuthenticator {

    private final int[] mCorrectPin;

    public PasscodeViewPinAuthenticator(int[] correctPin) {
        mCorrectPin = correctPin;
    }

    @WorkerThread
    @Override
    public PinAuthenticationState isValidPin(@NonNull final ArrayList<Integer> pinDigits) {
        //Check if the size of the entered pin matches the correct pin
        if (!isValidPinLength(pinDigits.size())) return PinAuthenticationState.NEED_MORE_DIGIT;

        //This calculations won't take much time.
        //We are not blocking the UI.
        for (int i = 0; i < mCorrectPin.length; i++) {
            if (mCorrectPin[i] != pinDigits.get(i)) {

                //Digit did not matched
                //Wrong PIN
                return PinAuthenticationState.FAIL;
            }
        }

        //PIN is correct¬
        return PinAuthenticationState.SUCCESS;
    }

    private boolean isValidPinLength(final int typedLength) {
        return typedLength == mCorrectPin.length;
    }
}
