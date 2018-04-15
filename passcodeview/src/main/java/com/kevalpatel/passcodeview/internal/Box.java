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

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 09/04/17.
 * This class defines a box inside the {@link BasePasscodeView}. The {@link BasePasscodeView} is made
 * up of these small boxes fitted together. Some example of the implementation of this class are
 * {@link BoxFingerprint} and {@link BoxKeypad}. Box follows the {@link PasscodeViewLifeCycle}.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see PasscodeViewLifeCycle
 */
abstract class Box implements PasscodeViewLifeCycle {

    /**
     * Reference to the {@link BasePasscodeView}.
     */
    private final BasePasscodeView mView;

    /**
     * Public constructor.
     *
     * @param rootView {@link BasePasscodeView} that contains this box.
     */
    Box(@NonNull final BasePasscodeView rootView) {
        mView = rootView;
    }

    /**
     * @return The root {@link BasePasscodeView} that contains this box.
     */
    @NonNull
    protected final BasePasscodeView getRootView() {
        return mView;
    }

    @NonNull
    protected final Context getContext() {
        return mView.getContext();
    }
}
