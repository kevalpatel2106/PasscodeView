package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.text.TextPaint;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 */

class KeyBox {
    static final int KEY_TYPE_CIRCLE = 0;
    static final int KEY_TYPE_RECT = 1;

    private ArrayList<Key> mKeys;

    //Theme params
    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.
    private int mPinCodeLength;                     //PIN code length
    @Dimension
    private float mKeyPadding;                      //Surround padding to each single key
    @Dimension
    private float mKeyTextSize;                     //Surround padding to each single key
    @Dimension
    private float mKeyStrokeWidth;                   //Surround padding to each single key
    @ColorInt
    private int mKeyStrokeColor;                    //KeyCircle background stroke color
    @ColorInt
    private int mKeyTextColor;                      //KeyCircle text color
    private int mKeyShape = KEY_TYPE_CIRCLE;

    private final Context mContext;
    private final PinView mPinView;

    private Paint mKeyPaint;
    private TextPaint mKeyTextPaint;

    private Rect mKeyBoxBound = new Rect();

    KeyBox(Context context, PinView pinView) {
        mContext = context;
        mPinView = pinView;
        mKeyPadding = mContext.getResources().getDimension(R.dimen.key_padding);
    }

    void measureKeyboard(Rect rootViewBound) {
        mKeyBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mKeyBoxBound.right = rootViewBound.width();
        mKeyBoxBound.top = (int) (rootViewBound.height() - (rootViewBound.height() * Defaults.KEY_BOARD_PROPORTION));
        mKeyBoxBound.bottom = rootViewBound.height();


        float singleKeyHeight = mKeyBoxBound.height() / Defaults.NO_OF_ROWS;
        float singleKeyWidth = mKeyBoxBound.width() / Defaults.NO_OF_COLUMNS;

        mKeys = new ArrayList<>();
        for (int colNo = 0; colNo < Defaults.NO_OF_COLUMNS; colNo++) {

            for (int rowNo = 0; rowNo < Defaults.NO_OF_ROWS; rowNo++) {
                Rect keyBound = new Rect();
                keyBound.left = (int) ((colNo * singleKeyWidth) + mKeyBoxBound.left);
                keyBound.right = (int) (keyBound.left + singleKeyWidth);
                keyBound.top = (int) ((rowNo * singleKeyHeight) + mKeyBoxBound.top);
                keyBound.bottom = (int) (keyBound.top + singleKeyHeight);

                switch (mKeyShape){
                    case KEY_TYPE_CIRCLE:
                        mKeys.add(new KeyCircle(mPinView, Defaults.KEY_VALUES[mKeys.size()], keyBound, mKeyPadding));
                        break;
                    case KEY_TYPE_RECT:
                        mKeys.add(new KeyRect(mPinView, Defaults.KEY_VALUES[mKeys.size()], keyBound, mKeyPadding));
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid key shape.");
                }
            }
        }
    }


    void setDefaults() {
        mKeyTextColor = Defaults.DEF_KEY_TEXT_COLOR;
        mKeyStrokeColor = Defaults.DEF_KEY_BACKGROUND_COLOR;
        mKeyTextSize = mContext.getResources().getDimension(R.dimen.key_text_size);
        mPinCodeLength = Defaults.DEF_PIN_LENGTH;
        mKeyStrokeWidth = mContext.getResources().getDimension(R.dimen.key_stroke_width);
    }

    void prepareKeyBgPaint() {

        mKeyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mKeyPaint.setStyle(Paint.Style.STROKE);
        mKeyPaint.setColor(mKeyStrokeColor);
        mKeyPaint.setTextSize(mKeyTextSize);
        mKeyPaint.setStrokeWidth(mKeyStrokeWidth);
    }

    void prepareKeyTextPaint() {

        mKeyTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mKeyTextPaint.setColor(mKeyTextColor);
        mKeyTextPaint.setTextSize(mKeyTextSize);
        mKeyTextPaint.setFakeBoldText(true);
        mKeyTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    void drawKeys(Canvas canvas) {
        for (Key key : mKeys) {
            if (key.getDigit().isEmpty()) continue; //Don't draw the empty button
            key.draw(canvas, mKeyPaint, mKeyTextPaint);
        }
    }

    ArrayList<Key> getKeys() {
        return mKeys;
    }

    Rect getBounds() {
        return mKeyBoxBound;
    }

    int getKeyBackgroundColor() {
        return mKeyStrokeColor;
    }

    void setKeyBackgroundColor(@ColorInt int keyBackgroundColor) {
        mKeyStrokeColor = keyBackgroundColor;
    }

    int getKeyTextColor() {
        return mKeyTextColor;
    }

    void setKeyTextColor(@ColorInt int keyTextColor) {
        mKeyTextColor = keyTextColor;
    }

    int getPinCodeLength() {
        return mPinCodeLength;
    }

    void setPinCodeLength(int pinCodeLength) {
        mPinCodeLength = pinCodeLength;
    }

    float getKeyPadding() {
        return mKeyPadding;
    }

    void setKeyPadding(float keyPadding) {
        mKeyPadding = keyPadding;
    }

    void setKeyTextSize(float keyTextSize) {
        mKeyTextSize = keyTextSize;
    }

    void setKeyStrokeWidth(float keyStrokeWidth) {
        mKeyStrokeWidth = keyStrokeWidth;
    }

    boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    void setOneHandOperation(boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }

    int getKeyShape() {
        return mKeyShape;
    }

    void setKeyShape(int keyShape) {
        mKeyShape = keyShape;
    }
}
