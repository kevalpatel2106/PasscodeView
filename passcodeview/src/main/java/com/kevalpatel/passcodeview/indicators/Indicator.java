/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.indicators;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.internal.BasePasscodeView;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class Indicator {
    @NonNull
    private final Rect mBound;

    @NonNull
    private final Indicator.Builder mBuilder;

    Indicator(@NonNull final Indicator.Builder builder,
              @NonNull final Rect bound) {
        mBound = bound;
        mBuilder = builder;
    }

    @NonNull
    public Rect getBound() {
        return mBound;
    }

    @NonNull
    protected final BasePasscodeView getRootView() {
        return mBuilder.getRootView();
    }

    @NonNull
    protected final Context getContext() {
        return mBuilder.getContext();
    }

    public abstract void draw(@NonNull final Canvas canvas, final boolean isFilled);

    public abstract void onAuthFailed();

    public abstract void onAuthSuccess();

    public static abstract class Builder {

        @NonNull
        private final BasePasscodeView mPasscodeView;

        public Builder(@NonNull final BasePasscodeView passcodeView) {
            mPasscodeView = passcodeView;
        }

        @NonNull
        protected final BasePasscodeView getRootView() {
            return mPasscodeView;
        }

        @Dimension
        public abstract float getIndicatorWidth();

        @NonNull
        protected final Context getContext() {
            return mPasscodeView.getContext();
        }

        public abstract Indicator buildInternal(@NonNull final Rect bound);
    }
}
