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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class CircleIndicator extends Indicator {

    @NonNull
    private final Rect mBounds;
    @NonNull
    private final Builder mBuilder;
    private boolean isDisplayError;

    private CircleIndicator(@NonNull PinView pinView,
                            @NonNull Rect bound,
                            @NonNull CircleIndicator.Builder builder) {
        super(pinView, bound, builder);

        mBounds = bound;
        mBuilder = builder;
    }

    /**
     * Draw the indicator.
     *
     * @param canvas   Canvas of {@link PinView}.
     * @param isFilled True if to display filled indicator.
     */
    @Override
    public void draw(@NonNull Canvas canvas, boolean isFilled) {
        canvas.drawCircle(mBounds.exactCenterX(),
                mBounds.exactCenterY(),
                mBuilder.getIndicatorRadius(),
                isDisplayError ? mBuilder.getErrorIndicatorPaint() :
                        isFilled ? mBuilder.getSolidIndicatorPaint() : mBuilder.getEmptyIndicatorPaint());
    }

    @Override
    public void onAuthFailed() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDisplayError = false;
                getRootView().invalidate();
            }
        }, 400);
        isDisplayError = true;
    }

    @Override
    public void onAuthSuccess() {
        //Do nothing
    }

    public static class Builder extends Indicator.Builder {
        @ColorInt
        private int mIndicatorStrokeColor;              //Empty indicator stroke color
        @ColorInt
        private int mIndicatorFilledColor;              //Filled indicator stroke color
        @Dimension
        private float mIndicatorRadius;
        @Dimension
        private float mIndicatorStrokeWidth;

        private Paint mEmptyIndicatorPaint;             //Empty indicator color
        private Paint mSolidIndicatorPaint;             //Solid indicator color
        private Paint mErrorIndicatorPaint;             //Error indicator color


        public Builder(@NonNull PinView pinView) {
            super(pinView);
        }

        @Dimension
        @Override
        public float getIndicatorWidth() {
            return mIndicatorRadius * 2;
        }

        @Override
        public CircleIndicator.Builder build() {

            //Set empty dot paint
            mEmptyIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mEmptyIndicatorPaint.setStyle(Paint.Style.STROKE);
            mEmptyIndicatorPaint.setColor(mIndicatorStrokeColor);
            mEmptyIndicatorPaint.setStrokeWidth(mIndicatorStrokeWidth);

            //Set filled dot paint
            mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mSolidIndicatorPaint.setColor(mIndicatorFilledColor);

            //Set filled dot paint
            mErrorIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mErrorIndicatorPaint.setColor(Color.RED);
            return this;
        }

        @Override
        protected void setDefaults(@NonNull Context context) {
            mIndicatorRadius = getContext().getResources().getDimension(R.dimen.lib_indicator_radius);
            mIndicatorStrokeWidth = getContext().getResources().getDimension(R.dimen.lib_indicator_stroke_width);
            mIndicatorFilledColor = getContext().getResources().getColor(R.color.lib_indicator_filled_color);
            mIndicatorStrokeColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @Override
        public Indicator getIndicator(@NonNull Rect bound) {
            return new CircleIndicator(getRootView(), bound, this);
        }

        @ColorInt
        public int getIndicatorStrokeColor() {
            return mIndicatorStrokeColor;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeColor(@ColorInt int indicatorStrokeColor) {
            mIndicatorStrokeColor = indicatorStrokeColor;
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeColorResource(@ColorRes int indicatorStrokeColor) {
            mIndicatorStrokeColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @ColorInt
        public int getIndicatorFilledColor() {
            return mIndicatorFilledColor;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorFilledColor(@ColorInt int indicatorFilledColor) {
            mIndicatorFilledColor = indicatorFilledColor;
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorFilledColorResource(@ColorRes int indicatorFilledColor) {
            mIndicatorFilledColor = getContext().getResources().getColor(indicatorFilledColor);
            return this;
        }

        @Dimension
        public float getIndicatorRadius() {
            return mIndicatorRadius;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorRadius(@DimenRes int indicatorRadius) {
            mIndicatorRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorRadius(@Dimension float indicatorRadius) {
            mIndicatorRadius = indicatorRadius;
            return this;
        }

        @Dimension
        public float getIndicatorStrokeWidth() {
            return mIndicatorStrokeWidth;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeWidth(@DimenRes int indicatorStrokeWidth) {
            mIndicatorStrokeWidth = getContext().getResources().getDimension(indicatorStrokeWidth);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeWidth(@Dimension float indicatorStrokeWidth) {
            mIndicatorStrokeWidth = indicatorStrokeWidth;
            return this;
        }

        @NonNull
        public Paint getEmptyIndicatorPaint() {
            return mEmptyIndicatorPaint;
        }

        @NonNull
        public Paint getSolidIndicatorPaint() {
            return mSolidIndicatorPaint;
        }

        @NonNull
        public Paint getErrorIndicatorPaint() {
            return mErrorIndicatorPaint;
        }
    }
}
