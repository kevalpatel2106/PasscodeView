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

package com.kevalpatel.passcodeview.patternCells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public abstract class PatternCell {

    @NonNull
    private final BasePasscodeView mPasscodeView;

    @NonNull
    private final Rect mBound;

    @NonNull
    private final PatternPoint mPoint;

    protected PatternCell(@NonNull final PatternCell.Builder builder,
                          @NonNull final Rect bound,
                          @NonNull final PatternPoint location) {
        mPasscodeView = builder.mBasePasscodeView;
        mBound = bound;
        mPoint = location;
    }

    protected final BasePasscodeView getRootView() {
        return mPasscodeView;
    }

    protected final Context getContext() {
        return mPasscodeView.getContext();
    }

    @NonNull
    public PatternPoint getPoint() {
        return mPoint;
    }

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    public float getCenterX() {
        return mBound.exactCenterX();
    }

    public float getCenterY() {
        return mBound.exactCenterY();
    }

    public abstract void draw(@NonNull final Canvas canvas);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    public abstract boolean isIndicatorTouched(final float touchX, final float touchY);


    public static abstract class Builder {

        @NonNull
        private final BasePasscodeView mBasePasscodeView;

        public Builder(@NonNull final BasePasscodeView passcodeView) {
            mBasePasscodeView = passcodeView;
        }

        @NonNull
        protected final BasePasscodeView getRootView() {
            return mBasePasscodeView;
        }

        @NonNull
        protected final Context getContext() {
            return mBasePasscodeView.getContext();
        }

        @NonNull
        public abstract PatternCell buildInternal(@NonNull final Rect bound,
                                                  @NonNull final PatternPoint point);
    }
}
