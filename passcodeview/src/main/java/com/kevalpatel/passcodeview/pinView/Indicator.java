package com.kevalpatel.passcodeview.pinView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.R;

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
     * @param context instance of the caller.
     * @param canvas  canvas of {@link pinView}.
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
