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

package com.kevalpatel.passcodeview.keys;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PinView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@SuppressWarnings("ALL")
public abstract class Key {
    private String mDigit;                        //RoundKey title.

    private Key() {
    }

    protected Key(@NonNull PinView view,
                  @NonNull String digit,
                  @NonNull Rect bounds,
                  @NonNull Key.Builder builder) {
        mDigit = digit;
    }

    public abstract void draw(@NonNull Canvas canvas);

    public final String getDigit() {
        return mDigit;
    }

    public abstract void onAuthFail();

    public abstract void onAuthSuccess();

    public abstract boolean checkKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY);

    public abstract void playClickAnimation();

    public static abstract class Builder {

        private PinView mPinView;

        private Builder() {
        }

        protected Builder(PinView pinView) {
            mPinView = pinView;
            setDefaults(pinView.getContext());
        }

        @NonNull
        protected final PinView getPinView() {
            return mPinView;
        }

        @NonNull
        protected final Context getContext() {
            return mPinView.getContext();
        }

        public abstract Builder build();

        protected abstract void setDefaults(@NonNull Context context);

        @NonNull
        public abstract Paint getKeyPaint();

        @NonNull
        public abstract Paint getKeyTextPaint();

        @NonNull
        public abstract Key getKey(@NonNull String digit, @NonNull Rect bound);
    }
}
