/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview.internal.box;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PasscodeViewLifeCycle;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public abstract class Box implements PasscodeViewLifeCycle {

    /**
     * Reference to the root {@link BasePasscodeView}.
     */
    private final BasePasscodeView mView;

    /**
     * Public constructor.
     *
     * @param rootView {@link BasePasscodeView} that contains this box.
     */
    Box(@NonNull BasePasscodeView rootView) {
        mView = rootView;
    }

    /**
     * @return The root {@link BasePasscodeView} that contains this box.
     */
    @NonNull
    public final BasePasscodeView getRootView() {
        return mView;
    }

    @NonNull
    public final Context getContext() {
        return mView.getContext();
    }

}
