/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

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

    @ColorInt
    public static int getColorCompat(@NonNull Context context, @ColorRes int colorRes) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ?
                context.getResources().getColor(colorRes) : context.getColor(colorRes);
    }
}
