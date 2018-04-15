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

package com.kevalpatel.passcodeview.patternCells;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.PatternView;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;

/**
 * Created by Keval on 06-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class DotPatternCell extends PatternCell {

    @NonNull
    private final DotPatternCell.Builder mBuilder;

    private final float mTouchRadius;

    @NonNull
    private final Paint mCellPaint;             //Empty indicator color

    @NonNull
    private final Paint mErrorCellPaint;             //Error indicator color

    private boolean isDisplayError;

    private DotPatternCell(@NonNull final DotPatternCell.Builder builder,
                           @NonNull final Rect bound,
                           @NonNull final PatternPoint point) {
        super(builder, bound, point);
        mBuilder = builder;

        mTouchRadius = mBuilder.mRadius < getContext().getResources().getDimension(R.dimen.lib_min_touch_radius)
                ? mBuilder.mRadius + 20 : mBuilder.mRadius;

        //Set empty dot paint
        mCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCellPaint.setColor(builder.mCellColor);

        //Set filled dot paint
        mErrorCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorCellPaint.setColor(Color.RED);
    }

    /**
     * Draw the indicator.
     *
     * @param canvas Canvas of {@link PinView}.
     */
    @Override
    public void draw(@NonNull final Canvas canvas) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                mBuilder.mRadius,
                isDisplayError ? mErrorCellPaint : mCellPaint);
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

    @Override
    public boolean isIndicatorTouched(final float touchX, final float touchY) {
        //Check if the click is between the width bounds
        //noinspection SimplifiableIfStatement
        if (touchX > getBound().exactCenterX() - mTouchRadius
                && touchX < getBound().exactCenterX() + mTouchRadius) {

            //Check if the click is between the height bounds
            return touchY > getBound().exactCenterY() - mTouchRadius
                    && touchY < getBound().exactCenterY() + mTouchRadius;
        }
        return false;
    }

    public static class Builder extends PatternCell.Builder {
        @ColorInt
        private int mCellColor;              //Empty indicator stroke color
        @Dimension
        private float mRadius;


        public Builder(@NonNull final PatternView patternView) {
            super(patternView);
            setDefaults();
        }

        private void setDefaults() {
            mRadius = getContext().getResources().getDimension(R.dimen.lib_dot_cell_radius_radius);
            mCellColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @NonNull
        public DotPatternCell.Builder setCellColor(@ColorInt final int normalColor) {
            mCellColor = normalColor;
            return this;
        }

        @NonNull
        public DotPatternCell.Builder setCellColorResource(@ColorRes final int indicatorStrokeColor) {
            mCellColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @NonNull
        public DotPatternCell.Builder setRadius(@DimenRes final int indicatorRadius) {
            mRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public DotPatternCell.Builder setRadius(@Dimension final float radius) {
            mRadius = radius;
            return this;
        }

        @NonNull
        @Override
        public DotPatternCell buildInternal(@NonNull final Rect bound, @NonNull final PatternPoint point) {
            return new DotPatternCell(this, bound, point);
        }
    }
}
