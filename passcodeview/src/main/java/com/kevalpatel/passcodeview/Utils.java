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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

import com.kevalpatel.passcodeview.patternCells.PatternCell;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Oct-16.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class Utils {

    private Utils() {
    }

    /**
     * Open the Security settings screen.
     *
     * @param context instance of the caller.
     */
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
    public static boolean isSupportedHardware(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false;
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        return fingerprintManager.isHardwareDetected();
    }

    @SuppressWarnings("MissingPermission")
    public static boolean isFingerPrintEnrolled(Context context) {
        // Check if we're running on Android 6.0 (M) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            return !(!fingerprintManager.isHardwareDetected() || !fingerprintManager.hasEnrolledFingerprints());
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

    static boolean isPatternMatched(int[] correctPin, ArrayList<PatternCell> pinToCheck) {
        for (int i = 0; i < correctPin.length; i++)
            if (correctPin[i] != pinToCheck.get(i).getIndex()) return false;
        return correctPin.length == pinToCheck.size();
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
        if (v.hasVibrator()) v.vibrate(50);
    }

    /**
     * Run the vibrator to give tactile feedback for 350ms when yser authentication is successful.
     */
    static void giveTactileFeedbackForAuthFail(@NonNull Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator()) v.vibrate(350);
    }

    /**
     * Run the vibrator to give tactile feedback for 100ms at difference of 50ms for two times when
     * user authentication is failed.
     */
    static void giveTactileFeedbackForAuthSuccess(@NonNull Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator()) v.vibrate(new long[]{50, 100, 50, 100}, -1);
    }
}
