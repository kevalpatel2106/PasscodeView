package com.kevalpatel.passcodeview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.CycleInterpolator;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class KeyCircle extends Key {
    private static final int MAX_RIPPLE_ALPHA = 180;
    private static final int RIPPLE_DURATION = 350;


    private final String mDigit;                        //KeyCircle title.
    private final Rect mBounds;                         //KeyCircle bound.
    private final View mView;                           //Pin view
    private final float mKeyRadius;                     //Radius of the key background.

    private ValueAnimator mRippleValueAnimator;         //Ripple animator
    private boolean isRippleEffectRunning = false;      //Bool to indicate if the ripple effect is currently running?
    private int mCurrentRippleRadius = 0;               //Current ripple radius
    private int mCurrentAlpha;                          //Current ripple alpha.
    private Paint mRipplePaint;

    /**
     * Public constructor.
     *
     * @param view   {@link PinView}
     * @param digit  title of the key. (-1 for the backspace key)
     * @param bounds {@link Rect} bound.
     */
    KeyCircle(View view, String digit, Rect bounds, float keyPadding) {
        mDigit = digit;
        mBounds = bounds;
        mView = view;
        mKeyRadius = calculateKeyRadius(bounds, keyPadding);

        setRipplePaint();
        setUpAnimator();
    }

    /**
     * Set the ripple paint.
     */
    private void setRipplePaint(){
        mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRipplePaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Get the digit string.
     *
     * @return KeyCircle name
     */
    @Override
    String getDigit() {
        return mDigit;
    }

    /**
     * Initialise the filed and listener for ripple effect mRippleValueAnimator
     */
    private void setUpAnimator() {
        final int circleAlphaOffset = (int) (MAX_RIPPLE_ALPHA / mKeyRadius);

        mRippleValueAnimator = ValueAnimator.ofFloat(0, mKeyRadius);
        mRippleValueAnimator.setDuration(RIPPLE_DURATION);
        mRippleValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (isRippleEffectRunning) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    mCurrentRippleRadius = (int) animatedValue;
                    mCurrentAlpha = (int) (MAX_RIPPLE_ALPHA - (animatedValue * circleAlphaOffset));

                    mView.invalidate(mBounds);
                }
            }
        });
        mRippleValueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isRippleEffectRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRippleEffectRunning = false;
                mCurrentRippleRadius = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * Calculate the circle radius.
     *
     * @param bounds  rectangle bound.
     * @param padding surround padding to the key.
     * @return radius.
     */
    private float calculateKeyRadius(Rect bounds, float padding) {
        return Math.min(bounds.height(), bounds.width()) / 2 - padding;       //radius = height or width - padding for single key
    }

    /**
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    @Override
    void playClickAnimation() {
        mRippleValueAnimator.start();
    }

    /**
     * Show animation indicated invalid pin code
     */
    @Override
    void playError() {
        ValueAnimator goLeftAnimator = ValueAnimator.ofInt(0, 10);
        goLeftAnimator.setInterpolator(new CycleInterpolator(2));
        goLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBounds.left += (int) animation.getAnimatedValue();
                mBounds.right += (int) animation.getAnimatedValue();
                mView.invalidate();
            }
        });
        goLeftAnimator.start();
    }

    /**
     * Draw the key of canvas.
     *
     * @param canvas       canvas of the view o which key will be drawn
     * @param keyPaint     KeyCircle background paint
     * @param keyTextPaint KeyCircle text paint
     */
    @Override
    void draw(@NonNull Canvas canvas,
              @NonNull Paint keyPaint,
              @NonNull Paint keyTextPaint) {

        //Draw circle background
        canvas.drawCircle(mBounds.exactCenterX(),   //Set center width of key
                mBounds.exactCenterY(),             //Set center height of key
                mKeyRadius,
                keyPaint);

        if (getDigit().equals(Defaults.BACKSPACE_TITLE)) {  //Backspace key
            Drawable d = ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_back_space);
            d.setBounds((int) (mBounds.exactCenterX() - mKeyRadius / 2),
                    (int) (mBounds.exactCenterY() + mKeyRadius / 2),
                    (int) (mBounds.exactCenterX() + mKeyRadius / 2),
                    (int) (mBounds.exactCenterY() - mKeyRadius / 2));
            d.setColorFilter(new PorterDuffColorFilter(keyTextPaint.getColor(), PorterDuff.Mode.SRC_ATOP));
            d.draw(canvas);
        } else {
            //Draw key text
            canvas.drawText(getDigit() + "",                //Text to display on key
                    mBounds.exactCenterX(),             //Set start point at center width of key
                    mBounds.exactCenterY() - (keyTextPaint.descent() + keyPaint.ascent()) / 2,    //center height of key - text height/2
                    keyTextPaint);
        }

        //Play ripple effect if the key has ripple effect enabled.
        if (isRippleEffectRunning) {
            mRipplePaint.setAlpha(mCurrentAlpha);
            canvas.drawCircle(mBounds.exactCenterX(),
                    mBounds.exactCenterY(),
                    mCurrentRippleRadius,
                    mRipplePaint);
        }
    }

    /**
     * Check if the key is pressed or not? It will check if the touch x & y coordinates are inside
     * thw key bound or not?
     *
     * @param downEventX ACTION_DOWN event X coordinate
     * @param downEventY ACTION_DOWN event Y coordinate
     * @param upEventX   ACTION_UP event X coordinate
     * @param upEventY   ACTION_UP event Y coordinate
     * @return true if the key is pressed else false.
     */
    @Override
    boolean checkKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY) {
        if (mDigit.isEmpty()) return false;  //Empty key

        //Check if the click is between the width bounds
        if (downEventX > mBounds.exactCenterX() - mKeyRadius
                && downEventX < mBounds.exactCenterX() + mKeyRadius) {

            //Check if the click is between the height bounds
            if (downEventY > mBounds.exactCenterY() - mKeyRadius
                    && downEventY < mBounds.exactCenterY() + mKeyRadius) {

                //Check if the click is between the width bounds
                if (upEventX > mBounds.exactCenterX() - mKeyRadius
                        && upEventX < mBounds.exactCenterX() + mKeyRadius) {

                    //Check if the click is between the height bounds
                    if (upEventY > mBounds.exactCenterY() - mKeyRadius
                            && upEventY < mBounds.exactCenterY() + mKeyRadius) {

                        playClickAnimation();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
