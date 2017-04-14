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
import android.view.animation.CycleInterpolator;

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class SquareKey extends Key {
    private final Rect mBounds;                         //RoundKey bound.
    private final Builder mBuilder;
    private final ValueAnimator mErrorAnimator;
    private boolean isClickedAnimationRunning = false;

    /**
     * Public constructor.
     *
     * @param pinView {@link PinView}
     * @param digit   title of the key. (-1 for the backspace key)
     * @param bounds  {@link Rect} bound.
     */
    private SquareKey(@NonNull PinView pinView,
                      @NonNull String digit,
                      @NonNull Rect bounds,
                      @NonNull SquareKey.Builder builder) {
        super(pinView, digit, bounds, builder);

        mBounds = bounds;
        mBuilder = builder;

        //Error animator
        mErrorAnimator = ValueAnimator.ofInt(0, 10);
        mErrorAnimator.setInterpolator(new CycleInterpolator(2));
        mErrorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBounds.left += (int) animation.getAnimatedValue();
                mBounds.right += (int) animation.getAnimatedValue();
                getPinView().invalidate();
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
        getPinView().invalidate();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isClickedAnimationRunning = false;
                getPinView().invalidate();
            }
        }, 200);
    }

    /**
     * Show animation indicated invalid pin code
     */
    @Override
    public void onAuthFail() {
        mErrorAnimator.start();
    }

    /**
     * Draw the key of canvas.
     * Don't change until you know what you are doing. :-)
     *
     * @param canvas canvas of the view o which key will be drawn
     */
    @Override
    public void drawText(@NonNull Canvas canvas) {
        //Draw key text
        canvas.drawText(getDigit() + "",                //Text to display on key
                mBounds.exactCenterX(),                 //Set start point at center width of key
                mBounds.exactCenterY() - (mBuilder.getKeyTextPaint().descent() + mBuilder.getKeyTextPaint().ascent()) / 2,    //center height of key - text height/2
                mBuilder.getKeyTextPaint());
    }

    @Override
    public void drawShape(@NonNull Canvas canvas) {
        float distanceToCenter = (Math.min(mBounds.height(), mBounds.width()) - mBuilder.getKeyPadding()) / 2;

        //Draw circle background
        canvas.drawRect(mBounds.exactCenterX() - distanceToCenter,
                mBounds.exactCenterY() - distanceToCenter,
                mBounds.exactCenterX() + distanceToCenter,
                mBounds.exactCenterY() + distanceToCenter,
                isClickedAnimationRunning ? mBuilder.getClickPaint() : mBuilder.getKeyPaint());

    }

    @Override
    public void drawBackSpace(@NonNull Canvas canvas, @NonNull Drawable backSpaceIcon) {
        float distanceToCenter = (Math.min(mBounds.height(), mBounds.width()) - mBuilder.getKeyPadding()) / 3;

        backSpaceIcon.setBounds((int) (mBounds.exactCenterX() - distanceToCenter),
                (int) (mBounds.exactCenterY() - distanceToCenter),
                (int) (mBounds.exactCenterX() + distanceToCenter),
                (int) (mBounds.exactCenterY() + distanceToCenter));
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
        if (touchX > mBounds.left && touchX < mBounds.right) {

            //Check if the click is between the height bounds
            if (touchY > mBounds.top && touchY < mBounds.bottom) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onAuthSuccess() {
        //Do noting
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
        private Paint mClickPaint;

        public Builder(@NonNull PinView pinView) {
            super(pinView);
        }

        @Dimension
        public float getKeyPadding() {
            return mKeyPadding;
        }

        public Builder setKeyPadding(@DimenRes int keyPaddingRes) {
            mKeyPadding = getContext().getResources().getDimension(keyPaddingRes);
            return this;
        }

        public Builder setKeyPadding(@Dimension float keyPadding) {
            mKeyPadding = keyPadding;
            return this;
        }

        public float getKeyTextSize() {
            return mKeyTextSize;
        }

        public Builder setKeyTextSize(@DimenRes int keyTextSize) {
            mKeyTextSize = getContext().getResources().getDimension(keyTextSize);
            return this;
        }

        public Builder setKeyTextSize(float keyTextSize) {
            mKeyTextSize = keyTextSize;
            return this;
        }

        public float getKeyStrokeWidth() {
            return mKeyStrokeWidth;
        }

        @Dimension
        public Builder setKeyStrokeWidth(@DimenRes int keyStrokeWidth) {
            mKeyStrokeWidth = getContext().getResources().getDimension(keyStrokeWidth);
            return this;
        }

        @Dimension
        public Builder setKeyStrokeWidth(float keyStrokeWidth) {
            mKeyStrokeWidth = keyStrokeWidth;
            return this;
        }

        @ColorInt
        public int getKeyStrokeColor() {
            return mKeyStrokeColor;
        }

        public Builder setKeyStrokeColor(@ColorInt int keyStrokeColor) {
            mKeyStrokeColor = keyStrokeColor;
            return this;
        }

        public Builder setKeyStrokeColorResource(@ColorRes int keyStrokeColor) {
            mKeyStrokeColor = getContext().getResources().getColor(keyStrokeColor);
            return this;
        }

        @ColorInt
        public int getKeyTextColor() {
            return mKeyTextColor;
        }

        public Builder setKeyTextColor(@ColorInt int keyTextColor) {
            mKeyTextColor = keyTextColor;
            return this;
        }

        public Builder setKeyTextColorResource(@ColorRes int keyTextColor) {
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

            //Ripple paint
            mClickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mClickPaint.setStyle(Paint.Style.STROKE);
            mClickPaint.setColor(Utils.makeColorDark(mKeyStrokeColor));
            mClickPaint.setStrokeWidth(mKeyStrokeWidth);
            return this;
        }

        @Override
        protected void setDefaults(@NonNull Context context) {
            mKeyTextColor = context.getResources().getColor(R.color.lib_key_default_color);
            mKeyStrokeColor = context.getResources().getColor(R.color.lib_key_background_color);
            mKeyTextSize = context.getResources().getDimension(R.dimen.lib_key_text_size);
            mKeyStrokeWidth = context.getResources().getDimension(R.dimen.lib_key_stroke_width);
            mKeyPadding = getContext().getResources().getDimension(R.dimen.lib_key_padding);
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
        protected Paint getClickPaint() {
            return mClickPaint;
        }

        @NonNull
        @Override
        public SquareKey getKey(@NonNull String digit, @NonNull Rect bound) {
            return new SquareKey(super.getPinView(), digit, bound, this);
        }
    }
}
