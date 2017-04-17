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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.kevalpatel.passcodeview.keys.Key;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

final class BoxKeypad extends Box {
    @Size(Constants.NO_OF_ROWS * Constants.NO_OF_COLUMNS)
    private static String[][] sKeyNames;
    private static KeyNamesBuilder sKeyNamesBuilder;

    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.
    private ArrayList<Key> mKeys;
    private Rect mKeyBoxBound = new Rect();
    private Key.Builder mKeyBuilder;

    /**
     * Public constructor
     *
     * @param pinView {@link PinView} in which box will be displayed.
     */
    BoxKeypad(@NonNull PinView pinView) {
        super(pinView);
        sKeyNamesBuilder = new KeyNamesBuilder();
        sKeyNames = sKeyNamesBuilder.build();
    }

    /**
     * Set the name of the different keys based on the locale.
     * This method and {@link #sKeyNames} are static to avoid duplicate object creation.
     *
     * @param keyNames String with the names of the key.
     */
    static void setKeyNames(@NonNull KeyNamesBuilder keyNames) {
        sKeyNamesBuilder = keyNames;
        sKeyNames = keyNames.build();
    }

    KeyNamesBuilder getKeyNameBuilder() {
        return sKeyNamesBuilder;
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
        if (mKeyBuilder == null)
            throw new NullPointerException("Set key using KeyBuilder first.");

        mKeyBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mKeyBoxBound.right = rootViewBound.width();
        mKeyBoxBound.top = (int) (rootViewBound.top + (rootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT));
        mKeyBoxBound.bottom = (int) (rootViewBound.bottom -
                rootViewBound.height() * (getRootView().isFingerPrintEnable() ? Constants.KEY_BOARD_BOTTOM_WEIGHT : 0));

        float singleKeyHeight = mKeyBoxBound.height() / Constants.NO_OF_ROWS;
        float singleKeyWidth = mKeyBoxBound.width() / Constants.NO_OF_COLUMNS;

        mKeys = new ArrayList<>();
        for (int colNo = 0; colNo < Constants.NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < Constants.NO_OF_ROWS; rowNo++) {
                Rect keyBound = new Rect();
                keyBound.left = (int) ((colNo * singleKeyWidth) + mKeyBoxBound.left);
                keyBound.right = (int) (keyBound.left + singleKeyWidth);
                keyBound.top = (int) ((rowNo * singleKeyHeight) + mKeyBoxBound.top);
                keyBound.bottom = (int) (keyBound.top + singleKeyHeight);
                mKeys.add(mKeyBuilder.getKey(sKeyNames[colNo][rowNo], keyBound));
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
        for (Key key : mKeys) key.onAuthFail();
        getRootView().invalidate();
    }

    @Override
    void onAuthenticationSuccess() {
        //Play success animation for all keys
        for (Key key : mKeys) key.onAuthSuccess();
        getRootView().invalidate();
    }

    /**
     * Draw keyboard on the canvas. This will drawText all the {@link #sKeyNames} on the canvas.
     *
     * @param canvas canvas on which the keyboard will be drawn.
     */
    @Override
    void draw(@NonNull Canvas canvas) {
        Drawable d = getContext().getResources().getDrawable(R.drawable.ic_back_space);
        d.setColorFilter(new PorterDuffColorFilter(mKeyBuilder.getKeyTextPaint().getColor(), PorterDuff.Mode.SRC_ATOP));

        for (Key key : mKeys) {
            if (key.getDigit().isEmpty()) continue; //Don't drawText the empty button

            key.drawShape(canvas);
            if (key.getDigit().equals(KeyNamesBuilder.BACKSPACE_TITLE)) {
                key.drawBackSpace(canvas, d);
            } else {
                key.drawText(canvas);
            }
        }
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
        //figure out down key.
        for (Key key : mKeys) {
            if (key.getDigit().isEmpty()) continue;  //Empty key

            //Update the typed passcode if the ACTION_DOWN and ACTION_UP keys are same.
            //Prevent swipe gestures to trigger false key press event.
            if (key.isKeyPressed(downEventX, downEventY) && key.isKeyPressed(upEventX, upEventY)) {
                key.playClickAnimation();
                return key.getDigit();
            }
        }
        return null;
    }

    ArrayList<Key> getKeys() {
        return mKeys;
    }

    Rect getBounds() {
        return mKeyBoxBound;
    }

    boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    void setOneHandOperation(boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }


    Key.Builder getKeyBuilder() {
        return mKeyBuilder;
    }

    void setKeyBuilder(Key.Builder keyBuilder) {
        mKeyBuilder = keyBuilder;
    }
}
