package com.kevalpatel.passcodeview;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public abstract class Box {

    abstract void setDefaults();

    abstract void onAuthenticationFail();

    abstract void onAuthenticationSuccess();

    abstract void draw(@NonNull Canvas canvas);

    abstract void measure(@NonNull Rect rootViewBounds);

    abstract void preparePaint();
}
