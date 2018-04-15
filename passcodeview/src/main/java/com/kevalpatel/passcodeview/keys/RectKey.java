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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.animation.CycleInterpolator;

import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class RectKey extends Key {
    /**
     * Duration of the error animation in milliseconds.
     */
    private static final long ANIMATION_DURATION = 200;
    @NonNull
    private final ValueAnimator mErrorAnimator;
    @NonNull
    private final Builder mBuilder;
    @NonNull
    private final Paint mKeyPaint;
    @NonNull
    private final TextPaint mKeyTextPaint;
    @NonNull
    private final Paint mRipplePaint;
    /**
     * True if the click animations are running or not.
     */
    private boolean isClickedAnimationRunning = false;

    /**
     * Public constructor.
     */
    private RectKey(@NonNull RectKey.Builder builder,
                    @NonNull final String keyTitle,
                    @NonNull final Rect bound) {
        super(builder, keyTitle, bound);

        mBuilder = builder;

        //Set the keyboard paint
        mKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mKeyPaint.setStyle(Paint.Style.STROKE);
        mKeyPaint.setColor(builder.mKeyStrokeColor);
        mKeyPaint.setTextSize(builder.mKeyTextSize);
        mKeyPaint.setStrokeWidth(builder.mKeyStrokeWidth);

        //Set the keyboard text paint
        mKeyTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mKeyTextPaint.setColor(builder.mKeyTextColor);
        mKeyTextPaint.setTextSize(builder.mKeyTextSize);
        mKeyTextPaint.setFakeBoldText(true);
        mKeyTextPaint.setTextAlign(Paint.Align.CENTER);

        //Ripple paint
        mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRipplePaint.setStyle(Paint.Style.STROKE);
        mRipplePaint.setColor(Utils.makeColorDark(builder.mKeyStrokeColor));
        mRipplePaint.setStrokeWidth(builder.mKeyStrokeWidth);

        //Error animator
        mErrorAnimator = ValueAnimator.ofInt(0, 10);
        mErrorAnimator.setInterpolator(new CycleInterpolator(2));
        mErrorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                getBounds().left += (int) animation.getAnimatedValue();
                getBounds().right += (int) animation.getAnimatedValue();
                getPasscodeView().invalidate();
            }
        });
    }

    /**
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    @Override
    public void playClickAnimation() {
        isClickedAnimationRunning = true;
        getPasscodeView().invalidate();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isClickedAnimationRunning = false;
                getPasscodeView().invalidate();
            }
        }, ANIMATION_DURATION);
    }

    /**
     * Draw the key of canvas.
     *
     * @param canvas canvas of the view o which key will be drawn
     */
    @Override
    public void drawText(@NonNull Canvas canvas) {
        //Draw key text
        canvas.drawText(getDigit() + "",                //Text to display on key
                getBounds().exactCenterX(),                 //Set start point at center width of key
                getBounds().exactCenterY() - (mKeyTextPaint.descent() + mKeyTextPaint.ascent()) / 2,    //center height of key - text height/2
                mKeyTextPaint);
    }

    @Override
    public void drawShape(@NonNull Canvas canvas) {
        //Draw circle background
        canvas.drawRect(getBounds().left + mBuilder.mKeyPadding,
                getBounds().top + mBuilder.mKeyPadding,
                getBounds().right - mBuilder.mKeyPadding,
                getBounds().bottom - mBuilder.mKeyPadding,
                isClickedAnimationRunning ? mRipplePaint : mKeyPaint);

    }

    /**
     * Draw the backspace key.
     *
     * @param canvas        {@link Canvas} on which the key text will be draw.
     * @param backSpaceIcon {@link Drawable} to display on the backspace key.
     */
    @Override
    public void drawBackSpace(@NonNull Canvas canvas, @NonNull Drawable backSpaceIcon) {
        backSpaceIcon.setColorFilter(new PorterDuffColorFilter(mKeyTextPaint.getColor(), PorterDuff.Mode.SRC_ATOP));
        backSpaceIcon.setBounds((int) (getBounds().exactCenterX() - Math.min(getBounds().height(), getBounds().width()) / 3),
                (int) (getBounds().exactCenterY() - Math.min(getBounds().height(), getBounds().width()) / 3),
                (int) (getBounds().exactCenterX() + Math.min(getBounds().height(), getBounds().width()) / 3),
                (int) (getBounds().exactCenterY() + Math.min(getBounds().height(), getBounds().width()) / 3));
        backSpaceIcon.draw(canvas);
    }

    /**
     * Check if the key is pressed or not for given touch coordinates?
     *
     * @param touchX touch X coordinate
     * @param touchY touch Y coordinate
     * @return true if the key is pressed else false.
     */
    @Override
    public boolean isKeyPressed(float touchX, float touchY) {

        //Check if the click is between the width bounds
        //noinspection SimplifiableIfStatement
        if (touchX > getBounds().left && touchX < getBounds().right) {

            //Check if the click is between the height bounds
            return touchY > getBounds().top && touchY < getBounds().bottom;
        }
        return false;
    }

    /**
     * Handle the authentication success.
     */
    @Override
    public void onAuthSuccess() {
        //Do noting
    }

    /**
     * Show animation indicated invalid pin code
     */
    @Override
    public void onAuthFail() {
        mErrorAnimator.start();
    }

    public static class Builder extends Key.Builder {
        /**
         * Surround padding to each single key.
         */
        @Dimension
        private float mKeyPadding;

        /**
         * Size of the key title text in pixels.
         */
        @Dimension
        private float mKeyTextSize;

        /**
         * Size of the width of the key border in pixels.
         */
        @Dimension
        private float mKeyStrokeWidth;

        /**
         * Key background stroke color.
         */
        @ColorInt
        private int mKeyStrokeColor;

        /**
         * Key title text color.
         */
        @ColorInt
        private int mKeyTextColor;

        public Builder(@NonNull final BasePasscodeView passcodeView) {
            super(passcodeView);
            setDefaults(getContext());
        }

        @NonNull
        public Builder setKeyPadding(@DimenRes int keyPaddingRes) {
            mKeyPadding = getContext().getResources().getDimension(keyPaddingRes);
            return this;
        }

        @NonNull
        public Builder setKeyPadding(@Dimension float keyPadding) {
            mKeyPadding = keyPadding;
            return this;
        }

        @NonNull
        public Builder setKeyTextSize(@DimenRes int keyTextSize) {
            mKeyTextSize = getContext().getResources().getDimension(keyTextSize);
            return this;
        }

        @NonNull
        public Builder setKeyTextSize(float keyTextSize) {
            mKeyTextSize = keyTextSize;
            return this;
        }

        @NonNull
        public Builder setKeyStrokeWidth(@DimenRes int keyStrokeWidth) {
            mKeyStrokeWidth = getContext().getResources().getDimension(keyStrokeWidth);
            return this;
        }

        @NonNull
        public Builder setKeyStrokeWidth(float keyStrokeWidth) {
            mKeyStrokeWidth = keyStrokeWidth;
            return this;
        }

        @NonNull
        public Builder setKeyStrokeColor(@ColorInt int keyStrokeColor) {
            mKeyStrokeColor = keyStrokeColor;
            return this;
        }

        @NonNull
        public Builder setKeyStrokeColorResource(@ColorRes int keyStrokeColor) {
            mKeyStrokeColor = getContext().getResources().getColor(keyStrokeColor);
            return this;
        }

        @NonNull
        public Builder setKeyTextColor(@ColorInt int keyTextColor) {
            mKeyTextColor = keyTextColor;
            return this;
        }

        @NonNull
        public Builder setKeyTextColorResource(@ColorRes int keyTextColor) {
            mKeyTextColor = getContext().getResources().getColor(keyTextColor);
            return this;
        }

        /**
         * Build the {@link RectKey}.
         *
         * @return {@link RectKey}
         */
        @NonNull
        @Override
        public RectKey buildInternal(@NonNull final String keyTitle, @NonNull final Rect bound) {
            return new RectKey(this, keyTitle, bound);
        }

        /**
         * Set the defaults for the key text, size and stroke width.
         *
         * @param context {@link Context} of the caller.
         */
        private void setDefaults(@NonNull final Context context) {
            mKeyTextColor = context.getResources().getColor(R.color.lib_key_default_color);
            mKeyStrokeColor = context.getResources().getColor(R.color.lib_key_background_color);
            mKeyTextSize = context.getResources().getDimension(R.dimen.lib_key_text_size);
            mKeyStrokeWidth = context.getResources().getDimension(R.dimen.lib_key_stroke_width);
            mKeyPadding = getContext().getResources().getDimension(R.dimen.lib_key_padding);
        }
    }
}
