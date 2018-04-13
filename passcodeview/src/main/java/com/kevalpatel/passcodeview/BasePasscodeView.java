/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.kevalpatel.passcodeview.box.BoxFingerprint;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;

/**
 * Created by Keval Patel on 18/04/17.
 * A base class to implement the view for authentication like {@link PinView} and {@link PatternView}.
 * This class will set up finger print reader and the indicator boxes.
 *
 * @author 'https://github.com/kevalpatel2106'
 * @see PatternView
 * @see PinView
 */

public abstract class BasePasscodeView extends View implements PasscodeViewLifeCycle {

    /**
     * {@link Context} of the view.
     */
    @NonNull
    protected final Context mContext;
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
        mContext = context;
        mBoxFingerprint = new BoxFingerprint(this);

        init(null);
    }

    public BasePasscodeView(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mBoxFingerprint = new BoxFingerprint(this);

        init(attrs);
    }

    public BasePasscodeView(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mBoxFingerprint = new BoxFingerprint(this);

        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasePasscodeView(@NonNull final Context context,
                            @Nullable final AttributeSet attrs,
                            final int defStyleAttr,
                            final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
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
            TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.BasePasscodeView, 0, 0);
            try {
                mIsTactileFeedbackEnabled = a.getBoolean(R.styleable.BasePasscodeView_giveTactileFeedback, true);

                //Parse divider params
                mDividerColor = a.getColor(R.styleable.BasePasscodeView_dividerColor,
                        Utils.getColorCompat(mContext, R.color.lib_divider_color));

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
                + mContext.getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.right = (int) (mRootViewBound.right
                - mContext.getResources().getDimension(R.dimen.lib_divider_horizontal_margin));
        mDividerBound.top = (int) (mRootViewBound.top + (mRootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT)
                - mContext.getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mDividerBound.bottom = (int) (mRootViewBound.top + (mRootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT)
                - mContext.getResources().getDimension(R.dimen.lib_divider_vertical_margin));
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
        if (isTactileFeedbackEnable())
            Utils.giveTactileFeedbackForAuthSuccess(mContext);  //Give tactile feedback.

        if (mAuthenticationListener != null) mAuthenticationListener.onAuthenticationSuccessful();
    }

    @Override
    @CallSuper
    public void onAuthenticationFail() {
        if (isTactileFeedbackEnable())
            Utils.giveTactileFeedbackForAuthFail(mContext);  //Give tactile feedback.

        if (mAuthenticationListener != null) mAuthenticationListener.onAuthenticationFailed();
    }

    @Override
    @CallSuper
    public void reset() {
        mBoxFingerprint.reset();
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////


    public void setAuthenticationListener(@NonNull final AuthenticationListener authenticationListener) {
        mAuthenticationListener = authenticationListener;
    }

    /**
     * Get the colors of the dividers.
     */
    @ColorInt
    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(@ColorInt final int dividerColor) {
        mDividerColor = dividerColor;
        prepareDividerPaint();
        invalidate();
    }

    public void setDividerColorRes(@ColorRes final int dividerColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setDividerColor(mContext.getColor(dividerColor));
        } else {
            setDividerColor(mContext.getResources().getColor(dividerColor));
        }
    }

    public boolean isTactileFeedbackEnable() {
        return mIsTactileFeedbackEnabled;
    }

    public void setTactileFeedback(final boolean enable) {
        mIsTactileFeedbackEnabled = enable;
    }


    public Boolean isFingerPrintEnable() {
        return mBoxFingerprint.isFingerPrintEnable();
    }

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

    public int getFingerPrintStatusTextColor() {
        return mBoxFingerprint.getStatusTextColor();
    }

    public void setFingerPrintStatusTextColor(@ColorInt final int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(statusTextColor);
        invalidate();
    }

    public void setFingerPrintStatusTextColorRes(@ColorRes final int statusTextColor) {
        mBoxFingerprint.setStatusTextColor(mContext.getResources().getColor(statusTextColor));
        invalidate();
    }

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
