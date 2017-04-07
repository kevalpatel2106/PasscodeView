package com.kevalpatel.passcodeview.pinview;

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

import com.kevalpatel.passcodeview.R;

/**
 * Created by Keval on 06-Apr-17.
 * This class represents single key.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class RectKey extends Key {
    private final String mDigit;                        //CircleKey title.
    private final Rect mBounds;                         //CircleKey bound.
    private final View mView;                           //Pin view
     /**
     * Public constructor.
     *
     * @param view   {@link PinView}
     * @param digit  title of the key. (-1 for the backspace key)
     * @param bounds {@link Rect} bound.
     */
    RectKey(View view, String digit, Rect bounds) {
        mDigit = digit;
        mBounds = bounds;
        mView = view;
    }

    /**
     * Get the digit string.
     *
     * @return CircleKey name
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
     *
     * @param canvas       canvas of the view o which key will be drawn
     * @param keyPaint     CircleKey background paint
     * @param keyTextPaint CircleKey text paint
     */
    @Override
    void draw(@NonNull Canvas canvas,
              @NonNull Paint keyPaint,
              @NonNull Paint keyTextPaint) {

        //Draw circle background
        canvas.drawRect(mBounds, keyPaint);

        if (getDigit().equals(Defaults.BACKSPACE_TITLE)) {  //Backspace key
            Drawable d = ContextCompat.getDrawable(mView.getContext(), R.drawable.ic_back_space);
            d.setBounds((int) (mBounds.exactCenterX() - Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() + Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterX() + Math.min(mBounds.height(), mBounds.width()) / 3),
                    (int) (mBounds.exactCenterY() - Math.min(mBounds.height(), mBounds.width()) / 3));
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
