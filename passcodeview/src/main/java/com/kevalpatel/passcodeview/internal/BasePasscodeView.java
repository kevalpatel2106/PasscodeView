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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kevalpatel.passcodeview.Constants;
import com.kevalpatel.passcodeview.PatternView;
import com.kevalpatel.passcodeview.PinView;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;

/**
 * Created by Keval Patel on 18/04/17.
 * A base class to implement the view for authentication like {@link PinView} and {@link PatternView}.
 * This class will set up finger print reader and the indicator boxes.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see PatternView
 * @see PinView
 */

public abstract class BasePasscodeView extends View implements PasscodeViewLifeCycle {
    /**
     * Finger print box.
     *
     * @see BoxFingerprint
     */
    @NonNull
    private final BoxFingerprint mBoxFingerprint;
    /**
     * Bounds of the divider between the title and the keypad or the pattern box.
     */
    @NonNull
    private final Rect mDividerBound = new Rect();
    /**
     * Bounds of the whole {@link BasePasscodeView}.
     */
    @NonNull
    protected Rect mRootViewBound = new Rect();             //Bounds for the root view
    /**
     * A listener to notify the user when the authentication successful or failed.
     *
     * @see AuthenticationListener
     */
    protected AuthenticationListener mAuthenticationListener;
    /**
     * Integer color of the divider between title and divider.
     */
    @ColorInt
    private int mDividerColor;
    /**
     * {@link Paint} of the horizontal divider within the view.
     */
    private Paint mDividerPaint;
    /**
     * Boolean to set true of the tactile feedback on the key is press is enabled or not?
     */
    private boolean mIsTactileFeedbackEnabled = true;

    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public BasePasscodeView(@NonNull final Context context) {
        super(context);
        mBoxFingerprint = new BoxFingerprint(this);

        init(null);
    }

    public BasePasscodeView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        mBoxFingerprint = new BoxFingerprint(this);

        init(attrs);
    }

    public BasePasscodeView(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mBoxFingerprint = new BoxFingerprint(this);

        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasePasscodeView(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr,
                            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mBoxFingerprint = new BoxFingerprint(this);

        init(attrs);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS INITIALIZE
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize the view. This will set {@link BoxFingerprint} and parse {@link TypedArray} to
     * read all the parameters added in xml file.
     * <p>
     * If you wan to enable customized parameters, override {@link #init()}  method and initialize the
     * parameters. This method will call before parsing the {@link TypedArray}.
     * <p>
     * If you want to parse view specific XML parameters, override {@link #parseTypeArr(AttributeSet)}
     * and parse the {@link TypedArray}. This method will only call if there is any custom parameters
     * defined in XML.
     * <p>
     * You can set default theme parameters by overriding {@link #setDefaults()} if there are no
     * parameters defined in XML layout.
     *
     * @param attrs {@link AttributeSet}
     */
    private void init(@Nullable final AttributeSet attrs) {
        mBoxFingerprint.init();
        init(); //Call init for the concrete class

        if (attrs != null) {    //Parse all the params from the arguments.
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.BasePasscodeView, 0, 0);
            try {
                mIsTactileFeedbackEnabled = a.getBoolean(R.styleable.BasePasscodeView_giveTactileFeedback, true);

                //Parse divider params
                mDividerColor = a.getColor(R.styleable.BasePasscodeView_dividerColor,
                        Utils.getColorCompat(getContext(), R.color.lib_divider_color));

                //Fet fingerprint params
                mBoxFingerprint.parseTypeArr(attrs);

                parseTypeArr(attrs);
            } finally {
                a.recycle();
            }
        } else {        //Nothing's provided in XML. Set default for now.
            setDividerColor(Utils.getColorCompat(getContext(), R.color.lib_divider_color));

            //Set every thing to defaults.
            mBoxFingerprint.setDefaults();
            setDefaults();
        }

        //Prepare paints.
        prepareDividerPaint();
        mBoxFingerprint.preparePaint();
        preparePaint();
    }


    /**
     * Create the paint to drawText divider.
     */
    private void prepareDividerPaint() {
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mDividerColor);
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);

        mRootViewBound.left = 0;
        mRootViewBound.right = mRootViewBound.left + viewWidth;
        mRootViewBound.top = 0;
        mRootViewBound.bottom = mRootViewBound.left + viewHeight;

        measureDivider();

        //Measure the finger print
        mBoxFingerprint.measureView(mRootViewBound);

        //Pass it to the implementation class
        measureView(mRootViewBound);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Measure horizontal divider bounds.
     * Don't change until you know what you are doing. :-)
     */
    private void measureDivider() {
        mDividerBound.left = (int) (mRootViewBound.left
                + getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.right = (int) (mRootViewBound.right
                - getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.top = (int) (mRootViewBound.top + (mRootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT)
                - getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mDividerBound.bottom = (int) (mRootViewBound.top + (mRootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT)
                - getResources().getDimension(R.dimen.lib_divider_vertical_margin));
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW DRAW
    ///////////////////////////////////////////////////////////////


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDivider(canvas);

        //Draw the finger print box
        mBoxFingerprint.drawView(canvas);

        //Pass it to the implementation class
        drawView(canvas);
    }

    /**
     * Draw the divider between title and the keyboard/pin box.
     *
     * @param canvas {@link Canvas} on which the divider is to draw.
     */
    private void drawDivider(Canvas canvas) {
        canvas.drawLine(mDividerBound.left,
                mDividerBound.top,
                mDividerBound.right,
                mDividerBound.bottom,
                mDividerPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //Stop scanning fingerprint
        mBoxFingerprint.reset();
    }

    @Override
    @CallSuper
    public void onAuthenticationSuccess() {
        giveTactileFeedbackForAuthSuccess();  //Give tactile feedback.
        if (mAuthenticationListener != null) mAuthenticationListener.onAuthenticationSuccessful();
        invalidate();
    }

    @Override
    @CallSuper
    public void onAuthenticationFail() {
        giveTactileFeedbackForAuthFail();  //Give tactile feedback.
        if (mAuthenticationListener != null) mAuthenticationListener.onAuthenticationFailed();
        invalidate();
    }

    @Override
    @CallSuper
    public void reset() {
        mBoxFingerprint.reset();
    }


    /**
     * Run the vibrator to give tactile feedback for 350ms when user authentication is successful.
     */
    private void giveTactileFeedbackForAuthFail() {
        if (!mIsTactileFeedbackEnabled) return;

        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(350);
    }

    /**
     * Run the vibrator to give tactile feedback for 100ms at difference of 50ms for two times when
     * user authentication is failed.
     */
    private void giveTactileFeedbackForAuthSuccess() {
        if (!mIsTactileFeedbackEnabled) return;

        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(new long[]{50, 100, 50, 100}, -1);
    }


    /**
     * Run the vibrator to give tactile feedback for 50ms when any key is pressed.
     */
    protected void giveTactileFeedbackForKeyPress() {
        if (!mIsTactileFeedbackEnabled) return;

        final Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(50);
    }


    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////

    /**
     * Set the {@link AuthenticationListener} to get callbacks when the user is authenticated or
     * not. This is the required parameter to set.
     *
     * @param authenticationListener {@link AuthenticationListener}.
     */
    public void setAuthenticationListener(@NonNull final AuthenticationListener authenticationListener) {
        mAuthenticationListener = authenticationListener;
    }

    /**
     * Get the integer color of the dividers.
     */
    @ColorInt
    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * Set the integer color of the divider.
     *
     * @param dividerColor Integer color.
     * @see #setDividerColorRes(int)
     */
    public void setDividerColor(@ColorInt final int dividerColor) {
        mDividerColor = dividerColor;
        prepareDividerPaint();
        invalidate();
    }

    /**
     * Set the color resource for the color of the divider.
     *
     * @param dividerColor color resource.
     * @see #setDividerColor(int)
     */
    public void setDividerColorRes(@ColorRes final int dividerColor) {
        setDividerColor(Utils.getColorCompat(getContext(), dividerColor));
    }

    /**
     * Check if the tactile feedback is enabled when the user presses the key or authentication goes
     * successful or fail.
     *
     * @return True if the tactile feedback is enabled else false.
     */
    public boolean isTactileFeedbackEnable() {
        return mIsTactileFeedbackEnabled;
    }

    /**
     * Enable or disable the tactile feedback. If the tactile feedback is enabled, application will
     * vibrate the device whenever the user presses any key or pattern cell or whenever authentication
     * completes.
     *
     * @param enable True if application wants to enable tactile feedback else false.
     */
    public void setTactileFeedback(final boolean enable) {
        mIsTactileFeedbackEnabled = enable;
        requestLayout();
        invalidate();
    }

    /**
     * Check if the library enabled to fingerprint scanner to authenticate user using his/her fingerprints
     * or not. If this method returns false, it indicates that {@link PinView} or {@link PatternView}
     * are not displaying fingerprint scanning view.
     *
     * @return Returns false if the device doesn't support the fingerprint scanning hardware, user
     * hasn't enrolled any fingerprints or application disabled fingerprint authentication using
     * {@link #setIsFingerPrintEnable(boolean)} else true.
     */
    public Boolean isFingerPrintEnable() {
        return mBoxFingerprint.isFingerPrintEnable();
    }

    /**
     * Enable or disable the fingerprint authentication manually. Setting the value true doesn't
     * grantee that user will be able to authenticate using fingerprint. Fingerprint scanning will
     * only be enabled if device has supported hardware for fingerprint scanning and user has enrolled
     * at least one finger print. Application can check if the finger print scanning is running using
     * {@link #isFingerPrintEnable()}.
     *
     * @param isEnable True to enable fingerprint scanning else false.
     * @see #isFingerPrintEnable()
     */
    public void setIsFingerPrintEnable(final boolean isEnable) {
        mBoxFingerprint.setFingerPrintEnable(isEnable);
        requestLayout();
        invalidate();
    }

    @NonNull
    public String getFingerPrintStatusText() {
        return mBoxFingerprint.getStatusText();
    }

    public void setFingerPrintStatusText(@NonNull final String statusText) {
        mBoxFingerprint.setStatusText(statusText);
        invalidate();
    }

    @ColorInt
    public int getFingerPrintStatusTextColor() {
        return mBoxFingerprint.getStatusTextColor();
    }

    public void setFingerPrintStatusTextColor(@ColorInt final int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(statusTextColor);
        invalidate();
    }

    public void setFingerPrintStatusTextColorRes(@ColorRes final int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(getResources().getColor(statusTextColor));
        invalidate();
    }

    @Dimension
    public float getFingerPrintStatusTextSize() {
        return mBoxFingerprint.getStatusTextSize();
    }

    public void setFingerPrintStatusTextSize(@Dimension final float statusTextSize) {
        mBoxFingerprint.setStatusTextSize(statusTextSize);
        invalidate();
    }

    public void setFingerPrintStatusTextSize(@DimenRes final int statusTextSize) {
        mBoxFingerprint.setStatusTextSize(getResources().getDimension(statusTextSize));
        invalidate();
    }
}
