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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.kevalpatel.passcodeview.patternCells.PatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Oct-16.
 * This class contains utility methods for the library.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class Utils {

    /**
     * Open the Security settings screen. This settings screen will allow user to register finger
     * prints.
     *
     * @param context instance of the caller.
     */
    @SuppressWarnings("WeakerAccess")
    public static void openSecuritySettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * Check if the device have supported hardware fir the finger print scanner.
     *
     * @param context instance of the caller.
     * @return true if device have the hardware.
     */
    @SuppressWarnings("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @RequiresPermission(allOf = {Manifest.permission.USE_FINGERPRINT})
    public static boolean hasSupportedFingerprintHardware(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    /**
     * Check if the device has any fingerprint registered?
     * <p>
     * If no fingerprint registered, use {@link #openSecuritySettings(Context)} to open security settings.
     *
     * @param context instance
     * @return true if any fingerprint is register.
     */
    @SuppressWarnings({"MissingPermission", "WeakerAccess"})
    public static boolean isFingerPrintEnrolled(Context context) {
        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            return fingerprintManager != null
                    && fingerprintManager.isHardwareDetected()
                    && fingerprintManager.hasEnrolledFingerprints();
        } else {
            return false;
        }
    }

    /**
     * Check if the given pin is valid or not?
     *
     * @param pinToCheck pin to validate
     * @return true if the entered pin is valid.
     */
    static boolean isValidPin(int[] pinToCheck) {
        for (int i : pinToCheck) if (i > 9 && i < 0) return false;
        return true;
    }

    /**
     * Compare two arrays of the PIN and check if both pin matches?
     *
     * @param correctPin correct pin.
     * @param pinToCheck pin entered by the user.
     * @return true if the both pin matches.
     */
    static boolean isPINMatched(int[] correctPin, ArrayList<Integer> pinToCheck) {
        for (int i = 0; i < correctPin.length; i++)
            if (correctPin[i] != pinToCheck.get(i)) return false;
        return correctPin.length == pinToCheck.size();
    }

    /**
     * Check if the both pattern matches? It uses {@link PatternPoint#equals(Object)} to match row and
     * column number of both pattern poins.
     *
     * @param correctPattern correct list of {@link PatternPoint}.
     * @param patternToCheck list of {@link PatternPoint} to check.
     * @return true if both pattern matches.
     * @see PatternPoint#equals(Object)
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    static boolean isPatternMatched(@NonNull PatternPoint[] correctPattern,
                                    @NonNull ArrayList<PatternCell> patternToCheck) {
        for (int i = 0; i < correctPattern.length; i++)
            if (!correctPattern[i].equals(patternToCheck.get(i).getPoint())) return false;

        return correctPattern.length == patternToCheck.size();
    }

    /**
     * Get the darker version of the given color.
     *
     * @param color Normal color.
     * @return Darker shade of the color.
     * @see 'http://stackoverflow.com/a/4928826'
     */
    @ColorInt
    public static int makeColorDark(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 1f - 0.8f * hsv[2]; // value component
        return Color.HSVToColor(hsv);
    }

    /**
     * Run the vibrator to give tactile feedback for 50ms when any key is pressed.
     */
    static void giveTactileFeedbackForKeyPress(@NonNull Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(50);
    }

    /**
     * Run the vibrator to give tactile feedback for 350ms when user authentication is successful.
     */
    static void giveTactileFeedbackForAuthFail(@NonNull Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(350);
    }

    /**
     * Run the vibrator to give tactile feedback for 100ms at difference of 50ms for two times when
     * user authentication is failed.
     */
    static void giveTactileFeedbackForAuthSuccess(@NonNull Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null) {
            Log.w("PasscodeView", "Vibrator service not found.");
            return;
        }

        if (v.hasVibrator()) v.vibrate(new long[]{50, 100, 50, 100}, -1);
    }

    @ColorInt
    public static int getColorCompat(@NonNull Context context, @ColorRes int colorRes) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
                context.getResources().getColor(colorRes) : context.getColor(colorRes);
    }
}
