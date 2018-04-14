/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.internal;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

/**
 * Created by Kevalpatel2106 on 05-Feb-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public interface PasscodeViewLifeCycle {

    void init();

    void setDefaults();

    void preparePaint();

    void parseTypeArr(@NonNull AttributeSet typedArray);

    void drawView(@NonNull Canvas canvas);

    void measureView(@NonNull Rect rootViewBounds);

    void onAuthenticationFail();

    void onAuthenticationSuccess();

    void reset();
}
