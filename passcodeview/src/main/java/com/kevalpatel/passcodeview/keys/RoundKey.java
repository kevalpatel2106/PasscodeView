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

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.view.animation.CycleInterpolator;

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class RoundKey extends Key {
    private static final int MAX_RIPPLE_ALPHA = 100;
    private static final int RIPPLE_DURATION = 350;

    private final Rect mBounds;                         //RoundKey bound.
    private final View mView;                           //Pin view
    private final float mKeyRadius;                     //Radius of the key background.
    private Builder mBuilder;

    private ValueAnimator mRippleValueAnimator;         //Ripple animator
    private ValueAnimator mErrorAnimator;               //Left-Right animator

    private boolean isRippleEffectRunning = false;      //Bool to indicate if the ripple effect is currently running?
    private int mCurrentRippleRadius = 0;               //Current ripple radius
    private int mCurrentAlpha;                          //Current ripple alpha.

    /**
     * Public constructor.
     *
     * @param view   {@link PinView}
     * @param digit  title of the key. (-1 for the backspace key)
     * @param bounds {@link Rect} bound.
     */
    private RoundKey(@NonNull PinView view,
                     @NonNull String digit,
                     @NonNull Rect bounds,
                     @NonNull RoundKey.Builder builder) {
        super(view, digit, bounds, builder);
        mBounds = bounds;
        mView = view;
        mBuilder = builder;
        mKeyRadius = calculateKeyRadius(bounds, mBuilder.getKeyPadding());

        setUpAnimator();
    }

    /**
     * Initialise the filed and listener for ripple effect mRippleValueAnimator
     */
    private void setUpAnimator() {
        final int circleAlphaOffset = (int) (MAX_RIPPLE_ALPHA / mKeyRadius);

        mRippleValueAnimator = ValueAnimator.ofFloat(0, mKeyRadius);
        mRippleValueAnimator.setDuration(RIPPLE_DURATION);
        mRippleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isRippleEffectRunning) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    mCurrentRippleRadius = (int) animatedValue;
                    mCurrentAlpha = (int) (MAX_RIPPLE_ALPHA - (animatedValue * circleAlphaOffset));
                    mView.invalidate();
                }
            }
        });
        mRippleValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isRippleEffectRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRippleEffectRunning = false;
                mCurrentRippleRadius = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        //Error animator
        mErrorAnimator = ValueAnimator.ofInt(0, 10);
        mErrorAnimator.setInterpolator(new CycleInterpolator(2));
        mErrorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBounds.left += (int) animation.getAnimatedValue();
                mBounds.right += (int) animation.getAnimatedValue();
                mView.invalidate();
            }
        });
    }

    /**
     * Calculate the circle radius.
     *
     * @param bounds  rectangle bound.
     * @param padding surround padding to the key.
     * @return radius.
     */
    private float calculateKeyRadius(Rect bounds, float padding) {
        return Math.min(bounds.height(), bounds.width()) / 2 - padding;       //radius = height or width - padding for single key
    }

    /**
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    @Override
    public void playClickAnimation() {
        mRippleValueAnimator.start();
    }

    /**
     * Show animation indicated invalid pin code
     */
    @Override
    public void onAuthFail() {
        mErrorAnimator.start();
    }

    @Override
    public void onAuthSuccess() {
        //Do noting
    }

    /**
     * Draw the key of canvas.
     * Don't change until you know what you are doing. :-)
     *
     * @param canvas canvas of the view o which key will be drawn
     */
    @SuppressWarnings({"deprecation", "ConstantConditions"})
    @Override
    public void drawText(@NonNull Canvas canvas) {
        //Draw key text
        canvas.drawText(getDigit() + "",                //Text to display on key
                mBounds.exactCenterX(),             //Set start point at center width of key
                mBounds.exactCenterY() - (mBuilder.getKeyTextPaint().descent() + mBuilder.getKeyTextPaint().ascent()) / 2,    //center height of key - text height/2
                mBuilder.getKeyTextPaint());
    }

    @Override
    public void drawShape(@NonNull Canvas canvas) {
        //Draw circle background
        canvas.drawCircle(mBounds.exactCenterX(),   //Set center width of key
                mBounds.exactCenterY(),             //Set center height of key
                mKeyRadius,
                mBuilder.getKeyPaint());

        //Play ripple effect if the key has ripple effect enabled.
        if (isRippleEffectRunning) {
            mBuilder.getRipplePaint().setAlpha(mCurrentAlpha);
            canvas.drawCircle(mBounds.exactCenterX(),
                    mBounds.exactCenterY(),
                    mCurrentRippleRadius,
                    mBuilder.getRipplePaint());
        }
    }

    @Override
    public void drawBackSpace(@NonNull Canvas canvas, @NonNull Drawable backSpaceIcon) {
        backSpaceIcon.setBounds((int) (mBounds.exactCenterX() - mKeyRadius / 2),
                (int) (mBounds.exactCenterY() - mKeyRadius / 2),
                (int) (mBounds.exactCenterX() + mKeyRadius / 2),
                (int) (mBounds.exactCenterY() + mKeyRadius / 2));
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
        if (getDigit().isEmpty()) return false;  //Empty key

        //Check if the click is between the width bounds
        if (touchX > mBounds.exactCenterX() - mKeyRadius
                && touchX < mBounds.exactCenterX() + mKeyRadius) {

            //Check if the click is between the height bounds
            if (touchY > mBounds.exactCenterY() - mKeyRadius
                    && touchY < mBounds.exactCenterY() + mKeyRadius) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("NullableProblems")
    public static class Builder extends Key.Builder {
        @Dimension
        private float mKeyPadding;
        @Dimension
        private float mKeyTextSize;                     //Surround padding to each single key
        @Dimension
        private float mKeyStrokeWidth;                   //Surround padding to each single key
        @ColorInt
        private int mKeyStrokeColor;                    //RoundKey background stroke color
        @ColorInt
        private int mKeyTextColor;                      //RoundKey text color

        @NonNull
        private Paint mKeyPaint;
        @NonNull
        private TextPaint mKeyTextPaint;
        @NonNull
        private Paint mRipplePaint;


        public Builder(@NonNull PinView pinView) {
            super(pinView);
        }

        @Dimension
        public float getKeyPadding() {
            return mKeyPadding;
        }

        public RoundKey.Builder setKeyPadding(@Dimension float keyPadding) {
            mKeyPadding = keyPadding;
            return this;
        }

        public RoundKey.Builder setKeyPadding(@DimenRes int keyPaddingRes) {
            mKeyPadding = getContext().getResources().getDimension(keyPaddingRes);
            return this;
        }

        public float getKeyTextSize() {
            return mKeyTextSize;
        }

        public RoundKey.Builder setKeyTextSize(float keyTextSize) {
            mKeyTextSize = keyTextSize;
            return this;
        }

        public RoundKey.Builder setKeyTextSize(@DimenRes int keyTextSize) {
            mKeyTextSize = getContext().getResources().getDimension(keyTextSize);
            return this;
        }

        public float getKeyStrokeWidth() {
            return mKeyStrokeWidth;
        }

        @Dimension
        public RoundKey.Builder setKeyStrokeWidth(float keyStrokeWidth) {
            mKeyStrokeWidth = keyStrokeWidth;
            return this;
        }

        @Dimension
        public RoundKey.Builder setKeyStrokeWidth(@DimenRes int keyStrokeWidth) {
            mKeyStrokeWidth = getContext().getResources().getDimension(keyStrokeWidth);
            return this;
        }

        @ColorInt
        public int getKeyStrokeColor() {
            return mKeyStrokeColor;
        }

        public RoundKey.Builder setKeyStrokeColor(@ColorInt int keyStrokeColor) {
            mKeyStrokeColor = keyStrokeColor;
            return this;
        }

        public RoundKey.Builder setKeyStrokeColorResource(@ColorRes int keyStrokeColor) {
            mKeyStrokeColor = getContext().getResources().getColor(keyStrokeColor);
            return this;
        }

        @ColorInt
        public int getKeyTextColor() {
            return mKeyTextColor;
        }

        public RoundKey.Builder setKeyTextColor(@ColorInt int keyTextColor) {
            mKeyTextColor = keyTextColor;
            return this;
        }

        public RoundKey.Builder setKeyTextColorResource(@ColorRes int keyTextColor) {
            mKeyTextColor = getContext().getResources().getColor(keyTextColor);
            return this;
        }

        @Override
        public Builder build() {
            //Set the keyboard paint
            mKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mKeyPaint.setStyle(Paint.Style.STROKE);
            mKeyPaint.setColor(mKeyStrokeColor);
            mKeyPaint.setTextSize(mKeyTextSize);
            mKeyPaint.setStrokeWidth(mKeyStrokeWidth);

            //Set the keyboard text paint
            mKeyTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mKeyTextPaint.setColor(mKeyTextColor);
            mKeyTextPaint.setTextSize(mKeyTextSize);
            mKeyTextPaint.setFakeBoldText(true);
            mKeyTextPaint.setTextAlign(Paint.Align.CENTER);

            return this;
        }

        @Override
        protected void setDefaults(@NonNull Context context) {
            mKeyTextColor = context.getResources().getColor(R.color.lib_key_default_color);
            mKeyStrokeColor = context.getResources().getColor(R.color.lib_key_background_color);
            mKeyTextSize = context.getResources().getDimension(R.dimen.lib_key_text_size);
            mKeyStrokeWidth = context.getResources().getDimension(R.dimen.lib_key_stroke_width);
            mKeyPadding = getContext().getResources().getDimension(R.dimen.lib_key_padding);

            //Prepare ripple paint
            mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRipplePaint.setStyle(Paint.Style.FILL);
        }

        @NonNull
        @Override
        public Paint getKeyPaint() {
            return mKeyPaint;
        }

        @NonNull
        @Override
        public Paint getKeyTextPaint() {
            return mKeyTextPaint;
        }

        @NonNull
        protected Paint getRipplePaint() {
            return mRipplePaint;
        }

        @NonNull
        @Override
        public RoundKey getKey(@NonNull String digit, @NonNull Rect bound) {
            return new RoundKey(super.getPinView(), digit, bound, this);
        }
    }
}
