/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.box.BoxFingerprint;
import com.kevalpatel.passcodeview.box.BoxKeypad;
import com.kevalpatel.passcodeview.box.BoxTitleIndicator;
import com.kevalpatel.passcodeview.box.KeyNamesBuilder;
import com.kevalpatel.passcodeview.indicators.Indicator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.keys.Key;

/**
 * Created by Keval on 06-Apr-17.
 * <p>
 * This view will perform the PIN based authentication. This view also support fingerprint authentication.
 * To set this view application has to
 * <li>1. Set the correct PIN using {@link #setCorrectPin(int[])}.</li>
 * <li>2. Set key shape using {@link #setKey(Key.Builder)}.</li>
 * <li>3. Set the callback listener. {@link #setAuthenticationListener(AuthenticationListener)}</li>
 * <br/>
 * This view is made up of three different views.
 * <li>Title with the PIN indicators. {@link com.kevalpatel.passcodeview.box.BoxTitleIndicator}</li>
 * <li>Keyboard. {@link BoxKeypad}</li>
 * <li>Fingerprint authentication view. {@link BoxFingerprint}</li>
 *
 * @author 'https://github.com/kevalpatel2106'
 * @see AuthenticationListener
 */

public final class PinView extends BasePasscodeView implements InteractiveArrayList.ChangeListener {
    private float mDownKeyX;                                        //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;                                        //Y coordinate of the ACTION_DOWN point

    private int[] mCorrectPin;                                      //Current PIN with witch entered PIN will check.
    private InteractiveArrayList<Integer> mPinTyped;                //PIN typed.

    /**
     * {@link BoxKeypad} that displays the numeric keyboard to display the keys.
     */
    private BoxKeypad mBoxKeypad;

    /**
     * Box that will contain title and pin indicators.
     */
    private BoxTitleIndicator mBoxIndicator;

    @NonNull
    private KeyNamesBuilder mKeyNamesBuilder = new KeyNamesBuilder();

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
     * Initialize view. This will initialize the view boxes.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void init() {
        //Set the typed pin numbers holder.
        mPinTyped = new InteractiveArrayList<>();
        mPinTyped.setChangeListener(this);

        //Set the keyboard box.
        mBoxKeypad = new BoxKeypad(this);
        mBoxKeypad.init();

        //Set the title/PIN indicator box.
        mBoxIndicator = new BoxTitleIndicator(this);
        mBoxIndicator.init();
    }

    /**
     * Set default parameters if the theme is not set.
     */
    @Override
    public void setDefaults() {
        mBoxIndicator.setDefaults();
        mBoxKeypad.setDefaults();
    }

    /**
     * Prepare all the required pain objects.
     */
    @Override
    public void preparePaint() {
        //Prepare paints.
        mBoxKeypad.preparePaint();
        mBoxIndicator.preparePaint();
    }

    /**
     * Parse the theme attribute using the parse array.
     *
     * @param typedArray {@link AttributeSet} received from the XML.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void parseTypeArr(@NonNull AttributeSet typedArray) {
        //Do nothing
    }

    @Override
    public void measureView(@NonNull Rect rootViewBounds) {
        mBoxKeypad.measureView(mRootViewBound);
        mBoxIndicator.measureView(mRootViewBound);
    }

    @Override
    public void onAuthenticationFail() {
        //Notify all the boxes for authentication success.
        mBoxKeypad.onAuthenticationFail();
        mBoxIndicator.onAuthenticationFail();
        super.onAuthenticationFail();
    }

    @Override
    public void onAuthenticationSuccess() {
        //Notify all the boxes for authentication success.
        mBoxKeypad.onAuthenticationSuccess();
        mBoxIndicator.onAuthenticationSuccess();
        super.onAuthenticationSuccess();
    }

    /**
     * Reset the pin code and view state.
     */
    @Override
    public void reset() {
        super.reset();
        mPinTyped.clear();
        mBoxKeypad.reset();
        mBoxIndicator.reset();
        invalidate();
    }

    /**
     * Draw method of the view called every time frame refreshes.
     *
     * @param canvas view canvas
     */
    @Override
    public void drawView(@NonNull Canvas canvas) {
        mBoxKeypad.drawView(canvas);
        mBoxIndicator.drawView(canvas);
    }

    ///////////////////////////////////////////////////////////////
    //                  TOUCH HANDLER
    ///////////////////////////////////////////////////////////////

    /**
     * Handle touch event.
     */
    @SuppressLint("ClickableViewAccessibility")
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
            mPinTyped.add(mKeyNamesBuilder.getValueOfKey(newDigit));
        }

        invalidate();

        if (mCorrectPin.length == mPinTyped.size()) {   //Only check for the pin validity if typed pin has the length of correct pin.

            //Check if the pin is matched?
            if (Utils.isPINMatched(mCorrectPin, mPinTyped)) {
                //Hurray!!! Authentication is successful.
                onAuthenticationSuccess();
            } else {
                //:-( Authentication failed.
                onAuthenticationFail();
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

    /**
     * @return true if the one hand operation is enabled.
     */
    public boolean isOneHandOperationEnabled() {
        return mBoxKeypad.isOneHandOperation();
    }

    /**
     * Enable/Disable the one hand operation. One hand operation  will shrink the keypad to 70% of the
     * original width and stick it to the right edge of the screen. This will allow user to press the
     * key without using both hands on large screen devices.(e.g. Phablets)
     *
     * @param isEnable true to enable one hand mode.
     */
    public void enableOneHandOperation(final boolean isEnable) {
        mBoxKeypad.setOneHandOperation(isEnable);
        requestLayout();
        invalidate();
    }

    /**
     * Set the correct PIN. If the PIN entered by the user matches this correct PIN, authentication will
     * successful. The length of the PIN and hence the number of the indicators in title will update
     * based on this PIN length automatically.
     *
     * @param correctPin Array if single digits of the PIN. The single digit should not be less than 0 or grater
     *                   than 9.
     */
    public void setCorrectPin(@NonNull final int[] correctPin) {
        //Validate the pin
        if (!Utils.isValidPin(correctPin)) throw new IllegalArgumentException("Invalid PIN.");

        mCorrectPin = correctPin;
        mBoxIndicator.setPinLength(mCorrectPin.length);

        mPinTyped.clear();
        invalidate();
    }

    /**
     * @return Title color of the view.
     */
    @ColorInt
    public int getTitleColor() {
        return mBoxIndicator.getTitleColor();
    }

    /**
     * Set the color of the view title.
     *
     * @param titleColor Color of the title.
     */
    public void setTitleColor(@ColorInt final int titleColor) {
        mBoxIndicator.setTitleColor(titleColor);
        invalidate();
    }

    /**
     * Set the color of the view title.
     *
     * @param titleColor Color of the title.
     */
    public void setTitleColorResource(@ColorRes final int titleColor) {
        mBoxIndicator.setTitleColor(mContext.getResources().getColor(titleColor));
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
    public void setTitle(@NonNull final String title) {
        mBoxIndicator.setTitle(title);
        invalidate();
    }

    /**
     * @return {@link com.kevalpatel.passcodeview.keys.Key.Builder}
     */
    @Nullable
    public Key.Builder getKeyBuilder() {
        return mBoxKeypad.getKeyBuilder();
    }

    /**
     * Set the PIN change indicator. Use {@link com.kevalpatel.passcodeview.indicators.Indicator.Builder}
     * to use different indicators.
     *
     * @param indicatorBuilder {@link com.kevalpatel.passcodeview.indicators.Indicator.Builder}
     */
    public void setIndicator(@NonNull final Indicator.Builder indicatorBuilder) {
        mBoxIndicator.setIndicatorBuilder(indicatorBuilder);
        requestLayout();
        invalidate();
    }

    /**
     * @return {@link com.kevalpatel.passcodeview.indicators.Indicator.Builder}
     */
    @Nullable
    public Indicator.Builder getIndicatorBuilder() {
        return mBoxIndicator.getIndicatorBuilder();
    }

    /**
     * Set the key shape and theme properties by using {@link com.kevalpatel.passcodeview.keys.Key.Builder}.
     * https://github.com/kevalpatel2106/PasscodeView/wiki/Diffrent-Key-Shapes
     *
     * @param keyBuilder {@link com.kevalpatel.passcodeview.keys.Key.Builder}
     */
    public void setKey(@NonNull final Key.Builder keyBuilder) {
        mBoxKeypad.setKeyBuilder(keyBuilder);
        requestLayout();
        invalidate();
    }

    /**
     * Set the name of the keys. So that you can support different locale.
     * https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names
     *
     * @param keyNamesBuilder {@link KeyNamesBuilder}
     */
    public void setKeyNames(@NonNull final KeyNamesBuilder keyNamesBuilder) {
        mKeyNamesBuilder = keyNamesBuilder;
        mBoxKeypad.setKeyNames(keyNamesBuilder);

        mPinTyped.clear(); //Need to clear the typed pin, so that change in localization don't affect the pin matching process.

        requestLayout();
        invalidate();
    }

    /**
     * Get the currently typed PIN numbers.
     *
     * @return Array of PIN digits.
     */
    public int[] getCurrentTypedPin() {
        int[] arr = new int[mPinTyped.size()];
        for (int i = 0; i < mPinTyped.size(); i++) arr[i] = mPinTyped.get(i);
        return arr;
    }

    /**
     * Set the currently typed PIN numbers.
     *
     * @param currentTypedPin Array of PIN digits.
     */
    public void setCurrentTypedPin(final int[] currentTypedPin) {
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
