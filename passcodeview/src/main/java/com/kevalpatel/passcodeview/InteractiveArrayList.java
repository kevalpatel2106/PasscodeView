/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
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

    public void setChangeListener(@NonNull ChangeListener listener) {
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
