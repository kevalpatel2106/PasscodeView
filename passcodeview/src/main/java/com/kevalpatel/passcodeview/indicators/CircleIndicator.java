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

package com.kevalpatel.passcodeview.indicators;

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
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class CircleIndicator extends Indicator {
    private static final long ERROR_ANIMATION_DURATION = 400;

    @NonNull
    private final Rect mBounds;
    @NonNull
    private final Builder mBuilder;
    private boolean isDisplayError;

    @NonNull
    private final Paint mEmptyIndicatorPaint;
    @NonNull
    private final Paint mSolidIndicatorPaint;
    @NonNull
    private final Paint mErrorIndicatorPaint;

    private CircleIndicator(@NonNull final CircleIndicator.Builder builder,
                            @NonNull final Rect bound) {
        super(builder, bound);

        mBounds = bound;
        mBuilder = builder;

        //Set empty dot paint
        mEmptyIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEmptyIndicatorPaint.setStyle(Paint.Style.STROKE);
        mEmptyIndicatorPaint.setColor(mBuilder.mIndicatorStrokeColor);
        mEmptyIndicatorPaint.setStrokeWidth(mBuilder.mIndicatorStrokeWidth);

        //Set filled dot paint
        mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidIndicatorPaint.setColor(mBuilder.mIndicatorFilledColor);

        //Set filled dot paint
        mErrorIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorIndicatorPaint.setColor(Color.RED);
    }

    /**
     * Draw the indicator.
     *
     * @param canvas   Canvas of {@link PinView}.
     * @param isFilled True if to display filled indicator.
     */
    @Override
    public void draw(@NonNull final Canvas canvas, final boolean isFilled) {
        canvas.drawCircle(mBounds.exactCenterX(),
                mBounds.exactCenterY(),
                mBuilder.mIndicatorRadius,
                isDisplayError ? mErrorIndicatorPaint : isFilled ? mSolidIndicatorPaint : mEmptyIndicatorPaint);
    }

    @Override
    public void onAuthFailed() {
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isDisplayError = false;
                getRootView().invalidate();
            }
        }, ERROR_ANIMATION_DURATION);
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


        public Builder(@NonNull PinView pinView) {
            super(pinView);

            //Set defaults
            mIndicatorRadius = getContext().getResources().getDimension(R.dimen.lib_indicator_radius);
            mIndicatorStrokeWidth = getContext().getResources().getDimension(R.dimen.lib_indicator_stroke_width);
            mIndicatorFilledColor = getContext().getResources().getColor(R.color.lib_indicator_filled_color);
            mIndicatorStrokeColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeColor(@ColorInt final int indicatorStrokeColor) {
            mIndicatorStrokeColor = indicatorStrokeColor;
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeColorResource(@ColorRes final int indicatorStrokeColor) {
            mIndicatorStrokeColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorFilledColor(@ColorInt final int indicatorFilledColor) {
            mIndicatorFilledColor = indicatorFilledColor;
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorFilledColorResource(@ColorRes final int indicatorFilledColor) {
            mIndicatorFilledColor = getContext().getResources().getColor(indicatorFilledColor);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorRadius(@DimenRes final int indicatorRadius) {
            mIndicatorRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorRadius(@Dimension final float indicatorRadius) {
            mIndicatorRadius = indicatorRadius;
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeWidth(@DimenRes final int indicatorStrokeWidth) {
            mIndicatorStrokeWidth = getContext().getResources().getDimension(indicatorStrokeWidth);
            return this;
        }

        @NonNull
        public CircleIndicator.Builder setIndicatorStrokeWidth(@Dimension final float indicatorStrokeWidth) {
            mIndicatorStrokeWidth = indicatorStrokeWidth;
            return this;
        }

        @Dimension
        @Override
        public float getIndicatorWidth() {
            return mIndicatorRadius;
        }

        @NonNull
        @Override
        public Indicator buildInternal(@NonNull final Rect bound) {
            return new CircleIndicator(this, bound);
        }
    }
}
