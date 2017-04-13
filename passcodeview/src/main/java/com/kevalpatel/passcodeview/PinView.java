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
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.kevalpatel.passcodeview.indicators.Indicator;
import com.kevalpatel.passcodeview.keys.Key;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class PinView extends View {
    private Context mContext;
    private float mDownKeyX;                                        //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;                                        //Y coordinate of the ACTION_DOWN point

    private AuthenticationListener mAuthenticationListener;         //Callback listener for application to get notify when authentication successful.
    private int[] mCorrectPin;                                      //Current PIN with witch entered PIN will check.
    private ArrayList<Integer> mPinTyped = new ArrayList<>();       //PIN typed.

    //Rectangle bounds
    private Rect mRootViewBound = new Rect();
    private Rect mDividerBound = new Rect();        //Divider bound

    //Theme attributes
    @ColorInt
    private int mDividerColor;                      //Horizontal divider color

    //Paints
    private Paint mDividerPaint;                    //Horizontal divider paint color

    private BoxKeypad mBoxKeypad;
    private BoxFingerprint mBoxFingerprint;
    private BoxTitleIndicator mBoxIndicator;

    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public PinView(Context context) {
        super(context);
        init(context, null);
    }

    public PinView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize view.
     *
     * @param context instance of the caller.
     * @param attrs   Typed attributes or null.
     */
    @SuppressWarnings("deprecation")
    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        mContext = context;

        //Initialize UI boxes.
        mBoxKeypad = new BoxKeypad(this);
        mBoxFingerprint = new BoxFingerprint(this);
        mBoxIndicator = new BoxTitleIndicator(this);

        if (attrs != null) {    //Parse all the params from the arguments.
            parseTypeArr(attrs);
        } else {        //Nothing's provided in XML. Set default for now.
            mDividerColor = getResources().getColor(R.color.lib_divider_color);

            mBoxIndicator.setDefaults();
            mBoxKeypad.setDefaults();
            mBoxFingerprint.setDefaults();
        }

        //Prepare paints.
        prepareDividerPaint();
        mBoxKeypad.preparePaint();
        mBoxFingerprint.preparePaint();
        mBoxIndicator.preparePaint();
    }

    @SuppressWarnings("deprecation")
    private void parseTypeArr(@Nullable AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinView, 0, 0);
        try {
            //Parse title params
            mBoxIndicator.setTitle(a.hasValue(R.styleable.PinView_titleText) ?
                    a.getString(R.styleable.PinView_titleText) : BoxTitleIndicator.DEF_TITLE_TEXT);
            mBoxIndicator.setTitleColor(a.getColor(R.styleable.PinView_titleTextColor,
                    mContext.getResources().getColor(R.color.lib_key_default_color)));

            //Parse divider params
            mDividerColor = a.getColor(R.styleable.PinView_dividerColor,
                    mContext.getResources().getColor(R.color.lib_divider_color));

            mBoxKeypad.setFingerPrintEnable(a.getBoolean(R.styleable.PinView_fingerprintEnable, true));

            //Fet fingerprint params
            //noinspection ConstantConditions
            mBoxFingerprint.setStatusText(a.hasValue(R.styleable.PinView_titleText) ?
                    a.getString(R.styleable.PinView_fingerprintDefaultText) : BoxFingerprint.DEF_FINGERPRINT_STATUS);
            mBoxFingerprint.setStatusTextColor(a.getColor(R.styleable.PinView_fingerprintTextColor,
                    mContext.getResources().getColor(R.color.lib_key_default_color)));
            mBoxFingerprint.setStatusTextSize(a.getDimension(R.styleable.PinView_fingerprintTextSize,
                    (int) mContext.getResources().getDimension(R.dimen.lib_fingerprint_status_text_size)));
            mBoxFingerprint.setFingerPrintEnable(a.getBoolean(R.styleable.PinView_fingerprintEnable, true));
        } finally {
            a.recycle();
        }
    }

    private void prepareDividerPaint() {
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mDividerColor);
    }


    ///////////////////////////////////////////////////////////////
    //                  VIEW DRAW
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBoxKeypad.draw(canvas);
        drawDivider(canvas);
        mBoxIndicator.draw(canvas);
        mBoxFingerprint.draw(canvas);
    }

    private void drawDivider(Canvas canvas) {
        canvas.drawLine(mDividerBound.left,
                mDividerBound.top,
                mDividerBound.right,
                mDividerBound.bottom,
                mDividerPaint);
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureMainView();
        mBoxKeypad.measure(mRootViewBound);
        measureDivider();
        mBoxIndicator.measure(mRootViewBound);
        mBoxFingerprint.measure(mRootViewBound);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Measure the root view and get bounds.
     */
    private Rect measureMainView() {
        getLocalVisibleRect(mRootViewBound);

        //Get the height of the actionbar if we have any actionbar and add it to the top
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mRootViewBound.top = mRootViewBound.top
                    + TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return mRootViewBound;
    }

    /**
     * Measure horizontal divider bounds.
     * Don't change untill you know what you are doing. :-)
     */
    private void measureDivider() {
        mDividerBound.left = (int) (mRootViewBound.left + mContext.getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.right = (int) (mRootViewBound.right - mContext.getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.top = (int) (mBoxKeypad.getBounds().top - mContext.getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mDividerBound.bottom = (int) (mBoxKeypad.getBounds().top - mContext.getResources().getDimension(R.dimen.lib_divider_vertical_margin));
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

        if (newDigit.equals(KeyNamesBuilder.BACKSPACE_TITLE)) {
            if (mPinTyped.size() > 0) mPinTyped.remove(mPinTyped.size() - 1);
        } else {
            mPinTyped.add(mBoxKeypad.getKeyNameBuilder().getValueOfKey(newDigit));
        }

        mBoxIndicator.onPinDigitEntered(mPinTyped.size());
        invalidate();

        if (mCorrectPin.length == mPinTyped.size()) {
            if (Utils.isPINMatched(mCorrectPin, mPinTyped)) {
                mAuthenticationListener.onAuthenticationSuccessful();
                mBoxKeypad.onAuthenticationSuccess();
                mBoxIndicator.onAuthenticationSuccess();
                mBoxFingerprint.onAuthenticationSuccess();
            } else {
                mAuthenticationListener.onAuthenticationFailed();
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
        }
    }

    /**
     * Reset the pin code and view state.
     */
    public void reset() {
        mPinTyped.clear();
        mBoxIndicator.onPinDigitEntered(mPinTyped.size());
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //Stop scanning fingerprint
        mBoxFingerprint.stopFingerprintScanner();
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
        mBoxIndicator.onPinDigitEntered(mPinTyped.size());
        invalidate();
    }

    @Nullable
    public AuthenticationListener getAuthenticationListener() {
        return mAuthenticationListener;
    }

    public void setAuthenticationListener(@NonNull AuthenticationListener authenticationListener) {
        mAuthenticationListener = authenticationListener;
        mBoxFingerprint.setAuthListener(authenticationListener);
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        mDividerColor = dividerColor;
        prepareDividerPaint();
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

    @NonNull
    public String getFingerPrintStatusText() {
        return mBoxFingerprint.getStatusText();
    }

    public void setFingerPrintStatusText(@NonNull String statusText) {
        mBoxFingerprint.setStatusText(statusText);
        invalidate();
    }

    public int getFingerPrintStatusTextColor() {
        return mBoxFingerprint.getStatusTextColor();
    }

    public void setFingerPrintStatusTextColor(@ColorInt int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(statusTextColor);
        invalidate();
    }

    public void setFingerPrintStatusTextColorRes(@ColorRes int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(mContext.getResources().getColor(statusTextColor));
        invalidate();
    }

    public float getFingerPrintStatusTextSize() {
        return mBoxFingerprint.getStatusTextSize();
    }

    public void setFingerPrintStatusTextSize(@DimenRes int statusTextSize) {
        mBoxFingerprint.setStatusTextSize(getResources().getDimension(statusTextSize));
        invalidate();
    }

    public void setFingerPrintStatusTextSize(@Dimension float statusTextSize) {
        mBoxFingerprint.setStatusTextSize(statusTextSize);
        invalidate();
    }

    public Boolean isFingerPrintEnable() {
        return mBoxFingerprint.setFingerPrintEnable();
    }

    public void isFingerPrintEnable(boolean isEnable) {
        mBoxFingerprint.setFingerPrintEnable(isEnable);
        mBoxKeypad.setFingerPrintEnable(isEnable);
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
        mBoxIndicator.onPinDigitEntered(mPinTyped.size());

        requestLayout();
        invalidate();
    }
}
