/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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

import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class CirclePatternCell extends PatternCell {

    @NonNull
    private final Builder mBuilder;
    private boolean isDisplayError;

    private float mTouchRadius;

    @NonNull
    private final Paint mCellPaint;             //Empty indicator color

    @NonNull
    private final Paint mErrorPaint;             //Error indicator color

    private CirclePatternCell(@NonNull final CirclePatternCell.Builder builder,
                              @NonNull final Rect bound,
                              @NonNull final PatternPoint point) {
        super(builder, bound, point);
        mBuilder = builder;

        mTouchRadius = mBuilder.mRadius < getContext().getResources().getDimension(R.dimen.lib_min_touch_radius)
                ? mBuilder.mRadius + 20 : mBuilder.mRadius;

        //Set empty dot paint
        mCellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCellPaint.setStyle(Paint.Style.STROKE);
        mCellPaint.setColor(builder.mNormalColor);
        mCellPaint.setStrokeWidth(builder.mStrokeWidth);

        //Set filled dot paint
        mErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorPaint.setColor(Color.RED);
    }

    /**
     * Draw the indicator.
     *
     * @param canvas Canvas of {@link PinView}.
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                mBuilder.mRadius,
                isDisplayError ? mErrorPaint : mCellPaint);
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
    public boolean isIndicatorTouched(float touchX, float touchY) {
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
        private int mNormalColor;              //Empty indicator stroke color
        @Dimension
        private float mRadius;
        @Dimension
        private float mStrokeWidth;

        public Builder(@NonNull final BasePasscodeView basePasscodeView) {
            super(basePasscodeView);
            setDefaults();
        }

        private void setDefaults() {
            mRadius = getContext().getResources().getDimension(R.dimen.lib_indicator_radius);
            mStrokeWidth = getContext().getResources().getDimension(R.dimen.lib_indicator_stroke_width);
            mNormalColor = getContext().getResources().getColor(R.color.lib_indicator_stroke_color);
        }

        @NonNull
        public CirclePatternCell.Builder setNormalColor(@ColorInt final int normalColor) {
            mNormalColor = normalColor;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setCellColorResource(@ColorRes final int indicatorStrokeColor) {
            mNormalColor = getContext().getResources().getColor(indicatorStrokeColor);
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setRadius(@Dimension final float radius) {
            mRadius = radius;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setRadius(@DimenRes final int indicatorRadius) {
            mRadius = getContext().getResources().getDimension(indicatorRadius);
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setStrokeWidth(@Dimension final float strokeWidth) {
            mStrokeWidth = strokeWidth;
            return this;
        }

        @NonNull
        public CirclePatternCell.Builder setStrokeWidth(@DimenRes final int indicatorStrokeWidth) {
            mStrokeWidth = getContext().getResources().getDimension(indicatorStrokeWidth);
            return this;
        }

        @NonNull
        @Override
        public PatternCell buildInternal(@NonNull Rect bound, @NonNull PatternPoint point) {
            return new CirclePatternCell(this, bound, point);
        }
    }
}
