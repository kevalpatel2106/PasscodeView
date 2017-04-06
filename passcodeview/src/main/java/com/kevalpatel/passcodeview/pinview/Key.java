package com.kevalpatel.passcodeview.pinview;

import android.graphics.Rect;

/**
 * Created by Keval on 06-Apr-17.
 */

class Key {

    private final String mDigit;
    private final Rect mBounds;

    Key(String digit, Rect bounds){
        mDigit = digit;
        mBounds = bounds;
    }

    String getDigit() {
        return mDigit;
    }

    Rect getBounds() {
        return mBounds;
    }
}
