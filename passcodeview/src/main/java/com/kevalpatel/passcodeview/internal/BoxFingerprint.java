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
import android.support.annotation.Dimension;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.animation.CycleInterpolator;

import com.kevalpatel.passcodeview.Constants;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;

/**
 * Created by Keval on 07-Apr-17.
 * This class create box for the finger print authentication. This box contains the finger print scanning
 * icon and the {@link android.widget.TextView} to display the status messages for the fingerprint
 * scanning.
 * <p>
 * ...............................................
 * .................             .................
 * .................    Icon     .................
 * .................             .................
 * ...............................................
 * ................. Status text .................
 * ...............................................
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

final class BoxFingerprint extends Box implements FingerPrintAuthHelper.FingerPrintAuthCallback {
    private static final long ANIMATION_DURATION = 1000;    //1 second

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
     * Generally the box gets displayed at the bottom of the view.
     */
    private Rect mBounds = new Rect();

    /**
     * Color of the finger print status messages.
     */
    @ColorInt
    private int mStatusTextColor;

    /**
     * Size of the finger print status text. This size is in dp.
     */
    @Dimension
    private float mStatusTextSize;

    /**
     * The text to display when fingerprint scanner is not scanning the fingerprint. When any warning
     * or error occurs, the error/warning message will replace this in the status text.
     */
    private String mNormalStatusText;

    /**
     * The currently displaying status message in the box.
     */
    private String mCurrentStatusText;

    /**
     * Paint for the status text. This paint will draw the text with {@link #mStatusTextSize} size
     * and {@link #mStatusTextColor} color.
     *
     * @see Paint
     */
    private TextPaint mStatusTextPaint;

    /**
     * Helper class that handles the authentication using the fingerprint.
     *
     * @see FingerPrintAuthHelper
     */
    @Nullable
    private FingerPrintAuthHelper mFingerPrintAuthHelper;


    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    /**
     * Public constructor.
     *
     * @param basePasscodeView {@link BasePasscodeView} that contains this box.
     */
    public BoxFingerprint(@NonNull BasePasscodeView basePasscodeView) {
        super(basePasscodeView);
    }

    ///////////////////////////////////////////////////////////////
    //                  Handle lifecycle callbacks
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize the box.
     */
    @Override
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
     * Prepare all the paints.
     */
    @Override
    public void preparePaint() {
        //Status text paint.
        mStatusTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mStatusTextPaint.setTextAlign(Paint.Align.CENTER);
        mStatusTextPaint.setTextSize(mStatusTextSize);
        mStatusTextPaint.setColor(mStatusTextColor);
    }

    /**
     * Set the default values for {@link #mStatusTextColor}, {@link #mNormalStatusText} and
     * {@link #mStatusTextSize}.
     */
    @Override
    public void setDefaults() {
        mStatusTextSize = getContext().getResources().getDimension(R.dimen.lib_fingerprint_status_text_size);

        mNormalStatusText = DEF_FINGERPRINT_STATUS;
        mCurrentStatusText = mNormalStatusText;

        mStatusTextColor = Utils.getColorCompat(getContext(), R.color.lib_key_default_color);
    }

    /**
     * Draw the box on the canvas.
     *
     * @param canvas {@link Canvas} on which the view will be drawn.
     */
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
     * @param canvas {@link Canvas} on which the text will be drawn.
     */
    private void drawStatusText(@NonNull Canvas canvas) {
        canvas.drawText(mCurrentStatusText,
                mBounds.exactCenterX(),
                (float) (mBounds.top + (mBounds.height() / 1.3) - ((mStatusTextPaint.descent() + mStatusTextPaint.ascent()) / 2)),
                mStatusTextPaint);
    }

    /**
     * Draw the status text.
     *
     * @param canvas {@link Canvas} on which the fingerprint will be drawn.
     */
    private void drawFingerPrintIcon(@NonNull Canvas canvas) {
        Drawable d = getContext().getResources().getDrawable(R.drawable.ic_fingerprint);
        d.setBounds((int) (mBounds.exactCenterX() - mBounds.height() / 4),
                mBounds.top + 15,
                (int) (mBounds.exactCenterX() + mBounds.height() / 4),
                mBounds.top + mBounds.height() / 2 + 15);
        d.setColorFilter(new PorterDuffColorFilter(mStatusTextPaint.getColor(), PorterDuff.Mode.SRC_ATOP));
        d.draw(canvas);
    }

    /**
     * Measure the box bounds.
     *
     * @param rootViewBounds {@link Rect} with the bounds of the root view.
     */
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

    /**
     * Handle the fingerprint authentication failure. When fingerprint authentication fails, the
     * status text color becomes {@link Color#RED} and shakes the view for 1 seconds.
     *
     * @see #playErrorAnimation()
     */
    @Override
    public void onAuthenticationFail() {
        //Change the color to red.
        mStatusTextPaint.setColor(Color.RED);

        //Shake the view.
        ValueAnimator animator = playErrorAnimation();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Do nothing
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                //Return back to the normal view
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCurrentStatusText = mNormalStatusText;
                        if (mStatusTextPaint != null) mStatusTextPaint.setColor(mStatusTextColor);
                        getRootView().invalidate();
                    }
                }, ANIMATION_DURATION /* After 1 second */);

                getRootView().onAuthenticationFail();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Do nothing
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Do nothing
            }
        });
        animator.start();
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

    @Override
    public void onAuthenticationSuccess() {
        mCurrentStatusText = "Fingerprint recognized.";

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentStatusText = mNormalStatusText;
                getRootView().onAuthenticationSuccess();
                getRootView().invalidate();
            }
        }, ANIMATION_DURATION);
    }

    @Override
    public void reset() {
        if (mFingerPrintAuthHelper != null && mFingerPrintAuthHelper.isScanning())
            mFingerPrintAuthHelper.stopAuth();
    }

    ///////////////// FINGERPRINT AUTHENTICATION CALLBACKS. //////////////

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

    ///////////////// SETTERS/GETTERS //////////////

    @NonNull
    String getStatusText() {
        return mNormalStatusText;
    }

    void setStatusText(@Nullable String statusText) {
        this.mNormalStatusText = statusText == null ? DEF_FINGERPRINT_STATUS : statusText;
        mCurrentStatusText = mNormalStatusText;
    }

    int getStatusTextColor() {
        return mStatusTextColor;
    }

    void setStatusTextColor(@ColorInt int statusTextColor) {
        this.mStatusTextColor = statusTextColor;
    }

    float getStatusTextSize() {
        return mStatusTextSize;
    }

    void setStatusTextSize(float statusTextSizePx) {
        this.mStatusTextSize = statusTextSizePx;
    }

    Boolean isFingerPrintEnable() {
        return isFingerPrintBoxVisible;
    }

    /**
     * Enable/Disable finger print scanning programmatically.
     *
     * @param isEnable true if the fingerprint scanning is enabled.
     */
    void setFingerPrintEnable(boolean isEnable) {
        this.isFingerPrintBoxVisible = isEnable && Utils.isFingerPrintEnrolled(getContext());
    }
}
