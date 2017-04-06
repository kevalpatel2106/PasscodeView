package com.kevalpatel.passcodeview.pinview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class Key {
    private static final int MAX_RIPPLE_ALPHA = 180;
    private static final int RIPPLE_DURATION = 350;

    private final String mDigit;
    private final Rect mBounds;
    private final View mView;
    private final float mKeyRadius;

    private ValueAnimator mRippleValueAnimator;
    private boolean isRippleEffectRunning = false;
    private int mCurrentRippleRadius = 0;
    private int mCurrentAlpha;

    /**
     * Public constructor.
     *
     * @param view   {@link PinView}
     * @param digit  title of the key. (-1 for the backspace key)
     * @param bounds {@link Rect} bound.
     */
    Key(View view, String digit, Rect bounds, float keyPadding) {
        mDigit = digit;
        mBounds = bounds;
        mView = view;
        mKeyRadius = calculateKeyRadius(bounds, keyPadding);

        setUpAnimator();
    }

    /**
     * Get the digit string.
     *
     * @return Key name
     */
    String getDigit() {
        return mDigit;
    }

    /**
     * Get bound.
     *
     * @return rectangle bound.
     */
    Rect getBounds() {
        return mBounds;
    }

    /**
     * Boolean to check the status of the ripple effect.
     *
     * @return true if the ripple effect is currently running.
     */
    boolean isRippleEffectRunning() {
        return isRippleEffectRunning;
    }

    /**
     * Get current ripple radius.
     *
     * @return current ripple radius or 0 if {@link #isRippleEffectRunning} is false.
     */
    int getCurrentRippleRadius() {
        return mCurrentRippleRadius;
    }

    /**
     * Get current ripple alpha.
     *
     * @return current ripple alpha or 0 if {@link #isRippleEffectRunning} is false.
     */
    int getCurrentAlpha() {
        return mCurrentAlpha;
    }

    /**
     * Get the full radius of the key circle.
     *
     * @return radius.
     */
    float getKeyRadius() {
        return mKeyRadius;
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
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    void playRippleAnim() {
        mRippleValueAnimator.start();
    }

    private float calculateKeyRadius(Rect bounds, float padding) {
        return Math.min(bounds.height(), bounds.width()) / 2 - padding;       //radius = height or width - padding for single key

    }
}
