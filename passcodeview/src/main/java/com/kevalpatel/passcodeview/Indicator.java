package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
abstract class Indicator {

    private Rect mBound;

    Indicator(Rect bound){
        mBound = bound;
    }


    abstract  void draw(@NonNull Context context,
                        @NonNull Canvas canvas,
                        @NonNull Paint paint);

    Rect getBound() {
        return mBound;
    }
}
