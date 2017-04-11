package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval on 06-Apr-17.
 */

class IndicatorCircle extends Indicator{

    IndicatorCircle(Rect rect) {
        super(rect);
    }

    /**
     * Draw the indicator.
     *
     * @param context Instance of the caller.
     * @param canvas  Canvas of {@link PinView}.
     * @param paint   Paint of the indicator.
     */
    @Override
    void draw(@NonNull Context context,
              @NonNull Canvas canvas,
              @NonNull Paint paint) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                context.getResources().getDimension(R.dimen.indicator_radius),
                paint);
    }
}
