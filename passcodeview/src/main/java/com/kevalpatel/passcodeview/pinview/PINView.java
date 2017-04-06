package com.kevalpatel.passcodeview.pinview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.kevalpatel.passcodeview.PinChangeListener;
import com.kevalpatel.passcodeview.R;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 */

public class PINView extends View {
    private Context mContext;
    private float mDownKeyX;                        //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;                        //Y coordinate of the ACTION_DOWN point

    private PinChangeListener mPinChangeListener;
    private String mPinToCheck;
    @NonNull
    private String mPinTyped = "";                  //PIN typed.

    private ArrayList<Key> mKeys;                   //List of al the keys
    private ArrayList<Indicator> mDotsIndicator;    //List of al the keys

    private Rect mKeyBoardBound = new Rect();
    private Rect mDividerBound = new Rect();
    private Rect mRootViewBound = new Rect();

    //Theme attributes
    private int mPinCodeLength = Defults.DEF_PIN_LENGTH;    //PIN code length
    private float mKeyPadding;                              //Surround padding to each single key
    private float mKeyTextSize;                             //Surround padding to each single key
    private boolean mIsOneHandOperation = false;            //Bool to set true if you want to display one hand key board.

    //Paints
    private TextPaint mKeyTextPaint;
    private Paint mKeyPaint;
    private Paint mDividerPaint;
    private Paint mEmptyIndicatorPaint;
    private Paint mSolidIndicatorPaint;

    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public PINView(Context context) {
        super(context);
        init(context);
    }

    public PINView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PINView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PINView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS
    ///////////////////////////////////////////////////////////////

    private void init(@NonNull Context context) {
        mContext = context;

        mKeyTextSize = mContext.getResources().getDimension(R.dimen.key_text_size);
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
        mEmptyIndicatorPaint.setColor(Color.BLUE);
        mEmptyIndicatorPaint.setStrokeWidth(mContext.getResources().getDimension(R.dimen.indicator_stroke_width));

        //Set filled dot paint
        mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidIndicatorPaint.setColor(Color.RED);
    }

    private void prepareDividerPaint() {
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(Color.RED);
    }

    private void prepareKeyBgPaint() {
        mKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mKeyPaint.setColor(Color.RED);
        mKeyPaint.setTextSize(mKeyTextSize);
    }

    private void prepareKeyTextPaint() {
        mKeyTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mKeyTextPaint.setColor(Color.BLACK);
        mKeyTextPaint.setTextSize(mKeyTextSize);
        mKeyTextPaint.setTextAlign(Paint.Align.CENTER);
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
            Indicator indicator = mDotsIndicator.get(i);
            canvas.drawCircle(indicator.getBound().exactCenterX(),
                    indicator.getBound().exactCenterY(),
                    mContext.getResources().getDimension(R.dimen.indicator_radius),
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

            //Draw circle background
            canvas.drawCircle(key.getBounds().exactCenterX(),   //Set center width of key
                    key.getBounds().exactCenterY(),             //Set center height of key
                    Math.min(key.getBounds().height(), key.getBounds().width()) / 2 - mKeyPadding, //radius = height or width - padding for single key
                    mKeyPaint);

            //Draw key text
            canvas.drawText(key.getDigit() + "",                //Text to display on key
                    key.getBounds().exactCenterX(),             //Set start point at center width of key
                    key.getBounds().exactCenterY() - (mKeyTextPaint.descent() + mKeyPaint.ascent()) / 2,    //center height of key - text height/2
                    mKeyTextPaint);
        }
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureManiView(widthMeasureSpec, heightMeasureSpec);
        measureKeyboard();
        measureDivider();
        measureIndicators();

        setMeasuredDimension(mRootViewBound.width(), mRootViewBound.height());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureIndicators() {
        int indicatorWidth = 2 * (int) (mContext.getResources().getDimension(R.dimen.indicator_radius)
                + mContext.getResources().getDimension(R.dimen.indicator_padding));
        int totalSpace = indicatorWidth * mPinCodeLength;

        Rect dotsIndicatorBound = new Rect();
        dotsIndicatorBound.left = (mRootViewBound.width() - totalSpace) / 2;
        dotsIndicatorBound.right = dotsIndicatorBound.left + totalSpace;
        dotsIndicatorBound.bottom = mDividerBound.top - 20;
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

    private void measureDivider() {
        mDividerBound.left = mRootViewBound.left + 20;
        mDividerBound.right = mRootViewBound.right - 20;
        mDividerBound.top = mKeyBoardBound.top - 20;
        mDividerBound.bottom = mKeyBoardBound.top - 20;
    }

    private void measureManiView(int widthMeasureSpec, int heightMeasureSpec) {
        int[] l = new int[2];
        getLocationOnScreen(l);

        mRootViewBound.left = l[0];
        mRootViewBound.top = l[1];
        mRootViewBound.right = mRootViewBound.left + MeasureSpec.getSize(widthMeasureSpec);
        mRootViewBound.bottom = mRootViewBound.top + MeasureSpec.getSize(heightMeasureSpec);
    }

    private void measureKeyboard() {
        mKeyBoardBound.left = mIsOneHandOperation ? (int) (mRootViewBound.width() * 0.3) : 0;
        mKeyBoardBound.right = mRootViewBound.width();
        mKeyBoardBound.top = (int) (mRootViewBound.height() - (mRootViewBound.height() * Defults.KEY_BOARD_PROPORTION));
        mKeyBoardBound.bottom = mRootViewBound.height();


        float singleKeyHeight = mKeyBoardBound.height() / Defults.NO_OF_ROWS;
        float singleKeyWidth = mKeyBoardBound.width() / Defults.NO_OF_COLUMNS;

        mKeys = new ArrayList<>();
        for (int colNo = 0; colNo < Defults.NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < Defults.NO_OF_ROWS; rowNo++) {
                Rect rect = new Rect();
                rect.left = (int) ((colNo * singleKeyWidth) + mKeyBoardBound.left);
                rect.right = (int) (rect.left + singleKeyWidth);
                rect.top = (int) ((rowNo * singleKeyHeight) + mKeyBoardBound.top);
                rect.bottom = (int) (rect.top + singleKeyHeight);

                Key key = new Key(Defults.KEY_VALUES[mKeys.size()], rect);
                mKeys.add(key);
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

    private void findKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY) {
        String downKey = "";
        String upKey = "";

        //figure out down key.
        for (Key key : mKeys) {
            //Check if the click is between the width bounds
            if (downEventX > key.getBounds().left
                    && downEventX < key.getBounds().left + key.getBounds().width()) {

                //Check if the click is between the height bounds
                if (downEventY > key.getBounds().top
                        && downEventY < key.getBounds().top + key.getBounds().height()) {
                    downKey = key.getDigit();
                    break;
                }
            }
        }

        //figure out up key.
        for (Key key : mKeys) {
            //Check if the click is between the width bounds
            if (upEventX > key.getBounds().left
                    && upEventX < key.getBounds().left + key.getBounds().width()) {

                //Check if the click is between the height bounds
                if (upEventY > key.getBounds().top
                        && upEventY < key.getBounds().top + key.getBounds().height()) {
                    upKey = key.getDigit();
                    break;
                }
            }
        }

        //Check if the up and down key are same? And also blank key is not pressed.
        if (!upKey.isEmpty() && downKey.equals(upKey)) {
            onKeyPressed(upKey);
        }
    }

    ///////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////

    private void onKeyPressed(String newDigit) {
        //Check for the state
        if (mPinChangeListener == null) {
            throw new IllegalStateException("Set PinChangeListener to receive callbacks.");
        } else if (mPinToCheck.isEmpty() || mPinToCheck.length() != mPinCodeLength) {
            throw new IllegalStateException("Please set current PIN to check with the entered value.");
        }

        mPinTyped = mPinTyped + newDigit;
        invalidate();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mPinTyped.length() == mPinCodeLength) {
                    if (mPinToCheck.equals(mPinTyped)) {
                        mPinChangeListener.onAuthenticationSuccessful();
                    } else {
                        mPinChangeListener.onAuthenticationFailed();
                    }

                    reset();
                }
            }
        }, 800);
    }

    public void reset() {
        mPinTyped = "";
        invalidate();
    }

    public float getKeyPadding() {
        return mKeyPadding;
    }

    public void setKeyPadding(float keyPadding) {
        mKeyPadding = keyPadding;
    }

    public int getPinCodeLength() {
        return mPinCodeLength;
    }

    public void setPinCodeLength(int pinCodeLength) {
        mPinCodeLength = pinCodeLength;
    }

    public float getKeyTextSize() {
        return mKeyTextSize;
    }

    public void setKeyTextSize(float keyTextSize) {
        mKeyTextSize = keyTextSize;
    }

    public boolean isOneHandOperationEnabled() {
        return mIsOneHandOperation;
    }

    public void enableOneHandOperation(boolean isEnable) {
        mIsOneHandOperation = isEnable;
    }

    @Nullable
    public String getPinToCheck() {
        return mPinToCheck;
    }

    public void setPinToCheck(String pinToCheck) {
        mPinToCheck = pinToCheck;
    }

    @Nullable
    public PinChangeListener getPinChangeListener() {
        return mPinChangeListener;
    }

    public void setPinChangeListener(PinChangeListener pinChangeListener) {
        mPinChangeListener = pinChangeListener;
    }
}
