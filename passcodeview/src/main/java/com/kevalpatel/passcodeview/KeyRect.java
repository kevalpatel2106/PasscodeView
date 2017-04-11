package com.kevalpatel.passcodeview;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.CycleInterpolator;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class KeyRect extends Key {
    private final String mDigit;                        //KeyCircle title.
    private final Rect mBounds;                         //KeyCircle bound.
    private final View mView;                           //Pin view
    private final float mKeyPadding;

    /**
     * Public constructor.
     *
     * @param view   {@link PinView}
     * @param digit  title of the key. (-1 for the backspace key)
     * @param bounds {@link Rect} bound.
     */
    KeyRect(View view, String digit, Rect bounds, float keyPadding) {
        mDigit = digit;
        mBounds = bounds;
        mView = view;
        mKeyPadding = keyPadding;
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
     * Start Playing ripple animation and notify listener accordingly
     * <p>
     * notified
     */
    @Override
    void playClickAnimation() {
        //TODO Click animation
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
     * Don't change until you know what you are doing. :-)
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
        canvas.drawRect(mBounds.left + mKeyPadding,
                mBounds.top + mKeyPadding,
                mBounds.right - mKeyPadding,
                mBounds.bottom - mKeyPadding,
                keyPaint);

        if (getDigit().equals(Constants.BACKSPACE_TITLE)) {  //Backspace key
            Drawable d = mView.getContext().getResources().getDrawable(R.drawable.ic_back_space);
            d.setBounds((int) (mBounds.exactCenterX() - Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() - Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterX() + Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() + Math.min(mBounds.height(), mBounds.width()) / 3));
            d.setColorFilter(new PorterDuffColorFilter(keyTextPaint.getColor(), PorterDuff.Mode.SRC_ATOP));
            d.draw(canvas);
        } else {
            //Draw key text
            canvas.drawText(getDigit() + "",                //Text to display on key
                    mBounds.exactCenterX(),             //Set start point at center width of key
                    mBounds.exactCenterY() - (keyTextPaint.descent() + keyPaint.ascent()) / 2,    //center height of key - text height/2
                    keyTextPaint);
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
        if (downEventX > mBounds.left && downEventX < mBounds.right) {

            //Check if the click is between the height bounds
            if (downEventY > mBounds.top && downEventY < mBounds.bottom) {

                //Check if the click is between the width bounds
                if (upEventX > mBounds.left && upEventX < mBounds.right) {

                    //Check if the click is between the height bounds
                    if (upEventY > mBounds.top && upEventY < mBounds.bottom) {

                        playClickAnimation();
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
