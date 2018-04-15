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

package com.kevalpatel.passcodeview.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public abstract class Indicator {
    @NonNull
    private final Rect mBound;

    @NonNull
    private final Indicator.Builder mBuilder;

    Indicator(@NonNull final Indicator.Builder builder,
              @NonNull final Rect bound) {
        mBound = bound;
        mBuilder = builder;
    }

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    @NonNull
    protected final BasePasscodeView getRootView() {
        return mBuilder.getRootView();
    }

    @NonNull
    protected final Context getContext() {
        return mBuilder.getContext();
    }

    public abstract void draw(@NonNull final Canvas canvas, final boolean isFilled);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    public static abstract class Builder {

        @NonNull
        private final BasePasscodeView mPasscodeView;

        public Builder(@NonNull final BasePasscodeView passcodeView) {
            mPasscodeView = passcodeView;
        }

        @NonNull
        protected final BasePasscodeView getRootView() {
            return mPasscodeView;
        }

        @Dimension
        public abstract float getIndicatorWidth();

        @NonNull
        protected final Context getContext() {
            return mPasscodeView.getContext();
        }

        public abstract Indicator buildInternal(@NonNull final Rect bound);
    }
}
