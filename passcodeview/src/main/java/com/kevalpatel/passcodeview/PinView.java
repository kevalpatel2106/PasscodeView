package com.kevalpatel.passcodeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kevalpatel.passcodeview.interfaces.PinChangeListener;

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

    private PinChangeListener mPinChangeListener;   //Callback listener for application to get notify when authentication successful.
    private String mPinToCheck;                     //Current PIN with witch entered PIN will check.
    @NonNull
    private String mPinTyped = "";                  //PIN typed.

    //Collections
    private ArrayList<Key> mKeys;                   //List of al the keys
    private ArrayList<Indicator> mDotsIndicator;    //List of al the keys

    //Rectangle bounds
    private Rect mKeyBoardBound = new Rect();       //Keyboard layout bound
    private Rect mDividerBound = new Rect();        //Divider bound
    private Rect mRootViewBound = new Rect();       //Main/Root view bound

    //Theme attributes
    private int mPinCodeLength;                     //PIN code length
    private float mKeyPadding;                      //Surround padding to each single key
    private float mKeyTextSize;                     //Surround padding to each single key
    private float mKeyStrokeWidth;                   //Surround padding to each single key
    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.
    @ColorInt
    private int mKeyStrokeColor;                    //KeyCircle background stroke color
    @ColorInt
    private int mKeyTextColor;                      //KeyCircle text color
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
    private TextPaint mKeyTextPaint;                //KeyCircle text paint
    private Paint mKeyPaint;                        //KeyCircle background pain
    private Paint mDividerPaint;                    //Horizontal divider paint color
    private Paint mEmptyIndicatorPaint;             //Empty indicator color
    private Paint mSolidIndicatorPaint;             //Solid indicator color

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
    private void init(@NonNull Context context, AttributeSet attrs) {
        mContext = context;

        if (attrs != null) {
            TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinView, 0, 0);
            try {
                mTitle = a.hasValue(R.styleable.PinView_titleText) ? a.getString(R.styleable.PinView_titleText) : Defaults.DEF_TITLE_TEXT;
                mDividerColor = a.getColor(R.styleable.PinView_dividerColor, Defaults.DEF_DIVIDER_COLOR);
                mIndicatorFilledColor = a.getColor(R.styleable.PinView_indicatorSolidColor, Defaults.DEF_INDICATOR_FILLED_COLOR);
                mIndicatorStrokeColor = a.getColor(R.styleable.PinView_indicatorStrokeColor, Defaults.DEF_INDICATOR_STROKE_COLOR);
                mKeyTextColor = a.getColor(R.styleable.PinView_keyTextColor, Defaults.DEF_KEY_TEXT_COLOR);
                mKeyStrokeColor = a.getColor(R.styleable.PinView_keyStrokeColor, Defaults.DEF_KEY_BACKGROUND_COLOR);
                mKeyTextSize = a.getDimensionPixelSize(R.styleable.PinView_keyTextSize, (int) mContext.getResources().getDimension(R.dimen.key_text_size));
                mTitleColor = a.getColor(R.styleable.PinView_titleTextColor, Defaults.DEF_TITLE_TEXT_COLOR);
                mPinCodeLength = a.getInteger(R.styleable.PinView_pinLength, Defaults.DEF_PIN_LENGTH);
                mKeyStrokeWidth = a.getDimension(R.styleable.PinView_keyStrokeWidth, mContext.getResources().getDimension(R.dimen.key_stroke_width));
            } finally {
                a.recycle();
            }
        } else {
            mTitle = Defaults.DEF_TITLE_TEXT;
            mDividerColor = Defaults.DEF_DIVIDER_COLOR;
            mIndicatorFilledColor = Defaults.DEF_INDICATOR_FILLED_COLOR;
            mIndicatorStrokeColor = Defaults.DEF_INDICATOR_STROKE_COLOR;
            mKeyTextColor = Defaults.DEF_KEY_TEXT_COLOR;
            mKeyStrokeColor = Defaults.DEF_KEY_BACKGROUND_COLOR;
            mKeyTextSize = mContext.getResources().getDimension(R.dimen.key_text_size);
            mTitleColor = Defaults.DEF_TITLE_TEXT_COLOR;
            mPinCodeLength = Defaults.DEF_PIN_LENGTH;
            mKeyStrokeWidth = mContext.getResources().getDimension(R.dimen.key_stroke_width);
        }
        mKeyPadding = mContext.getResources().getDimension(R.dimen.key_padding);

        prepareKeyTextPaint();
        prepareKeyBgPaint();
        prepareDividerPaint();
        prepareIndicatorPaint();
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

        invalidate();
    }

    private void prepareDividerPaint() {
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mDividerColor);

        invalidate();
    }

    private void prepareKeyBgPaint() {
        mKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mKeyPaint.setStyle(Paint.Style.STROKE);
        mKeyPaint.setColor(mKeyStrokeColor);
        mKeyPaint.setTextSize(mKeyTextSize);
        mKeyPaint.setStrokeWidth(mKeyStrokeWidth);

        invalidate();
    }

    private void prepareKeyTextPaint() {
        mKeyTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mKeyTextPaint.setColor(mKeyTextColor);
        mKeyTextPaint.setTextSize(mKeyTextSize);
        mKeyTextPaint.setFakeBoldText(true);
        mKeyTextPaint.setTextAlign(Paint.Align.CENTER);

        invalidate();
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW DRAW
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawKeys(canvas);
        drawDivider(canvas);
        drawIndicatorDots(canvas);
    }

    private void drawIndicatorDots(Canvas canvas) {
        for (int i = 0; i < mPinCodeLength; i++) {
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

    private void drawKeys(Canvas canvas) {
        for (Key key : mKeys) {
            if (key.getDigit().isEmpty()) continue; //Don't draw the empty button
            key.draw(canvas, mKeyPaint, mKeyTextPaint);
        }
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        measureMainView(widthMeasureSpec, heightMeasureSpec);
        measureKeyboard();
        measureDivider();
        measureIndicators();

        setMeasuredDimension(mRootViewBound.width(), mRootViewBound.height());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Measure the root view and get bounds.
     */
    private void measureMainView(int widthMeasureSpec, int heightMeasureSpec) {
        int[] l = new int[2];
        getLocationOnScreen(l);

        mRootViewBound.left = l[0];
        mRootViewBound.top = l[1];
        mRootViewBound.right = mRootViewBound.left + MeasureSpec.getSize(widthMeasureSpec);
        mRootViewBound.bottom = mRootViewBound.top + MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * Measure the dots indicators.
     */
    private void measureIndicators() {
        int indicatorWidth = 2 * (int) (mContext.getResources().getDimension(R.dimen.indicator_radius) + mContext.getResources().getDimension(R.dimen.indicator_padding));
        int totalSpace = indicatorWidth * mPinCodeLength;

        Rect dotsIndicatorBound = new Rect();
        dotsIndicatorBound.left = (mRootViewBound.width() - totalSpace) / 2;
        dotsIndicatorBound.right = dotsIndicatorBound.left + totalSpace;
        dotsIndicatorBound.bottom = (int) (mDividerBound.top - mContext.getResources().getDimension(R.dimen.divider_horizontal_margin));
        dotsIndicatorBound.top = dotsIndicatorBound.bottom - indicatorWidth;

        mDotsIndicator = new ArrayList<>(mPinCodeLength);
        for (int i = 0; i < mPinCodeLength; i++) {
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
        mDividerBound.top = (int) (mKeyBoardBound.top - mContext.getResources().getDimension(R.dimen.divider_vertical_margin));
        mDividerBound.bottom = (int) (mKeyBoardBound.top - mContext.getResources().getDimension(R.dimen.divider_vertical_margin));
    }

    private void measureKeyboard() {
        mKeyBoardBound.left = mIsOneHandOperation ? (int) (mRootViewBound.width() * 0.3) : 0;
        mKeyBoardBound.right = mRootViewBound.width();
        mKeyBoardBound.top = (int) (mRootViewBound.height() - (mRootViewBound.height() * Defaults.KEY_BOARD_PROPORTION));
        mKeyBoardBound.bottom = mRootViewBound.height();


        float singleKeyHeight = mKeyBoardBound.height() / Defaults.NO_OF_ROWS;
        float singleKeyWidth = mKeyBoardBound.width() / Defaults.NO_OF_COLUMNS;

        mKeys = new ArrayList<>();
        for (int colNo = 0; colNo < Defaults.NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < Defaults.NO_OF_ROWS; rowNo++) {
                Rect rect = new Rect();
                rect.left = (int) ((colNo * singleKeyWidth) + mKeyBoardBound.left);
                rect.right = (int) (rect.left + singleKeyWidth);
                rect.top = (int) ((rowNo * singleKeyHeight) + mKeyBoardBound.top);
                rect.bottom = (int) (rect.top + singleKeyHeight);

                mKeys.add(new KeyRect(this, Defaults.KEY_VALUES[mKeys.size()], rect));
            }
        }
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
                findKeyPressed(mDownKeyX,
                        mDownKeyY,
                        event.getX(),
                        event.getY());
                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Find which key is pressed based on the ACTION_DOWN and ACTION_UP coordinates.
     *
     * @param downEventX ACTION_DOWN event X coordinate
     * @param downEventY ACTION_DOWN event Y coordinate
     * @param upEventX   ACTION_UP event X coordinate
     * @param upEventY   ACTION_UP event Y coordinate
     */
    private void findKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY) {
        //figure out down key.
        for (Key key : mKeys) {

            if (key.checkKeyPressed(downEventX, downEventY, upEventX, upEventY)) {

                //Update the typed passcode
                onKeyPressed(key.getDigit());
                break;
            }
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Handle the newly added key digit. Append the digit to {@link #mPinTyped}.
     * If the new digit is {@link Defaults#BACKSPACE_TITLE}, remove the last digit of the {@link #mPinTyped}.
     * If the {@link #mPinTyped} has length of {@link #mPinCodeLength} and equals to {@link #mPinToCheck}
     * notify application as authenticated.
     *
     * @param newDigit newly pressed digit
     */
    private void onKeyPressed(@NonNull String newDigit) {
        //Check for the state
        if (mPinChangeListener == null) {
            throw new IllegalStateException("Set PinChangeListener to receive callbacks.");
        } else if (mPinToCheck.isEmpty() || mPinToCheck.length() != mPinCodeLength) {
            throw new IllegalStateException("Please set current PIN to check with the entered value.");
        }

        if (newDigit.equals(Defaults.BACKSPACE_TITLE)) {
            if (!mPinTyped.isEmpty()) mPinTyped = mPinTyped.substring(0, mPinTyped.length() - 1);
        } else {
            mPinTyped = mPinTyped + newDigit;
        }
        invalidate();

        if (mPinTyped.length() == mPinCodeLength) {

            if (mPinToCheck.equals(mPinTyped)) {
                mPinChangeListener.onAuthenticationSuccessful();
            } else {
                mPinChangeListener.onAuthenticationFailed();

                //Vibrate all the keys.
                for (Key key : mKeys) key.playError();
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

    public float getKeyPadding() {
        return mKeyPadding;
    }

    public void setKeyPadding(float keyPadding) {
        mKeyPadding = keyPadding;
        invalidate();
    }

    public int getPinCodeLength() {
        return mPinCodeLength;
    }

    public void setPinCodeLength(int pinCodeLength) {
        mPinCodeLength = pinCodeLength;
        requestLayout();
        invalidate();
    }

    public boolean isOneHandOperationEnabled() {
        return mIsOneHandOperation;
    }

    public void enableOneHandOperation(boolean isEnable) {
        mIsOneHandOperation = isEnable;
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
    public PinChangeListener getPinChangeListener() {
        return mPinChangeListener;
    }

    public void setPinChangeListener(@NonNull PinChangeListener pinChangeListener) {
        mPinChangeListener = pinChangeListener;
    }

    public int getKeyBackgroundColor() {
        return mKeyStrokeColor;
    }

    public void setKeyBackgroundColor(@ColorInt int keyBackgroundColor) {
        mKeyStrokeColor = keyBackgroundColor;
        prepareKeyBgPaint();
    }

    public int getKeyTextColor() {
        return mKeyTextColor;
    }

    public void setKeyTextColor(@ColorInt int keyTextColor) {
        mKeyTextColor = keyTextColor;
        prepareKeyTextPaint();
    }

    public int getIndicatorStrokeColor() {
        return mIndicatorStrokeColor;
    }

    public void setIndicatorStrokeColor(@ColorInt int indicatorStrokeColor) {
        mIndicatorStrokeColor = indicatorStrokeColor;
        prepareIndicatorPaint();
    }

    public int getIndicatorFilledColor() {
        return mIndicatorFilledColor;
    }

    public void setIndicatorFilledColor(@ColorInt int indicatorFilledColor) {
        mIndicatorFilledColor = indicatorFilledColor;
        prepareIndicatorPaint();
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        mDividerColor = dividerColor;
        prepareDividerPaint();
    }

    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(@ColorInt int titleColor) {
        mTitleColor = titleColor;
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
        requestLayout();
        invalidate();
    }
}
