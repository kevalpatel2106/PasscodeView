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

package com.kevalpatel.passcodeview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.indicators.Indicator;

import java.util.ArrayList;

/**
 * Created by Keval Patel on 09/04/17.
 * This {@link Box} is to display title and passcode indicator. The number of the passcode indicator
 * will depend on the length of the passcode indicator.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

final class BoxTitleIndicator extends Box {
    private int mPintCodeLength;

    @ColorInt
    private int mTitleColor;                        //Title text color
    private String mTitle;                          //Title color
    private Paint mTitlePaint;                      //Solid indicator color

    private String mPinTyped = "";                  //Characters of the PIN typed. Whenever user types pin update it using onValueEntered().
    private ArrayList<Indicator> mIndicators;

    private Rect mDotsIndicatorBound;
    private Indicator.Builder mIndicatorBuilder;

    BoxTitleIndicator(@NonNull PinView view) {
        super(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    void setDefaults() {
        mTitle = Constants.DEF_TITLE_TEXT;
        mTitleColor = getContext().getResources().getColor(R.color.lib_key_default_color);
    }

    @Override
    void onAuthenticationFail() {
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
    void onAuthenticationSuccess() {
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
    void draw(@NonNull Canvas canvas) {
        if (mIndicatorBuilder == null)
            throw new NullPointerException("Build indicator before using it.");

        canvas.drawText(mTitle,
                mDotsIndicatorBound.exactCenterX(),
                mDotsIndicatorBound.top - (int) getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin),
                mTitlePaint);

        for (int i = 0; i < mPintCodeLength; i++)
            mIndicators.get(i).draw(canvas, i < mPinTyped.length());
    }

    /**
     * |------------------------|=|
     * |                        | |
     * |                        | |
     * |         TITLE          | | => 2 * {@link com.kevalpatel.passcodeview.R.dimen#lib_divider_vertical_margin} px up from the bottom of the indicators.
     * |   |===============|    | |
     * |   |   INDICATORS  |    | |
     * |   |===============|    | | => 2 * {@link com.kevalpatel.passcodeview.R.dimen#lib_divider_vertical_margin} px up from the bottom key board
     * |------------------------|=| => {@link BoxKeypad#KEY_BOARD_TOP_WEIGHT} of the total height.
     * |                        | |
     * |                        | |
     * |                        | | => Keypad height. ({@link BoxKeypad#measure(Rect)})
     * |                        | |
     * |                        | |
     * |------------------------|=| => {@link BoxKeypad#KEY_BOARD_BOTTOM_WEIGHT} of the total weight if the fingerprint is available. Else it touches to the bottom of the main view.
     * |                        | | => Section for fingerprint. If the fingerprint is enabled. Otherwise keyboard streaches to the bottom of the root view.
     * |------------------------|=|
     * Don't change until you know what you are doing. :-)
     *
     * @param rootViewBounds {@link Rect} bounds of the main view.
     */
    @Override
    void measure(@NonNull Rect rootViewBounds) {

        int indicatorWidth = (int) (mIndicatorBuilder.getIndicatorWidth() + 2 * getContext().getResources().getDimension(R.dimen.lib_indicator_padding));
        int totalSpace = indicatorWidth * mPintCodeLength;

        //Dots indicator
        mDotsIndicatorBound = new Rect();
        mDotsIndicatorBound.left = (rootViewBounds.width() - totalSpace) / 2;
        mDotsIndicatorBound.right = mDotsIndicatorBound.left + totalSpace;
        mDotsIndicatorBound.bottom = rootViewBounds.top
                + (int) (rootViewBounds.height() * BoxKeypad.KEY_BOARD_TOP_WEIGHT
                - 2 * getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mDotsIndicatorBound.top = mDotsIndicatorBound.bottom - indicatorWidth;

        mIndicators = new ArrayList<>();
        for (int i = 0; i < mPintCodeLength; i++) {
            Rect rect = new Rect();
            rect.left = mDotsIndicatorBound.left + i * indicatorWidth;
            rect.right = rect.left + indicatorWidth;
            rect.top = mDotsIndicatorBound.top;
            rect.bottom = mDotsIndicatorBound.bottom;
            mIndicators.add(mIndicatorBuilder.getIndicator(rect));
        }
    }

    @Override
    void preparePaint() {
        //Set title paint
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(getContext().getResources().getDimension(R.dimen.lib_title_text_size));
    }

    @Override
    void onValueEntered(@NonNull String newValue) {
        mPinTyped = newValue;
    }

    void setPintCodeLength(int pintCodeLength) {
        this.mPintCodeLength = pintCodeLength;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        this.mTitle = title;
    }

    int getTitleColor() {
        return mTitleColor;
    }

    void setTitleColor(int titleColor) {
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
