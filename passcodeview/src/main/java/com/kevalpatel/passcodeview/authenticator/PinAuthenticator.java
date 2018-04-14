/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.authenticator;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Keval on 14/04/18.
 *
 * @author [kevalpatel2106](https : / / github.com / kevalpatel2106)
 */
public interface PinAuthenticator {

    boolean isValidPin(@NonNull final ArrayList<Integer> pinDigits);

    boolean isValidPinLength(final int typedLength);
}
