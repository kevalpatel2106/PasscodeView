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
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kevalpatel.passcodeview.indicators.Indicator;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

final class BoxPattern extends Box {
    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.
    private ArrayList<Indicator> mIndicators;
    private Rect mPatternBoxBound = new Rect();
    private Indicator.Builder mIndicatorBuilder;

    /**
     * Public constructor
     *
     * @param passcodeView {@link PasscodeView} in which box will be displayed.
     */
    BoxPattern(@NonNull PasscodeView passcodeView) {
        super(passcodeView);
    }

    /**
     * Measure and display the keypad box.
     * |------------------------|=|
     * |                        | |
     * |                        | | => The title and the indicator. ({@link BoxTitleIndicator#measure(Rect)})
     * |                        | |
     * |                        | |
     * |------------------------|=| => {@link Constants#KEY_BOARD_TOP_WEIGHT} of the total height.
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | | => Keypad height.
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |------------------------|=|=> {@link Constants#KEY_BOARD_BOTTOM_WEIGHT} of the total weight if the fingerprint is available. Else it touches to the bottom of the main view.
     * |                        | |
     * |                        | |=> Section for fingerprint. If the fingerprint is enabled. Otherwise keyboard streaches to the bottom of the root view.
     * |------------------------|=|
     * Don't change until you know what you are doing. :-)
     *
     * @param rootViewBound bound of the main view.
     */
    @Override
    void measure(@NonNull Rect rootViewBound) {
        mPatternBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mPatternBoxBound.right = rootViewBound.width();
        mPatternBoxBound.top = (int) (rootViewBound.top + (rootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT));
        mPatternBoxBound.bottom = (int) (rootViewBound.bottom -
                rootViewBound.height() * (getRootView().isFingerPrintEnable() ? Constants.KEY_BOARD_BOTTOM_WEIGHT : 0));

        float singleIndicatorHeight = mPatternBoxBound.height() / Constants.NO_OF_ROWS;
        float singleIndicatorWidth = mPatternBoxBound.width() / Constants.NO_OF_COLUMNS;

        mIndicators = new ArrayList<>();
        for (int colNo = 0; colNo < Constants.NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < Constants.NO_OF_ROWS; rowNo++) {
                Rect indicatorBound = new Rect();
                indicatorBound.left = (int) ((colNo * singleIndicatorWidth) + mPatternBoxBound.left);
                indicatorBound.right = (int) (indicatorBound.left + singleIndicatorWidth);
                indicatorBound.top = (int) ((rowNo * singleIndicatorHeight) + mPatternBoxBound.top);
                indicatorBound.bottom = (int) (indicatorBound.top + singleIndicatorHeight);
                mIndicators.add(mIndicatorBuilder.getIndicator(indicatorBound));
            }
        }
    }

    @Override
    void preparePaint() {

    }

    /**
     * Set the default theme parameters.
     */
    @SuppressWarnings("deprecation")
    @Override
    void setDefaults() {
        //Do nothing
    }

    @Override
    void onAuthenticationFail() {
        //Play failed animation for all keys
        for (Indicator indicator : mIndicators) indicator.onAuthFailed();
        getRootView().invalidate();
    }

    @Override
    void onAuthenticationSuccess() {
        //Play success animation for all keys
        for (Indicator indicator : mIndicators) indicator.onAuthSuccess();
        getRootView().invalidate();
    }

    /**
     * Draw pattern box on the canvas.
     *
     * @param canvas canvas on which the keyboard will be drawn.
     */
    @Override
    void draw(@NonNull Canvas canvas) {
        for (Indicator indicator : mIndicators) indicator.draw(canvas, false);
    }

    ///////////////// SETTERS/GETTERS //////////////

    /**
     * Find which key is pressed based on the ACTION_DOWN and ACTION_UP coordinates.
     *
     * @param downEventX ACTION_DOWN event X coordinate
     * @param downEventY ACTION_DOWN event Y coordinate
     * @param upEventX   ACTION_UP event X coordinate
     * @param upEventY   ACTION_UP event Y coordinate
     */
    @Nullable
    String findKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY) {
//        //figure out down key.
//        for (Key key : mIndicators) {
//            if (key.getDigit().isEmpty()) continue;  //Empty key
//
//            //Update the typed passcode if the ACTION_DOWN and ACTION_UP keys are same.
//            //Prevent swipe gestures to trigger false key press event.
//            if (key.isKeyPressed(downEventX, downEventY) && key.isKeyPressed(upEventX, upEventY)) {
//                key.playClickAnimation();
//                return key.getDigit();
//            }
//        }
        return null;
    }

    ArrayList<Indicator> getIndicators() {
        return mIndicators;
    }

    Rect getBounds() {
        return mPatternBoxBound;
    }

    boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    void setOneHandOperation(boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }

    Indicator.Builder getIndicatorBuilder() {
        return mIndicatorBuilder;
    }

    void setIndicatorBuilder(@NonNull Indicator.Builder mIndicatorBuilder) {
        this.mIndicatorBuilder = mIndicatorBuilder;
    }
}
