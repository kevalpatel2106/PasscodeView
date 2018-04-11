/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.patternCells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.BasePasscodeView;
import com.kevalpatel.passcodeview.PatternView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class PatternCell {

    private PatternView mPatternView;

    private Rect mBound;
    private Point mPoint;

    private PatternCell() {
    }

    protected PatternCell(@NonNull PatternView pinView,
                          @NonNull Rect bound,
                          @NonNull PatternCell.Builder builder,
                          Point location) {
        this.mPatternView = pinView;
        mBound = bound;
        mPoint = location;
    }

    protected final BasePasscodeView getRootView() {
        return mPatternView;
    }

    protected final Context getContext() {
        return mPatternView.getContext();
    }

    public abstract void draw(@NonNull Canvas canvas);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    public float getCenterX() {
        return getBound().exactCenterX();
    }

    public float getCenterY() {
        return getBound().exactCenterY();
    }

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    public abstract boolean isIndicatorTouched(float touchX, float touchY);

    public Point getPoint() {
        return mPoint;
    }

    public static abstract class Builder {

        private PatternView mPatternView;

        private Builder() {
        }

        public Builder(@NonNull PatternView pinView) {
            mPatternView = pinView;
            setDefaults(pinView.getContext());
        }

        @NonNull
        protected final PatternView getRootView() {
            return mPatternView;
        }

        @NonNull
        protected final Context getContext() {
            return mPatternView.getContext();
        }

        @Dimension
        public abstract float getCellRadius();

        public abstract PatternCell.Builder build();

        protected abstract void setDefaults(@NonNull Context context);

        public abstract PatternCell getCell(@NonNull Rect bound, Point point);
    }
}
