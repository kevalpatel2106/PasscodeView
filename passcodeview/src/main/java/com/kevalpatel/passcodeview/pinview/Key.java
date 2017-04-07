package com.kevalpatel.passcodeview.pinview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public abstract class Key {

    abstract void draw(@NonNull Canvas canvas,
                       @NonNull Paint keyPaint,
                       @NonNull Paint keyTextPaint);

    abstract String getDigit();

    abstract void playError();

    abstract boolean checkKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY);

    abstract void playClickAnimation();
}
