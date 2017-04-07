package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

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

    void draw(Context context, Canvas canvas,Paint paint ) {
        canvas.drawCircle(mBound.exactCenterX(),
                mBound.exactCenterY(),
                context.getResources().getDimension(R.dimen.indicator_radius),
                paint);
    }
}
