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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class IndicatorCircle extends Indicator {

    IndicatorCircle(Rect rect) {
        super(rect);
    }

    /**
     * Draw the indicator.
     *
     * @param context Instance of the caller.
     * @param canvas  Canvas of {@link PinView}.
     * @param paint   Paint of the indicator.
     */
    @Override
    void draw(@NonNull Context context,
              @NonNull Canvas canvas,
              @NonNull Paint paint) {
        canvas.drawCircle(getBound().exactCenterX(),
                getBound().exactCenterY(),
                context.getResources().getDimension(R.dimen.indicator_radius),
                paint);
    }
}
