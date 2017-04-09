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
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class PinView extends View {
    private Context mContext;
    private float mDownKeyX;                        //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;                        //Y coordinate of the ACTION_DOWN point

    private AuthenticationListener mAuthenticationListener;   //Callback listener for application to get notify when authentication successful.
    private String mPinToCheck;                     //Current PIN with witch entered PIN will check.
    @NonNull
    private String mPinTyped = "";                  //PIN typed.

    //Collections
    private ArrayList<Indicator> mDotsIndicator;    //List of al the keys

    //Rectangle bounds
    private Rect mRootViewBound = new Rect();
    private Rect mDividerBound = new Rect();        //Divider bound

    //Theme attributes
    @ColorInt
    private int mIndicatorStrokeColor;              //Empty indicator stroke color
    @ColorInt
    private int mIndicatorFilledColor;              //Filled indicator stroke color
    @ColorInt
    private int mDividerColor;                      //Horizontal divider color
    @ColorInt
    private int mTitleColor;                        //Title text color
    private String mTitle;                          //Title color

    //Paints
    private Paint mDividerPaint;                    //Horizontal divider paint color
    private Paint mEmptyIndicatorPaint;             //Empty indicator color
    private Paint mSolidIndicatorPaint;             //Solid indicator color

    private KeyPadBox mKeyPadBox;
    private FingerPrintBox mFingerPrintBox;

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
        mKeyPadBox = new KeyPadBox(this);
        mFingerPrintBox = new FingerPrintBox(this);

        if (attrs != null) {
            parseTypeArr(attrs);
        } else {
            mTitle = Constants.DEF_TITLE_TEXT;
            mTitleColor = getResources().getColor(R.color.key_default_color);

            mDividerColor = getResources().getColor(R.color.divider_color);

            mIndicatorFilledColor = getResources().getColor(R.color.indicator_filled_color);
            mIndicatorStrokeColor = getResources().getColor(R.color.indicator_stroke_color);

            mKeyPadBox.setDefaults();
            mFingerPrintBox.setDefaults();
        }

        mKeyPadBox.prepareKeyTextPaint();
        mKeyPadBox.prepareKeyBgPaint();
        mFingerPrintBox.prepareStatusTextPaint();
        prepareDividerPaint();
        prepareIndicatorPaint();
    }

    @SuppressWarnings("deprecation")
    private void parseTypeArr(@Nullable AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinView, 0, 0);
        try {
            //Parse title params
            mTitle = a.hasValue(R.styleable.PinView_titleText) ?
                    a.getString(R.styleable.PinView_titleText) : Constants.DEF_TITLE_TEXT;
            mTitleColor = a.getColor(R.styleable.PinView_titleTextColor,
                    mContext.getResources().getColor(R.color.key_default_color));

            //Parse divider params
            mDividerColor = a.getColor(R.styleable.PinView_dividerColor,
                    mContext.getResources().getColor(R.color.divider_color));

            //Parse indicator params
            mIndicatorFilledColor = a.getColor(R.styleable.PinView_indicatorSolidColor,
                    getResources().getColor(R.color.indicator_filled_color));
            mIndicatorStrokeColor = a.getColor(R.styleable.PinView_indicatorStrokeColor,
                    getResources().getColor(R.color.indicator_stroke_color));

            //Set the key box params
            mKeyPadBox.setKeyTextColor(a.getColor(R.styleable.PinView_keyTextColor,
                    mContext.getResources().getColor(R.color.key_default_color)));
            mKeyPadBox.setKeyBackgroundColor(a.getColor(R.styleable.PinView_keyStrokeColor,
                    mContext.getResources().getColor(R.color.key_background_color)));
            mKeyPadBox.setKeyTextSize(a.getDimensionPixelSize(R.styleable.PinView_keyTextSize,
                    (int) mContext.getResources().getDimension(R.dimen.key_text_size)));
            mKeyPadBox.setPinCodeLength(a.getInteger(R.styleable.PinView_pinLength,
                    Constants.DEF_PIN_LENGTH));
            mKeyPadBox.setKeyStrokeWidth(a.getDimension(R.styleable.PinView_keyStrokeWidth,
                    mContext.getResources().getDimension(R.dimen.key_stroke_width)));
            //noinspection WrongConstant
            mKeyPadBox.setKeyShape(a.getInt(R.styleable.PinView_keyShape, KeyPadBox.KEY_TYPE_CIRCLE));
            mKeyPadBox.setFingerPrintEnable(a.getBoolean(R.styleable.PinView_fingerprintEnable, true));

            //Fet fingerprint params
            //noinspection ConstantConditions
            mFingerPrintBox.setStatusText(a.hasValue(R.styleable.PinView_titleText) ?
                    a.getString(R.styleable.PinView_fingerprintDefaultText) : FingerPrintBox.DEF_FINGERPRINT_STATUS);
            mFingerPrintBox.setStatusTextColor(a.getColor(R.styleable.PinView_fingerprintTextColor,
                    mContext.getResources().getColor(R.color.key_default_color)));
            mFingerPrintBox.setStatusTextSize(a.getDimension(R.styleable.PinView_fingerprintTextSize,
                    (int) mContext.getResources().getDimension(R.dimen.fingerprint_status_text_size)));
            mFingerPrintBox.setFingerPrintEnable(a.getBoolean(R.styleable.PinView_fingerprintEnable, true));
        } finally {
            a.recycle();
        }
    }

    private void prepareIndicatorPaint() {
        //Set empty dot paint
        mEmptyIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEmptyIndicatorPaint.setStyle(Paint.Style.STROKE);
        mEmptyIndicatorPaint.setColor(mIndicatorStrokeColor);
        mEmptyIndicatorPaint.setStrokeWidth(mContext.getResources().getDimension(R.dimen.indicator_stroke_width));

        //Set filled dot paint
        mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidIndicatorPaint.setColor(mIndicatorFilledColor);
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
        mKeyPadBox.drawKeys(canvas);
        drawDivider(canvas);
        drawIndicatorDots(canvas);
        mFingerPrintBox.drawFingerPrintBox(canvas);
    }

    private void drawIndicatorDots(Canvas canvas) {
        for (int i = 0; i < mKeyPadBox.getPinCodeLength(); i++) {
            mDotsIndicator.get(i).draw(mContext, canvas,
                    i < mPinTyped.length() ? mSolidIndicatorPaint : mEmptyIndicatorPaint);
        }
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

        measureMainView(widthMeasureSpec, heightMeasureSpec);
        mKeyPadBox.measureKeyboard(mRootViewBound);
        measureDivider();
        measureIndicators();
        mFingerPrintBox.measureFingerPrintBox(mRootViewBound);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Measure the root view and get bounds.
     */
    private Rect measureMainView(int widthMeasureSpec, int heightMeasureSpec) {
        int[] l = new int[2];
        getLocationOnScreen(l);

        mRootViewBound.left = l[0];
        mRootViewBound.top = l[1];
        mRootViewBound.right = mRootViewBound.left + MeasureSpec.getSize(widthMeasureSpec);
        mRootViewBound.bottom = mRootViewBound.top + MeasureSpec.getSize(heightMeasureSpec);

        return mRootViewBound;
    }

    /**
     * Measure the dots indicators.
     */
    private void measureIndicators() {
        int indicatorWidth = 2 * (int) (mContext.getResources().getDimension(R.dimen.indicator_radius) + mContext.getResources().getDimension(R.dimen.indicator_padding));
        int totalSpace = indicatorWidth * mKeyPadBox.getPinCodeLength();

        Rect dotsIndicatorBound = new Rect();
        dotsIndicatorBound.left = (mRootViewBound.width() - totalSpace) / 2;
        dotsIndicatorBound.right = dotsIndicatorBound.left + totalSpace;
        dotsIndicatorBound.bottom = (int) (mDividerBound.top - mContext.getResources().getDimension(R.dimen.divider_horizontal_margin));
        dotsIndicatorBound.top = dotsIndicatorBound.bottom - indicatorWidth;

        mDotsIndicator = new ArrayList<>(mKeyPadBox.getPinCodeLength());
        for (int i = 0; i < mKeyPadBox.getPinCodeLength(); i++) {
            Rect rect = new Rect();
            rect.left = dotsIndicatorBound.left + i * indicatorWidth;
            rect.right = rect.left + indicatorWidth;
            rect.top = dotsIndicatorBound.top;
            rect.bottom = dotsIndicatorBound.bottom;

            mDotsIndicator.add(new Indicator(rect));
        }
    }

    /**
     * Measure horizontal divider bounds.
     */
    private void measureDivider() {
        mDividerBound.left = (int) (mRootViewBound.left + mContext.getResources().getDimension(R.dimen.divider_horizontal_margin));
        mDividerBound.right = (int) (mRootViewBound.right - mContext.getResources().getDimension(R.dimen.divider_horizontal_margin));
        mDividerBound.top = (int) (mKeyPadBox.getBounds().top - mContext.getResources().getDimension(R.dimen.divider_vertical_margin));
        mDividerBound.bottom = (int) (mKeyPadBox.getBounds().top - mContext.getResources().getDimension(R.dimen.divider_vertical_margin));
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
                onKeyPressed(mKeyPadBox.findKeyPressed(mDownKeyX,
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
     * If the new digit is {@link Constants#BACKSPACE_TITLE}, remove the last digit of the {@link #mPinTyped}.
     * If the {@link #mPinTyped} has length of {@link KeyPadBox#mPinCodeLength} and equals to {@link #mPinToCheck}
     * notify application as authenticated.
     *
     * @param newDigit newly pressed digit
     */
    private void onKeyPressed(@Nullable String newDigit) {
        if (newDigit == null) return;

        //Check for the state
        if (mAuthenticationListener == null) {
            throw new IllegalStateException("Set AuthenticationListener to receive callbacks.");
        } else if (mPinToCheck.isEmpty() || mPinToCheck.length() != mKeyPadBox.getPinCodeLength()) {
            throw new IllegalStateException("Please set current PIN to check with the entered value.");
        }

        if (newDigit.equals(Constants.BACKSPACE_TITLE)) {
            if (!mPinTyped.isEmpty()) mPinTyped = mPinTyped.substring(0, mPinTyped.length() - 1);
        } else {
            mPinTyped = mPinTyped + newDigit;
        }

        invalidate();

        if (mPinTyped.length() == mKeyPadBox.getPinCodeLength()) {

            if (mPinToCheck.equals(mPinTyped)) {
                mAuthenticationListener.onAuthenticationSuccessful();
            } else {
                mAuthenticationListener.onAuthenticationFailed();
                mKeyPadBox.onAuthenticationError();
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
        mPinTyped = "";
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFingerPrintBox.disableFingerPrint();
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////

    public float getKeyPadding() {
        return mKeyPadBox.getKeyPadding();
    }

    public void setKeyPadding(@Dimension float keyPadding) {
        mKeyPadBox.setKeyPadding(keyPadding);
        requestLayout();
        invalidate();
    }

    public int getPinCodeLength() {
        return mKeyPadBox.getPinCodeLength();
    }

    public void setPinCodeLength(int pinCodeLength) {
        mKeyPadBox.setPinCodeLength(pinCodeLength);
        invalidate();
    }

    public boolean isOneHandOperationEnabled() {
        return mKeyPadBox.isOneHandOperation();
    }

    public void enableOneHandOperation(boolean isEnable) {
        mKeyPadBox.setOneHandOperation(isEnable);
        requestLayout();
        invalidate();
    }

    @Nullable
    public String getPinToCheck() {
        return mPinToCheck;
    }

    public void setPinToCheck(@NonNull String pinToCheck) {
        mPinToCheck = pinToCheck;
    }

    @Nullable
    public AuthenticationListener getAuthenticationListener() {
        return mAuthenticationListener;
    }

    public void setAuthenticationListener(@NonNull AuthenticationListener authenticationListener) {
        mAuthenticationListener = authenticationListener;
    }

    public int getKeyBackgroundColor() {
        return mKeyPadBox.getKeyBackgroundColor();
    }

    public void setKeyBackgroundColor(@ColorInt int keyBackgroundColor) {
        mKeyPadBox.setKeyBackgroundColor(keyBackgroundColor);
        invalidate();
    }

    public int getKeyTextColor() {
        return mKeyPadBox.getKeyTextColor();
    }

    public void setKeyTextColor(@ColorInt int keyTextColor) {
        mKeyPadBox.setKeyTextColor(keyTextColor);
        invalidate();
    }

    public int getIndicatorStrokeColor() {
        return mIndicatorStrokeColor;
    }

    public void setIndicatorStrokeColor(@ColorInt int indicatorStrokeColor) {
        mIndicatorStrokeColor = indicatorStrokeColor;
        prepareIndicatorPaint();
        invalidate();
    }

    public int getIndicatorFilledColor() {
        return mIndicatorFilledColor;
    }

    public void setIndicatorFilledColor(@ColorInt int indicatorFilledColor) {
        mIndicatorFilledColor = indicatorFilledColor;
        prepareIndicatorPaint();
        invalidate();
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
        return mTitleColor;
    }

    public void setTitleColor(@ColorInt int titleColor) {
        mTitleColor = titleColor;
        //TODO set title paint
        invalidate();
    }

    /**
     * @return Current title of the view.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Set the title at the top of view.
     *
     * @param title title string
     */
    public void setTitle(@NonNull String title) {
        mTitle = title;
        invalidate();
    }

    @NonNull
    String getFingerPrintStatusText() {
        return mFingerPrintBox.getStatusText();
    }

    void setFingerPrintStatusText(@NonNull String statusText) {
        mFingerPrintBox.setStatusText(statusText);
        invalidate();
    }

    int getFingerPrintStatusTextColor() {
        return mFingerPrintBox.getStatusTextColor();
    }

    void setFingerPrintStatusTextColor(@ColorInt int statusTextColor) {
        mFingerPrintBox.setStatusTextColor(statusTextColor);
        invalidate();
    }

    void setFingerPrintStatusTextColorRes(@ColorRes int statusTextColor) {
        mFingerPrintBox.setStatusTextColor(mContext.getResources().getColor(statusTextColor));
        invalidate();
    }

    float getFingerPrintStatusTextSize() {
        return mFingerPrintBox.getStatusTextSize();
    }

    void setFingerPrintStatusTextSize(@Dimension float statusTextSize) {
        mFingerPrintBox.setStatusTextSize(statusTextSize);
        invalidate();
    }

    void setFingerPrintStatusTextSize(@DimenRes int statusTextSize) {
        mFingerPrintBox.setStatusTextSize(getResources().getDimension(statusTextSize));
        invalidate();
    }

    Boolean isFingerPrintEnable() {
        return mFingerPrintBox.setFingerPrintEnable();
    }

    void isFingerPrintEnable(boolean isEnable) {
        mFingerPrintBox.setFingerPrintEnable(isEnable);
        mKeyPadBox.setFingerPrintEnable(isEnable);
    }
}
