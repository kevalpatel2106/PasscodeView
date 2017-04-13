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
import android.graphics.Rect;
import android.support.annotation.NonNull;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

abstract class Box {

    private final PinView mView;

    public Box(@NonNull PinView rootView) {
        mView = rootView;
    }

    @NonNull
    public final PinView getRootView() {
        return mView;
    }

    @NonNull
    public final Context getContext() {
        return mView.getContext();
    }

    abstract void setDefaults();

    abstract void onAuthenticationFail();

    abstract void onAuthenticationSuccess();

    abstract void draw(@NonNull Canvas canvas);

    abstract void measure(@NonNull Rect rootViewBounds);

    abstract void preparePaint();
}
