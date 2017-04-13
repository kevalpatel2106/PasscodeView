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

import com.kevalpatel.passcodeview.KeyNamesBuilder;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

@SuppressWarnings("ALL")
public final class RectKey extends Key {
    private final Rect mBounds;                         //RoundKey bound.
    private final PinView mPinView;                           //Pin view
    private final Builder mBuilder;

    /**
     * Public constructor.
     *
     * @param pinView {@link PinView}
     * @param digit   title of the key. (-1 for the backspace key)
     * @param bounds  {@link Rect} bound.
     */
    private RectKey(@NonNull PinView pinView,
                    @NonNull String digit,
                    @NonNull Rect bounds,
                    @NonNull RectKey.Builder builder) {
        super(pinView, digit, bounds, builder);

        mBounds = bounds;
        mPinView = pinView;
        mBuilder = builder;
    }

    /**
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    @Override
    public void playClickAnimation() {
        //TODO Click animation
    }

    /**
     * Show animation indicated invalid pin code
     */
    @Override
    public void onAuthFail() {
        ValueAnimator goLeftAnimator = ValueAnimator.ofInt(0, 10);
        goLeftAnimator.setInterpolator(new CycleInterpolator(2));
        goLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBounds.left += (int) animation.getAnimatedValue();
                mBounds.right += (int) animation.getAnimatedValue();
                mPinView.invalidate();
            }
        });
        goLeftAnimator.start();
    }

    /**
     * Draw the key of canvas.
     * Don't change until you know what you are doing. :-)
     *
     * @param canvas       canvas of the view o which key will be drawn
     * @param keyPaint     RoundKey background paint
     * @param keyTextPaint RoundKey text paint
     */
    @Override
    public void draw(@NonNull Canvas canvas) {

        //Draw circle background
        canvas.drawRect(mBounds.left + mBuilder.getKeyPadding(),
                mBounds.top + mBuilder.getKeyPadding(),
                mBounds.right - mBuilder.getKeyPadding(),
                mBounds.bottom - mBuilder.getKeyPadding(),
                mBuilder.getKeyPaint());

        if (getDigit().equals(KeyNamesBuilder.BACKSPACE_TITLE)) {  //Backspace key
            Drawable d = mPinView.getContext().getResources().getDrawable(R.drawable.ic_back_space);
            d.setBounds((int) (mBounds.exactCenterX() - Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() - Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterX() + Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() + Math.min(mBounds.height(), mBounds.width()) / 3));
            d.setColorFilter(new PorterDuffColorFilter(mBuilder.getKeyTextColor(), PorterDuff.Mode.SRC_ATOP));
            d.draw(canvas);
        } else {
            //Draw key text
            canvas.drawText(getDigit() + "",                //Text to display on key
                    mBounds.exactCenterX(),                 //Set start point at center width of key
                    mBounds.exactCenterY() - (mBuilder.getKeyTextPaint().descent() + mBuilder.getKeyTextPaint().ascent()) / 2,    //center height of key - text height/2
                    mBuilder.getKeyTextPaint());
        }
    }

    /**
     * Check if the key is pressed or not? It will check if the touch x & y coordinates are inside
     * thw key bound or not?
     *
     * @param downEventX ACTION_DOWN event X coordinate
     * @param downEventY ACTION_DOWN event Y coordinate
     * @param upEventX   ACTION_UP event X coordinate
     * @param upEventY   ACTION_UP event Y coordinate
     * @return true if the key is pressed else false.
     */
    @Override
    public boolean checkKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY) {
        if (getDigit().isEmpty()) return false;  //Empty key

        //Check if the click is between the width bounds
        if (downEventX > mBounds.left && downEventX < mBounds.right) {

            //Check if the click is between the height bounds
            if (downEventY > mBounds.top && downEventY < mBounds.bottom) {

                //Check if the click is between the width bounds
                if (upEventX > mBounds.left && upEventX < mBounds.right) {

                    //Check if the click is between the height bounds
                    if (upEventY > mBounds.top && upEventY < mBounds.bottom) {

                        playClickAnimation();
                        return true;
                    }
                }
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
        @Override
        public RectKey getKey(@NonNull String digit, @NonNull Rect bound) {
            return new RectKey(super.getPinView(), digit, bound, this);
        }
    }
}
