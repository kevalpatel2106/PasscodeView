package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval on 06-Apr-17.
 */

class Indicator {

    private Rect mBound;

    Indicator(Rect rect) {
        mBound = rect;
    }

    Rect getBound() {
        return mBound;
    }

    void setBound(Rect bound) {
        mBound = bound;
    }

    /**
     * Draw the indicator.
     *
     * @param context Instance of the caller.
     * @param canvas  Canvas of {@link PinView}.
     * @param paint   Paint of the indicator.
     */
    void draw(@NonNull Context context,
              @NonNull Canvas canvas,
              @NonNull Paint paint) {
        canvas.drawCircle(mBound.exactCenterX(),
                mBound.exactCenterY(),
                context.getResources().getDimension(R.dimen.indicator_radius),
                paint);
    }
}
