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

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 */

public class PINView extends View {
    private static final int NO_OF_COLUMNS = 3;
    private static final int NO_OF_ROWS = 4;
    private static final float KEY_BOARD_PROPOTION = 0.7F;
    private static final String[] KEY_VALUES = new String[]{"1", "4", "7", "", "2", "5", "8", "0", "3", "6", "9", "-1"};

    private static final int DEF_PIN_LENGTH = 4;
    private static final float DEF_KEY_PADDING = 20f;
    private static final float DEF_KEY_TEXT_SIZE = 100f;

    private Context mContext;
    private float mDownKeyX;    //X coordinate of the ACTION_DOWN point
    private float mDownKeyY;    //Y coordinate of the ACTION_DOWN point
    private ArrayList<Key> mKeys;   //List of al the keys
    private String mPinTyped = "";  //PIN typed.

    //Theme attributes
    private int mPinCodeLength = DEF_PIN_LENGTH;    //PIN code length
    private float mKeyPadding = DEF_KEY_PADDING;    //Surround padding to each single key
    private float mKeyTextSize = DEF_KEY_TEXT_SIZE;    //Surround padding to each single key
    private boolean mIsOneHandOperation = false;

    //Paints
    private TextPaint mKeyTextPaint;
    private Paint mKeyPaint;

    //Cached measurements
    private int mViewHeight;    //Height of the whole key
    private int mViewWidth;     //Width of the whole key

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


    private void init(@NonNull Context context) {
        mContext = context;

        prepareKeyTextPaint();
        prepareKeyBgPaint();
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawKeys(canvas);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);

        measureKeys(mIsOneHandOperation ? (float) (mViewWidth * 0.3) : 0,
                mViewWidth,
                mViewHeight - (mViewHeight * KEY_BOARD_PROPOTION),
                mViewHeight);

        setMeasuredDimension(mViewWidth, mViewHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureKeys(float keyboardLeft,
                             float keyboardRight,
                             float keyboardTop,
                             float keyboardBottom) {
        float singleKeyHeight = (keyboardBottom - keyboardTop) / NO_OF_ROWS;
        float singleKeyWidth = (keyboardRight - keyboardLeft) / NO_OF_COLUMNS;

        mKeys = new ArrayList<>();
        for (int colNo = 0; colNo < NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < NO_OF_ROWS; rowNo++) {
                Rect rect = new Rect();
                rect.left = (int) ((colNo * singleKeyWidth) + keyboardLeft);
                rect.right = (int) (rect.left + singleKeyWidth);
                rect.top = (int) ((rowNo * singleKeyHeight) + keyboardTop);
                rect.bottom = (int) (rect.top + singleKeyHeight);

                Key key = new Key(KEY_VALUES[mKeys.size()], rect);
                mKeys.add(key);
            }
        }
    }


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
            mPinTyped = mPinTyped + upKey;
        }
    }

    public void reset() {
        mPinTyped = "";
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
}
