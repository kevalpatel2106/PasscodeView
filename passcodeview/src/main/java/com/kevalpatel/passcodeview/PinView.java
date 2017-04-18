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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.indicators.Indicator;
import com.kevalpatel.passcodeview.keys.Key;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class PinView extends PasscodeView implements InteractiveArrayList.ChangeListener {
    private float mDownKeyX;                                        //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;                                        //Y coordinate of the ACTION_DOWN point

    private int[] mCorrectPin;                                      //Current PIN with witch entered PIN will check.
    private InteractiveArrayList<Integer> mPinTyped;                //PIN typed.

    private BoxKeypad mBoxKeypad;
    private BoxTitleIndicator mBoxIndicator;

    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public PinView(Context context) {
        super(context);
    }

    public PinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS INITIALIZE
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize view.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void init() {
        mPinTyped = new InteractiveArrayList<>();
        mPinTyped.setChangeListener(this);

        mBoxKeypad = new BoxKeypad(this);
        mBoxIndicator = new BoxTitleIndicator(this);
    }

    @Override
    protected void setDefaultParams() {
        mBoxIndicator.setDefaults();
        mBoxKeypad.setDefaults();
    }

    @Override
    protected void preparePaint() {
        //Prepare paints.
        mBoxKeypad.preparePaint();
        mBoxIndicator.preparePaint();
    }

    /**
     * Parse the theme attribute using the parse array.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void parseTypeArr(@Nullable TypedArray typedArray) {
        //Parse title params
        mBoxIndicator.setTitle(typedArray.hasValue(R.styleable.PinView_titleText) ?
                typedArray.getString(R.styleable.PinView_titleText) : BoxTitleIndicator.DEF_TITLE_TEXT);
        mBoxIndicator.setTitleColor(typedArray.getColor(R.styleable.PinView_titleTextColor,
                mContext.getResources().getColor(R.color.lib_key_default_color)));
    }


    ///////////////////////////////////////////////////////////////
    //                  VIEW DRAW
    ///////////////////////////////////////////////////////////////

    /**
     * Draw method of the view called every time frame refreshes.
     *
     * @param canvas view canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBoxKeypad.draw(canvas);
        mBoxIndicator.draw(canvas);
        mBoxFingerprint.draw(canvas);
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBoxKeypad.measure(mRootViewBound);
        mBoxIndicator.measure(mRootViewBound);
        mBoxFingerprint.measure(mRootViewBound);
    }

    ///////////////////////////////////////////////////////////////
    //                  TOUCH HANDLER
    ///////////////////////////////////////////////////////////////


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownKeyX = event.getX();
                mDownKeyY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                onKeyPressed(mBoxKeypad.findKeyPressed(mDownKeyX,
                        mDownKeyY,
                        event.getX(),
                        event.getY()));
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Handle the newly added key digit. Append the digit to {@link #mPinTyped}.
     * If the new digit is {@link KeyNamesBuilder#BACKSPACE_TITLE}, remove the last digit of the {@link #mPinTyped}.
     * If the {@link #mPinTyped} has length of {@link #mCorrectPin} and equals to {@link #mCorrectPin}
     * notify application as authenticated.
     *
     * @param newDigit newly pressed digit
     */
    private void onKeyPressed(@Nullable String newDigit) {
        if (newDigit == null) return;

        //Check for the state
        if (mAuthenticationListener == null) {
            throw new IllegalStateException("Set AuthenticationListener to receive callbacks.");
        } else if (mCorrectPin == null || mCorrectPin.length == 0) {
            throw new IllegalStateException("Please set current PIN to check with the entered value.");
        }

        if (newDigit.equals(KeyNamesBuilder.BACKSPACE_TITLE)) { //Back space key is pressed.
            if (mPinTyped.size() > 0) mPinTyped.remove(mPinTyped.size() - 1);   //Remove last digit.
        } else {

            //Add new digit
            mPinTyped.add(mBoxKeypad.getKeyNameBuilder().getValueOfKey(newDigit));
        }

        invalidate();

        if (mCorrectPin.length == mPinTyped.size()) {   //Only check for the pin validity if typed pin has the length of correct pin.

            //Check if the pin is matched?
            if (Utils.isPINMatched(mCorrectPin, mPinTyped)) {
                //Hurray!!! Authentication is successful.

                if (isTactileFeedbackEnable())
                    Utils.giveTactileFeedbackForAuthSuccess(mContext);  //Give tactile feedback.
                mAuthenticationListener.onAuthenticationSuccessful();   //Notify the parent application

                //Notify all the boxes for authentication success.
                mBoxKeypad.onAuthenticationSuccess();
                mBoxIndicator.onAuthenticationSuccess();
                mBoxFingerprint.onAuthenticationSuccess();
            } else {
                //:-( Authentication failed.

                if (isTactileFeedbackEnable())
                    Utils.giveTactileFeedbackForAuthFail(mContext);     //Give tactile feedback.
                mAuthenticationListener.onAuthenticationFailed();       //Notify parent application

                //Notify all the boxes for authentication success.
                mBoxFingerprint.onAuthenticationFail();
                mBoxKeypad.onAuthenticationFail();
                mBoxIndicator.onAuthenticationFail();
            }

            //Reset the view.
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            }, 350);
        } else if (isTactileFeedbackEnable()) {
            Utils.giveTactileFeedbackForKeyPress(mContext);
        }
    }

    /**
     * Reset the pin code and view state.
     */
    @Override
    public void reset() {
        mPinTyped.clear();
        invalidate();
    }

    /**
     * This method will be called when there is any change in {@link #mPinTyped}.
     *
     * @param size this is the new size of {@link #mPinTyped}.
     * @see InteractiveArrayList
     */
    @Override
    public void onArrayValueChange(int size) {
        mBoxIndicator.onPinDigitEntered(size);
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////

    public boolean isOneHandOperationEnabled() {
        return mBoxKeypad.isOneHandOperation();
    }

    public void enableOneHandOperation(boolean isEnable) {
        mBoxKeypad.setOneHandOperation(isEnable);
        requestLayout();
        invalidate();
    }

    public void setCorrectPin(@NonNull int[] correctPin) {
        //Validate the pin
        if (!Utils.isValidPin(correctPin)) {
            throw new IllegalArgumentException("Invalid PIN.");
        }

        mCorrectPin = correctPin;
        mBoxIndicator.setPinLength(mCorrectPin.length);

        mPinTyped.clear();
        invalidate();
    }

    public int getTitleColor() {
        return mBoxIndicator.getTitleColor();
    }

    public void setTitleColor(@ColorInt int titleColor) {
        mBoxIndicator.setTitleColor(titleColor);
        invalidate();
    }

    /**
     * @return Current title of the view.
     */
    public String getTitle() {
        return mBoxIndicator.getTitle();
    }

    /**
     * Set the title at the top of view.
     *
     * @param title title string
     */
    public void setTitle(@NonNull String title) {
        mBoxIndicator.setTitle(title);
        invalidate();
    }

    @Nullable
    public Key.Builder getKeyBuilder() {
        return mBoxKeypad.getKeyBuilder();
    }

    public void setIndicator(@NonNull Indicator.Builder indicatorBuilder) {
        mBoxIndicator.setIndicatorBuilder(indicatorBuilder);
        requestLayout();
        invalidate();
    }

    @Nullable
    public Indicator.Builder getIndicatorBuilder() {
        return mBoxIndicator.getIndicatorBuilder();
    }

    public void setKey(@NonNull Key.Builder keyBuilder) {
        mBoxKeypad.setKeyBuilder(keyBuilder);
        requestLayout();
        invalidate();
    }

    public void setKeyNames(@NonNull KeyNamesBuilder keyNames) {
        BoxKeypad.setKeyNames(keyNames);

        mPinTyped.clear(); //Need to clear the typed pin, so that change in localization don't affect the pin matching process.

        requestLayout();
        invalidate();
    }

    public int[] getCurrentTypedPin() {
        int[] arr = new int[mPinTyped.size()];
        for (int i = 0; i < mPinTyped.size(); i++) arr[i] = mPinTyped.get(i);
        return arr;
    }

    public void setCurrentTypedPin(int[] currentTypedPin) {
        if (mCorrectPin.length == 0) {
            throw new IllegalStateException("You must call setCorrectPattern() before calling this method.");
        } else if (currentTypedPin.length > mCorrectPin.length) {
            throw new IllegalArgumentException("Invalid pin length.");
        }

        //Add the pin to pin typed
        mPinTyped.clear();
        for (int i : currentTypedPin) mPinTyped.add(i);

        requestLayout();
        invalidate();
    }
}
