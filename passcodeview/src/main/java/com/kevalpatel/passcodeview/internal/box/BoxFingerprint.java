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

package com.kevalpatel.passcodeview.internal.box;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.CycleInterpolator;

import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;
import com.kevalpatel.passcodeview.internal.Constants;

/**
 * Created by Keval on 07-Apr-17.
 * This class create box for the finger print authentication. This is the internal class.
 * <p>
 * ...............................................
 * .................             .................
 * .................    Icon     .................
 * .................             .................
 * ...............................................
 * ................. Status text .................
 * ...............................................
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class BoxFingerprint extends Box implements FingerPrintAuthHelper.FingerPrintAuthCallback {

    /**
     * Default message for the finger print box.
     */
    private static final String DEF_FINGERPRINT_STATUS = "Scan your finger to authenticate";

    /**
     * Boolean to indicate weather the finger print box is visible or not? True indicates the finger
     * print authentication box is visible. False indicates the finger print authentication will be
     * disable and the box will be invisible.
     */
    private Boolean isFingerPrintBoxVisible;

    /**
     * {@link Rect} bound for the {@link BoxFingerprint}. The box will be drawn between these bounds.
     */
    private Rect mBounds = new Rect();

    /**
     * Color of the finger print status.
     */
    @ColorInt
    private int mStatusTextColor;

    /**
     * Size of the finger print status text. This size is in dp.
     */
    @Dimension
    private float mStatusTextSize;
    private String mNormalStatusText;

    private String mCurrentStatusText;

    /**
     * Paint for the status text.
     *
     * @see Paint
     */
    private TextPaint mStatusTextPaint;

    @Nullable
    private FingerPrintAuthHelper mFingerPrintAuthHelper;


    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public BoxFingerprint(@NonNull BasePasscodeView basePasscodeView) {
        super(basePasscodeView);
    }

    ///////////////////////////////////////////////////////////////
    //                  Handle lifecycle callbacks
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize the box.
     */
    public void init() {

        //Check if the finger print authentication is enabled?
        isFingerPrintBoxVisible = Utils.isFingerPrintEnrolled(getContext());

        //Initialize the finger print reader.
        if (isFingerPrintBoxVisible) {
            mFingerPrintAuthHelper = new FingerPrintAuthHelper(getContext(), this);
            mFingerPrintAuthHelper.startAuth();
        }
    }

    @Override
    public void parseTypeArr(@NonNull AttributeSet typedArray) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(typedArray,
                R.styleable.BasePasscodeView, 0, 0);

        setFingerPrintEnable(a.getBoolean(R.styleable.BasePasscodeView_fingerprintEnable, true));

        setStatusText(a.hasValue(R.styleable.BasePasscodeView_fingerprintDefaultText) ?
                a.getString(R.styleable.BasePasscodeView_fingerprintDefaultText) : null);

        setStatusTextColor(a.getColor(R.styleable.BasePasscodeView_fingerprintTextColor,
                Utils.getColorCompat(getContext(), R.color.lib_key_default_color)));

        setStatusTextSize(a.getDimension(R.styleable.BasePasscodeView_fingerprintTextSize,
                (int) getContext().getResources().getDimension(R.dimen.lib_fingerprint_status_text_size)));
    }

    /**
     * Prepare all the pains.
     */
    @Override
    public void preparePaint() {

        //Status text paint.
        mStatusTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mStatusTextPaint.setTextAlign(Paint.Align.CENTER);
        mStatusTextPaint.setTextSize(mStatusTextSize);
        mStatusTextPaint.setColor(mStatusTextColor);
    }

    @Override
    public void setDefaults() {
        mStatusTextSize = getContext().getResources().getDimension(R.dimen.lib_fingerprint_status_text_size);

        mNormalStatusText = DEF_FINGERPRINT_STATUS;
        mCurrentStatusText = mNormalStatusText;

        mStatusTextColor = Utils.getColorCompat(getContext(), R.color.lib_key_default_color);
    }


    @Override
    public void drawView(@NonNull Canvas canvas) {
        if (isFingerPrintBoxVisible) {

            //Show fingerprint icon
            drawFingerPrintIcon(canvas);

            //Show finger print text
            drawStatusText(canvas);
        }
    }

    /**
     * Draw the status text.
     *
     * @param canvas
     */
    private void drawStatusText(@NonNull Canvas canvas) {
        canvas.drawText(mCurrentStatusText,
                mBounds.exactCenterX(),
                (float) (mBounds.top + (mBounds.height() / 1.3) - ((mStatusTextPaint.descent() + mStatusTextPaint.ascent()) / 2)),
                mStatusTextPaint);
    }

    private void drawFingerPrintIcon(@NonNull Canvas canvas) {
        Drawable d = getContext().getResources().getDrawable(R.drawable.ic_fingerprint);
        d.setBounds((int) (mBounds.exactCenterX() - mBounds.height() / 4),
                mBounds.top + 15,
                (int) (mBounds.exactCenterX() + mBounds.height() / 4),
                mBounds.top + mBounds.height() / 2 + 15);
        d.setColorFilter(new PorterDuffColorFilter(mStatusTextPaint.getColor(), PorterDuff.Mode.SRC_ATOP));
        d.draw(canvas);
    }

    @Override
    public void measureView(@NonNull Rect rootViewBounds) {
        if (isFingerPrintBoxVisible) {

            //Get the bound of the box
            mBounds.left = rootViewBounds.left;
            mBounds.right = rootViewBounds.right;
            mBounds.top = (int) (rootViewBounds.bottom - rootViewBounds.height() * (Constants.KEY_BOARD_BOTTOM_WEIGHT));
            mBounds.bottom = rootViewBounds.bottom;
        } else {
            mBounds.setEmpty();
        }
    }

    @Override
    public void onAuthenticationFail() {
        //Change the color to red.
        mStatusTextPaint.setColor(Color.RED);

        //Shake the view.
        ValueAnimator animator = playErrorAnimation();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //Return back to the normal view
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentStatusText = mNormalStatusText;
                        mStatusTextPaint.setColor(mStatusTextColor);
                        getRootView().invalidate();
                    }
                }, 1000);

                getRootView().onAuthenticationFail();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @Override
    public void onAuthenticationSuccess() {
        mCurrentStatusText = "Fingerprint recognized.";
        getRootView().invalidate();

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentStatusText = mNormalStatusText;

                getRootView().onAuthenticationSuccess();
                getRootView().invalidate();
            }
        }, 1000);
    }

    @Override
    public void reset() {
        if (mFingerPrintAuthHelper != null && mFingerPrintAuthHelper.isScanning())
            mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onFingerprintAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        onAuthenticationSuccess();
    }

    @Override
    public void onFingerprintAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {
            case FingerPrintAuthHelper.CANNOT_RECOGNIZE_ERROR:
            case FingerPrintAuthHelper.NON_RECOVERABLE_ERROR:
            case FingerPrintAuthHelper.RECOVERABLE_ERROR:
                //Display the error message
                mCurrentStatusText = errorMessage;

                onAuthenticationFail();
                break;
        }
    }

    /**
     * Apply the error animations which will move key left to right and after right to left for two times.
     */
    private ValueAnimator playErrorAnimation() {
        ValueAnimator goLeftAnimator = ValueAnimator.ofInt(0, 10);
        goLeftAnimator.setInterpolator(new CycleInterpolator(2));
        goLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBounds.left += (int) animation.getAnimatedValue();
                mBounds.right += (int) animation.getAnimatedValue();
                getRootView().invalidate();
            }
        });
        return goLeftAnimator;
    }

    ///////////////// SETTERS/GETTERS //////////////

    @NonNull
    public String getStatusText() {
        return mNormalStatusText;
    }

    public void setStatusText(@Nullable String statusText) {
        this.mNormalStatusText = statusText == null ? DEF_FINGERPRINT_STATUS : statusText;
        mCurrentStatusText = mNormalStatusText;
    }

    public int getStatusTextColor() {
        return mStatusTextColor;
    }

    public void setStatusTextColor(@ColorInt int statusTextColor) {
        this.mStatusTextColor = statusTextColor;
    }

    public void setStatusTextColorRes(@ColorRes int statusTextColor) {
        this.mStatusTextColor = Utils.getColorCompat(getContext(), statusTextColor);
    }

    public float getStatusTextSize() {
        return mStatusTextSize;
    }

    public void setStatusTextSize(@DimenRes int statusTextSize) {
        this.mStatusTextSize = getContext().getResources().getDimension(statusTextSize);
    }

    public void setStatusTextSize(float statusTextSizePx) {
        this.mStatusTextSize = statusTextSizePx;
    }

    public Boolean isFingerPrintEnable() {
        return isFingerPrintBoxVisible;
    }

    /**
     * Enable/Disable finger print scanning programmatically.
     *
     * @param isEnable true if the fingerprint scanning is enabled.
     */
    public void setFingerPrintEnable(boolean isEnable) {
        this.isFingerPrintBoxVisible = isEnable && Utils.isFingerPrintEnrolled(getContext());
    }
}
