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

package com.kevalpatel.passcodeview.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PinView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class Indicator {

    private PinView mPinView;

    private Indicator() {
    }

    protected Indicator(@NonNull PinView mPinView,
                        @NonNull Rect bound,
                        @NonNull Indicator.Builder builder) {
        this.mPinView = mPinView;
    }

    protected final PinView getRootView() {
        return mPinView;
    }

    protected final Context getContext() {
        return mPinView.getContext();
    }

    public abstract void draw(@NonNull Canvas canvas, boolean isFilled);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();


    public static abstract class Builder {

        private PinView mPinView;

        private Builder() {
        }

        public Builder(PinView pinView) {
            mPinView = pinView;
            setDefaults(pinView.getContext());
        }

        @NonNull
        protected final PinView getRootView() {
            return mPinView;
        }

        @NonNull
        protected final Context getContext() {
            return mPinView.getContext();
        }

        @Dimension
        public abstract float getIndicatorWidth();

        public abstract Indicator.Builder build();

        protected abstract void setDefaults(@NonNull Context context);

        public abstract Indicator getIndicator(@NonNull Rect bound);
    }
}
