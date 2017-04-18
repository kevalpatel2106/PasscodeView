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

package com.kevalpatel.passcodeview.patternCells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PasscodeView;
import com.kevalpatel.passcodeview.PatternView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class PatternCell {

    private PatternView mPatternView;

    private Rect mBound;
    private Integer mIndex;

    private PatternCell() {
    }

    protected PatternCell(@NonNull PatternView pinView,
                          @NonNull Rect bound,
                          @NonNull PatternCell.Builder builder,
                          int index) {
        this.mPatternView = pinView;
        mBound = bound;
        mIndex = index;
    }

    protected final PasscodeView getRootView() {
        return mPatternView;
    }

    protected final Context getContext() {
        return mPatternView.getContext();
    }

    public abstract void draw(@NonNull Canvas canvas, boolean isFilled);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    public abstract boolean isIndicatorTouched(float touchX, float touchY);

    public Integer getIndex() {
        return mIndex;
    }

    public static abstract class Builder {

        private PatternView mPatternView;

        private Builder() {
        }

        public Builder(@NonNull PatternView pinView) {
            mPatternView = pinView;
            setDefaults(pinView.getContext());
        }

        @NonNull
        protected final PatternView getRootView() {
            return mPatternView;
        }

        @NonNull
        protected final Context getContext() {
            return mPatternView.getContext();
        }

        @Dimension
        public abstract float getCellRadius();

        public abstract PatternCell.Builder build();

        protected abstract void setDefaults(@NonNull Context context);

        public abstract PatternCell getCell(@NonNull Rect bound, int index);
    }
}
