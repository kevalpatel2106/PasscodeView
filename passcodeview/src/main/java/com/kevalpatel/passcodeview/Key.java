/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 07/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
abstract class Key {

    abstract void draw(@NonNull Canvas canvas,
                       @NonNull Paint keyPaint,
                       @NonNull Paint keyTextPaint);

    abstract String getDigit();

    abstract void playErrorAnimation();

    abstract boolean checkKeyPressed(float downEventX, float downEventY, float upEventX, float upEventY);

    abstract void playClickAnimation();
}
