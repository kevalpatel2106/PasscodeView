/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
