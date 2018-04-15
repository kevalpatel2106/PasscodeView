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

public final class DotIndicator extends Indicator {
    private static final long ERROR_ANIMATION_DURATION = 400;
    @NonNull
    private final Paint mEmptyIndicatorPaint;             //Empty indicator color

    @NonNull
    private final Builder mBuilder;
    @NonNull
    private final Paint mSolidIndicatorPaint;             //Solid indicator color
    @NonNull
    private final Paint mErrorIndicatorPaint;             //Error indicator color
    private boolean isDisplayError;

    private DotIndicator(@NonNull final DotIndicator.Builder builder,
                         @NonNull final Rect bound) {
        super(builder, bound);
        mBuilder = builder;

        //Set empty dot paint
        mEmptyIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEmptyIndicatorPaint.setColor(builder.mEmptyIndicatorColor);

        //Set filled dot paint
        mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidIndicatorPaint.setColor(builder.mIndicatorFilledColor);

        //Set filled dot paint
        mErrorIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorIndicatorPaint.setColor(Color.RED);
    }

    /**
     * Draw the indicator.
     *
     * @param canvas     Canvas of {@link PinView}.
     * @param isSelected True if to display selectedL indicator.
     */
    @Override
    public void draw(@NonNull final Canvas canvas,
                     final boolean isSelected) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                mBuilder.mIndicatorRadius,
                isDisplayError ? mErrorIndicatorPaint : isSelected ? mSolidIndicatorPaint : mEmptyIndicatorPaint);
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
        private int mEmptyIndicatorColor;              //Empty indicator stroke color
        @ColorInt
        private int mIndicatorFilledColor;              //Filled indicator stroke color
        @Dimension
        private float mIndicatorRadius;

        public Builder(@NonNull final PinView pinView) {
            super(pinView);

            mIndicatorRadius = getContext().getResources().getDimension(R.dimen.lib_indicator_radius);
            mIndicatorFilledColor = getContext().getResources().getColor(R.color.lib_indicator_filled_color);
            mEmptyIndicatorColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @NonNull
        public DotIndicator.Builder setIndicatorEmptyColor(@ColorInt final int indicatorStrokeColor) {
            mEmptyIndicatorColor = indicatorStrokeColor;
            return this;
        }

        @NonNull
        public DotIndicator.Builder setIndicatorEmptyColorResource(@ColorRes final int indicatorStrokeColor) {
            mEmptyIndicatorColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @NonNull
        public DotIndicator.Builder setIndicatorFilledColor(@ColorInt final int indicatorFilledColor) {
            mIndicatorFilledColor = indicatorFilledColor;
            return this;
        }

        @NonNull
        public DotIndicator.Builder setIndicatorFilledColorResource(@ColorRes final int indicatorFilledColor) {
            mIndicatorFilledColor = getContext().getResources().getColor(indicatorFilledColor);
            return this;
        }

        @NonNull
        public DotIndicator.Builder setIndicatorRadius(@DimenRes final int indicatorRadius) {
            mIndicatorRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public DotIndicator.Builder setIndicatorRadius(@Dimension final float indicatorRadius) {
            mIndicatorRadius = indicatorRadius;
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
            return new DotIndicator(this, bound);
        }
    }
}
