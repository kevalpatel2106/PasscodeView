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

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Keval Patel on 14/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

final class InteractiveArrayList<E> extends ArrayList<E> {
    private ChangeListener mChangeListener;

    void setChangeListener(@NonNull ChangeListener listener) {
        mChangeListener = listener;
    }

    @Override
    public boolean add(E e) {
        boolean b = super.add(e);
        mChangeListener.onArrayValueChange(size());
        return b;
    }

    @Override
    public boolean remove(Object o) {
        boolean b = super.remove(o);
        mChangeListener.onArrayValueChange(size());
        return b;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        mChangeListener.onArrayValueChange(size());
    }

    @Override
    public E remove(int index) {
        mChangeListener.onArrayValueChange(size() - 1);
        return super.remove(index);
    }

    @Override
    public void clear() {
        super.clear();
        mChangeListener.onArrayValueChange(size());
    }

    public interface ChangeListener {
        void onArrayValueChange(int size);
    }
}
