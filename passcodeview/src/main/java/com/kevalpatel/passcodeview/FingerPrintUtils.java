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
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;

/**
 * Created by Keval on 07-Oct-16.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class FingerPrintUtils {

    private FingerPrintUtils() {
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
}
