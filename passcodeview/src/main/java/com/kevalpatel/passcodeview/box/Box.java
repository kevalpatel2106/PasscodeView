/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.box;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevalpatel.passcodeview.BasePasscodeView;
import com.kevalpatel.passcodeview.PasscodeViewLifeCycle;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
abstract class Box implements PasscodeViewLifeCycle {

    /**
     * Reference to the root {@link BasePasscodeView}.
     */
    private final BasePasscodeView mView;

    /**
     * Public constructor.
     *
     * @param rootView {@link BasePasscodeView} that contains this box.
     */
    Box(@NonNull BasePasscodeView rootView) {
        mView = rootView;
    }

    /**
     * @return The root {@link BasePasscodeView} that contains this box.
     */
    @NonNull
    public final BasePasscodeView getRootView() {
        return mView;
    }

    @NonNull
    public final Context getContext() {
        return mView.getContext();
    }

}
