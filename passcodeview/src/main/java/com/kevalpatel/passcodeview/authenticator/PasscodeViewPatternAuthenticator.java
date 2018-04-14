/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.authenticator;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 14/04/18.
 *
 * @author [kevalpatel2106](https : / / github.com / kevalpatel2106)
 */
public final class PasscodeViewPatternAuthenticator implements PatternAuthenticator {

    @NonNull
    private final PatternPoint[] mCorrectPattern;

    public PasscodeViewPatternAuthenticator(@NonNull final PatternPoint[] correctPattern) {
        mCorrectPattern = correctPattern;
    }

    @WorkerThread
    @Override
    public boolean isValidPattern(@NonNull final ArrayList<PatternPoint> patternPoints) {
        //This calculations won't take much time.
        //We are not blocking the UI.
        for (int i = 0; i < mCorrectPattern.length; i++)
            if (!mCorrectPattern[i].equals(patternPoints.get(i))) return false;

        return mCorrectPattern.length == patternPoints.size();
    }
}
