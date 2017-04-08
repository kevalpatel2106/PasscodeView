package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextPaint;

/**
 * Created by Keval on 07-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class FingerPrintBox implements FingerPrintAuthHelper.FingerPrintAuthCallback {
    private static final String DEF_FINGERPRINT_STATUS = "Scan your finger to authenticate";

    private Context mContext;
    private Boolean isFingerPrintBoxVisible;
    private Rect mBounds = new Rect();

    private TextPaint mStatusTextPaint;
    @ColorInt
    private int mStatusTextColor;
    @NonNull
    private String mStatusText = DEF_FINGERPRINT_STATUS;


    public FingerPrintBox(@NonNull Context context) {
        mContext = context;
        FingerPrintAuthHelper.getHelper(mContext, this);
        isFingerPrintBoxVisible = FingerPrintUtils.isFingerPrintEnrolled(mContext);

        prepareStatusTextPaint();
    }

    private void prepareStatusTextPaint() {
        mStatusTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mStatusTextPaint.setTextAlign(Paint.Align.CENTER);
        mStatusTextPaint.setColor(mStatusTextColor);
    }

    void measureFingerPrintBox(Rect rootViewBound) {
        if (isFingerPrintBoxVisible) {
            mBounds.left = rootViewBound.left;
            mBounds.right = rootViewBound.right;
            mBounds.top = (int) (rootViewBound.top + rootViewBound.height() * (1 - KeyPadBox.KEY_BOARD_BOTTOM_WEIGHT));
            mBounds.top = rootViewBound.bottom;
        }
    }

    void drawFingerPrintBox(Canvas canvas) {
        if (isFingerPrintBoxVisible) {
            canvas.drawText(mStatusText,
                    mBounds.exactCenterX(),
                    mBounds.exactCenterY()- (mStatusTextPaint.descent() + mStatusTextPaint.ascent()) / 2,
                    mStatusTextPaint);
        }
    }


    @Override
    public void onNoFingerPrintHardwareFound() {

    }

    @Override
    public void onNoFingerPrintRegistered() {

    }

    @Override
    public void onBelowMarshmallow() {

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {

    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

    }

    public String getmStatusText() {
        return mStatusText;
    }

    public void setmStatusText(String mStatusText) {
        this.mStatusText = mStatusText;
    }

    public int getmStatusTextColor() {
        return mStatusTextColor;
    }

    public void setmStatusTextColor(int mStatusTextColor) {
        this.mStatusTextColor = mStatusTextColor;
    }
}
