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

package com.kevalpatel.passcodeview.authenticator;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.ArrayList;

/**
 * Created by Keval on 14/04/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see <a href="https://github.com/kevalpatel2106/PasscodeView/wiki/Authenticator">Authenticator</a>
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

        //PIN is correct
        return PinAuthenticationState.SUCCESS;
    }

    private boolean isValidPinLength(final int typedLength) {
        return typedLength == mCorrectPin.length;
    }
}
