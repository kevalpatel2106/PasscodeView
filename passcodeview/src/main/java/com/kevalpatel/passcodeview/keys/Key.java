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

package com.kevalpatel.passcodeview.keys;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.internal.BasePasscodeView;
import com.kevalpatel.passcodeview.internal.BoxKeypad;

/**
 * Created by Keval Patel on 07/04/17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public abstract class Key {
    /**
     * {@link Rect} bound of the key. Between this bound the key will be drawn.
     */
    @NonNull
    private final Rect mBound;
    /**
     * Title of the key.
     */
    private String mDigit;
    private Builder mBuilder;


    protected Key(@NonNull final Builder builder,
                  @NonNull final String keyTitle,
                  @NonNull final Rect bound) {
        mDigit = keyTitle;
        mBuilder = builder;
        mBound = bound;
    }

    @NonNull
    protected final BasePasscodeView getPasscodeView() {
        return mBuilder.mPasscodeView;
    }

    @NonNull
    protected final Context getContext() {
        return mBuilder.mPasscodeView.getContext();
    }

    @NonNull
    public final String getDigit() {
        return mDigit;
    }

    @NonNull
    protected final Rect getBounds() {
        return mBound;
    }

    /**
     * Implement this method with the description on how to draw the text on the canvas. This method
     * will be called by the {@link BoxKeypad} when ever it's time to
     * draw the text on the {@link Canvas}. Application should draw only the key ranging from 0 to 9.
     * Backspace key is drawn separately using {@link #drawBackSpace(Canvas, Drawable)}.
     *
     * @param canvas {@link Canvas} on which the key text will be draw.
     * @see RoundKey#drawText(Canvas)
     */
    public abstract void drawText(@NonNull Canvas canvas);

    /**
     * Implement this method with the description on how to draw the shape of the key on the canvas.
     * This method will be called by the {@link BoxKeypad} when ever
     * it's time to draw the shape of the key on the {@link Canvas}. Application daw any shape such
     * as round or square.
     *
     * @param canvas {@link Canvas} on which the key text will be draw.
     * @see RoundKey#drawShape(Canvas)
     */
    public abstract void drawShape(@NonNull Canvas canvas);

    /**
     * Draw the backspace key on the keyboard.
     *
     * @param canvas        {@link Canvas} on which the key text will be draw.
     * @param backSpaceIcon {@link Drawable} to display on the backspace key.
     */
    public abstract void drawBackSpace(@NonNull Canvas canvas, @NonNull Drawable backSpaceIcon);

    /**
     * Handle the click events. Play some animations!!!
     */
    public abstract void playClickAnimation();

    /**
     * Handle the authentication failure. Application can play some animations to let the user
     * know that authentication failed.
     */
    public abstract void onAuthFail();

    /**
     * Handle authentication success. Application can play some animations or change the text color
     * to let the user know that authentication failed.
     */
    public abstract void onAuthSuccess();

    /**
     * Check if the key is pressed or not for given touch coordinates?
     *
     * @param touchX touch X coordinate
     * @param touchY touch Y coordinate
     * @return true if the key is pressed else false.
     */
    public abstract boolean isKeyPressed(float touchX, float touchY);

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof String && obj.equals(mDigit);
    }

    @Override
    public int hashCode() {
        return mDigit.hashCode();
    }

    /**
     * Builder for building the {@link Key}.
     */
    public static abstract class Builder {

        /**
         * {@link BasePasscodeView} in which the key will be displayed.
         */
        @NonNull
        private final BasePasscodeView mPasscodeView;

        /**
         * Protected constructor.
         *
         * @param passcodeView {@link BasePasscodeView} in which the key will be displayed.
         */
        protected Builder(@NonNull final BasePasscodeView passcodeView) {
            mPasscodeView = passcodeView;
        }

        /**
         * @return {@link Context} of the caller.
         */
        @NonNull
        protected Context getContext() {
            return mPasscodeView.getContext();
        }

        /**
         * Build the {@link Key}.
         *
         * @param keyTitle Title of the key to display.
         * @return {@link Key}
         * @see #Key(Builder, String, Rect)
         */
        public abstract Key buildInternal(@NonNull final String keyTitle,
                                          @NonNull final Rect bound);
    }
}
