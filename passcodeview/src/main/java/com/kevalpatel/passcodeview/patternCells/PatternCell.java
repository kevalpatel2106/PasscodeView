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
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class PatternCell {

    @NonNull
    private final BasePasscodeView mPasscodeView;

    @NonNull
    private final Rect mBound;

    @NonNull
    private final PatternPoint mPoint;

    protected PatternCell(@NonNull final PatternCell.Builder builder,
                          @NonNull final Rect bound,
                          @NonNull final PatternPoint location) {
        mPasscodeView = builder.mBasePasscodeView;
        mBound = bound;
        mPoint = location;
    }

    protected final BasePasscodeView getRootView() {
        return mPasscodeView;
    }

    protected final Context getContext() {
        return mPasscodeView.getContext();
    }

    @NonNull
    public PatternPoint getPoint() {
        return mPoint;
    }

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    public float getCenterX() {
        return mBound.exactCenterX();
    }

    public float getCenterY() {
        return mBound.exactCenterY();
    }

    public abstract void draw(@NonNull final Canvas canvas);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    public abstract boolean isIndicatorTouched(final float touchX, final float touchY);


    public static abstract class Builder {

        @NonNull
        private final BasePasscodeView mBasePasscodeView;

        public Builder(@NonNull final BasePasscodeView passcodeView) {
            mBasePasscodeView = passcodeView;
        }

        @NonNull
        protected final BasePasscodeView getRootView() {
            return mBasePasscodeView;
        }

        @NonNull
        protected final Context getContext() {
            return mBasePasscodeView.getContext();
        }

        @NonNull
        public abstract PatternCell buildInternal(@NonNull final Rect bound,
                                                  @NonNull final PatternPoint point);
    }
}
