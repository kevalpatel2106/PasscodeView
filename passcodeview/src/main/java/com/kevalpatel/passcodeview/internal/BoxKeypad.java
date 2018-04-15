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

package com.kevalpatel.passcodeview.internal;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.util.AttributeSet;

import com.kevalpatel.passcodeview.Constants;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.keys.Key;
import com.kevalpatel.passcodeview.keys.KeyNamesBuilder;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class BoxKeypad extends Box {

    @NonNull
    private final Drawable mBackSpaceIcon;
    /**
     * Names of all the keys to display in the key board.
     */
    @Size(Constants.NO_OF_KEY_BOARD_ROWS * Constants.NO_OF_KEY_BOARD_COLUMNS)
    private String[][] sKeyNames;
    /**
     * Boolean to indicate if the keyboard in the one hand operation? If this is true, the keys will be
     * shrieked horizontally to accommodate in small areas.
     */
    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.
    /**
     * List of all the {@link Key}.
     *
     * @see Key
     */
    @NonNull
    private ArrayList<Key> mKeys;
    /**
     * {@link Rect} coordinates of the keyboard box.
     */
    @NonNull
    private Rect mKeyBoxBound = new Rect();
    /**
     * {@link Key.Builder} with the parameters of the key.
     */
    private Key.Builder mKeyBuilder;

    /**
     * Public constructor.
     *
     * @param basePasscodeView {@link PinView} in which box will be displayed.
     */
    public BoxKeypad(@NonNull final BasePasscodeView basePasscodeView) {
        super(basePasscodeView);
        //Initialize the keys list
        mKeys = new ArrayList<>();
        mBackSpaceIcon = getContext().getResources().getDrawable(R.drawable.ic_back_space);
    }

    @Override
    public void init() {
        //Do nothing
    }

    /**
     * Set the default theme parameters.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setDefaults() {
        mKeys.clear();
    }

    @Override
    public void preparePaint() {
        //Do nothing
    }

    @Override
    public void parseTypeArr(@NonNull final AttributeSet typedArray) {
        //Do nothing
    }

    /**
     * Draw keyboard on the canvas. This will drawText all the {@link #sKeyNames} on the canvas.
     *
     * @param canvas canvas on which the keyboard will be drawn.
     */
    @Override
    public void drawView(@NonNull final Canvas canvas) {
        for (Key key : mKeys) {
            if (key.getDigit().isEmpty()) continue; //Don't drawText the empty button

            key.drawShape(canvas);
            if (key.getDigit().equals(KeyNamesBuilder.BACKSPACE_TITLE)) {
                key.drawBackSpace(canvas, mBackSpaceIcon);
            } else {
                key.drawText(canvas);
            }
        }
    }

    /**
     * Measure and display the keypad box.
     * |------------------------|=|
     * |                        | |
     * |                        | | => The title and the indicator. ({@link BoxTitleIndicator#measureView(Rect)})
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
    public void measureView(@NonNull final Rect rootViewBound) {
        if (mKeyBuilder == null)
            throw new NullPointerException("Set key using KeyBuilder first.");

        //Prepare the bound of the key board box
        mKeyBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mKeyBoxBound.right = rootViewBound.width();
        mKeyBoxBound.top = (int) (rootViewBound.top + (rootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT));
        mKeyBoxBound.bottom = (int) (rootViewBound.bottom -
                rootViewBound.height() * (getRootView().isFingerPrintEnable() ? Constants.KEY_BOARD_BOTTOM_WEIGHT : 0));

        //Prepare the keys.
        float singleKeyHeight = mKeyBoxBound.height() / Constants.NO_OF_KEY_BOARD_ROWS;
        float singleKeyWidth = mKeyBoxBound.width() / Constants.NO_OF_KEY_BOARD_COLUMNS;

        mKeys.clear();

        //Columns
        for (int colNo = 0; colNo < Constants.NO_OF_KEY_BOARD_COLUMNS; colNo++) {

            //Rows
            for (int rowNo = 0; rowNo < Constants.NO_OF_KEY_BOARD_ROWS; rowNo++) {

                Rect keyBound = new Rect();
                keyBound.left = (int) ((colNo * singleKeyWidth) + mKeyBoxBound.left);
                keyBound.right = (int) (keyBound.left + singleKeyWidth);
                keyBound.top = (int) ((rowNo * singleKeyHeight) + mKeyBoxBound.top);
                keyBound.bottom = (int) (keyBound.top + singleKeyHeight);
                mKeys.add(mKeyBuilder.buildInternal(sKeyNames[colNo][rowNo], keyBound));
            }
        }
    }

    @Override
    public void onAuthenticationFail() {
        //Play failed animation for all keys
        for (Key key : mKeys) key.onAuthFail();
    }

    @Override
    public void onAuthenticationSuccess() {
        //Play success animation for all keys
        for (Key key : mKeys) key.onAuthSuccess();
    }

    @Override
    public void reset() {
        //Do nothing
    }

    ///////////////// SETTERS/GETTERS //////////////

    /**
     * Set the name of the different keys based on the locale.
     * This method and {@link #sKeyNames} are static to avoid duplicate object creation.
     *
     * @param keyNames String with the names of the key.
     * @see KeyNamesBuilder
     */
    @InternalApi
    public void setKeyNames(@NonNull final KeyNamesBuilder keyNames) {
        sKeyNames = new String[][]{
                {keyNames.getKeyOne(), keyNames.getKeyFour(), keyNames.getKeySeven(), ""},
                {keyNames.getKeyTwo(), keyNames.getKeyFive(), keyNames.getKeyEight(), keyNames.getKeyZero()},
                {keyNames.getKeyThree(), keyNames.getKeySix(), keyNames.getKeyNine(), KeyNamesBuilder.BACKSPACE_TITLE}
        };
    }

    /**
     * Find which key is pressed based on the ACTION_DOWN and ACTION_UP coordinates.
     *
     * @param downEventX ACTION_DOWN event X coordinate
     * @param downEventY ACTION_DOWN event Y coordinate
     * @param upEventX   ACTION_UP event X coordinate
     * @param upEventY   ACTION_UP event Y coordinate
     */
    @InternalApi
    @Nullable
    public String findKeyPressed(final float downEventX,
                                 final float downEventY,
                                 final float upEventX,
                                 final float upEventY) {
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

    @InternalApi
    @NonNull
    public Rect getBounds() {
        return mKeyBoxBound;
    }

    @InternalApi
    public boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    @InternalApi
    public void setOneHandOperation(final boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }

    @InternalApi
    public Key.Builder getKeyBuilder() {
        return mKeyBuilder;
    }

    @InternalApi
    public void setKeyBuilder(final Key.Builder keyBuilder) {
        mKeyBuilder = keyBuilder;
    }
}
