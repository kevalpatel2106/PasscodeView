package com.kevalpatel.passcodeview.pinview;

import android.graphics.Rect;

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
}
