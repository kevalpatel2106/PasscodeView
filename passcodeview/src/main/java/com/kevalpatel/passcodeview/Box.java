package com.kevalpatel.passcodeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public abstract class Box {

    private final View mView;

    public Box(@NonNull View rootView) {
        mView = rootView;
    }

    @NonNull
    public View getRootView() {
        return mView;
    }

    @NonNull
    public Context getContext() {
        return mView.getContext();
    }

    abstract void setDefaults();

    abstract void onAuthenticationFail();

    abstract void onAuthenticationSuccess();

    abstract void draw(@NonNull Canvas canvas);

    abstract void measure(@NonNull Rect rootViewBounds);

    abstract void preparePaint();

    abstract void onValueEntered(@NonNull String valueDigits);
}
