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

package com.kevalpatel.passcodeview.internal.box;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;
import com.kevalpatel.passcodeview.indicators.Indicator;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;
import com.kevalpatel.passcodeview.internal.Constants;

import java.util.ArrayList;

/**
 * Created by Keval Patel on 09/04/17.
 * This {@link Box} is to display title and passcode indicator. The number of the passcode indicator
 * will depend on the length of the passcode indicator.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class BoxTitleIndicator extends Box {
    private static final String DEF_TITLE_TEXT = "Enter PIN";

    private int mPinLength;
    private int mTypedPinLength;

    @ColorInt
    private int mTitleColor;                        //Title text color
    private String mTitle;                          //Title color

    private Paint mTitlePaint;                      //Solid indicator color

    private ArrayList<Indicator> mIndicators;

    private Rect mDotsIndicatorBound;
    private Indicator.Builder mIndicatorBuilder;

    public BoxTitleIndicator(@NonNull BasePasscodeView view) {
        super(view);
    }

    @Override
    public void init() {
        //TODO init
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setDefaults() {
        mTitle = DEF_TITLE_TEXT;
        mTitleColor = getContext().getResources().getColor(R.color.lib_key_default_color);
    }

    @Override
    public void parseTypeArr(@NonNull AttributeSet typedArray) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(typedArray, R.styleable.PinView, 0, 0);
        try {
            //Parse title params
            mTitle = a.hasValue(R.styleable.PinView_pin_titleText) ?
                    a.getString(R.styleable.PinView_pin_titleText) : DEF_TITLE_TEXT;
            mTitleColor = a.getColor(R.styleable.PinView_pin_titleTextColor,
                    Utils.getColorCompat(getContext(), R.color.lib_key_default_color));
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onAuthenticationFail() {
        //Set indicator to error
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Indicator indicator : mIndicators) indicator.onAuthFailed();
                getRootView().invalidate();
            }
        }, 100);
    }

    @Override
    public void onAuthenticationSuccess() {
        //Set indicator to success
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Indicator indicator : mIndicators) indicator.onAuthSuccess();
                getRootView().invalidate();
            }
        }, 100);
    }

    @Override
    public void reset() {
        //TODO Reset
    }

    @Override
    public void drawView(@NonNull Canvas canvas) {
        if (mIndicatorBuilder == null)
            throw new NullPointerException("Build indicator before using it.");

        canvas.drawText(mTitle,
                mDotsIndicatorBound.exactCenterX(),
                mDotsIndicatorBound.top - (int) getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin),
                mTitlePaint);

        for (int i = 0; i < mPinLength; i++)
            mIndicators.get(i).draw(canvas, i < mTypedPinLength);
    }

    /**
     * |------------------------|=|
     * |                        | |
     * |                        | |
     * |         TITLE          | | => 2 * {@link com.kevalpatel.passcodeview.R.dimen#lib_divider_vertical_margin} px up from the bottom of the indicators.
     * |   |===============|    | |
     * |   |   INDICATORS  |    | |
     * |   |===============|    | | => 2 * {@link com.kevalpatel.passcodeview.R.dimen#lib_divider_vertical_margin} px up from the bottom key board
     * |------------------------|=| => {@link Constants#KEY_BOARD_TOP_WEIGHT} of the total height.
     * |                        | |
     * |                        | |
     * |                        | | => Keypad height. ({@link BoxKeypad#measureView(Rect)})
     * |                        | |
     * |                        | |
     * |------------------------|=| => {@link Constants#KEY_BOARD_BOTTOM_WEIGHT} of the total weight if the fingerprint is available. Else it touches to the bottom of the main view.
     * |                        | | => Section for fingerprint. If the fingerprint is enabled. Otherwise keyboard streaches to the bottom of the root view.
     * |------------------------|=|
     * Don't change until you know what you are doing. :-)
     *
     * @param rootViewBounds {@link Rect} bounds of the main view.
     */
    @Override
    public void measureView(@NonNull Rect rootViewBounds) {
        int indicatorWidth = (int) (mIndicatorBuilder.getIndicatorWidth() + 2 * getContext().getResources().getDimension(R.dimen.lib_indicator_padding));
        int totalSpace = indicatorWidth * mPinLength;

        //Dots indicator
        mDotsIndicatorBound = new Rect();
        mDotsIndicatorBound.left = (rootViewBounds.width() - totalSpace) / 2;
        mDotsIndicatorBound.right = mDotsIndicatorBound.left + totalSpace;
        mDotsIndicatorBound.bottom = rootViewBounds.top
                + (int) (rootViewBounds.height() * Constants.KEY_BOARD_TOP_WEIGHT
                - 2 * getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mDotsIndicatorBound.top = mDotsIndicatorBound.bottom - indicatorWidth;

        mIndicators = new ArrayList<>();
        for (int i = 0; i < mPinLength; i++) {
            Rect rect = new Rect();
            rect.left = mDotsIndicatorBound.left + i * indicatorWidth;
            rect.right = rect.left + indicatorWidth;
            rect.top = mDotsIndicatorBound.top;
            rect.bottom = mDotsIndicatorBound.bottom;
            mIndicators.add(mIndicatorBuilder.getIndicator(rect));
        }
    }

    @Override
    public void preparePaint() {
        //Set title paint
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(getContext().getResources().getDimension(R.dimen.lib_title_text_size));
    }

    public void onPinDigitEntered(int newLength) {
        mTypedPinLength = newLength;
    }

    public void setPinLength(int pinLength) {
        mPinLength = pinLength;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(@ColorInt int titleColor) {
        this.mTitleColor = titleColor;
        preparePaint();
    }

    public Indicator.Builder getIndicatorBuilder() {
        return mIndicatorBuilder;
    }

    public void setIndicatorBuilder(@NonNull Indicator.Builder mIndicatorBuilder) {
        this.mIndicatorBuilder = mIndicatorBuilder;
    }
}
