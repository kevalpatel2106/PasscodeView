package com.kevalpatel.passcodeview.fingerprint;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by Keval on 07-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class FingerPrintBox implements FingerPrintAuthCallback {

    private Context mContext;
    private Rect mBounds = new Rect();

    public FingerPrintBox(Context context) {
        mContext = context;
        FingerPrintAuthHelper.getHelper(mContext, this);
    }


    @Override
    public void onNoFingerPrintHardwareFound() {

    }

    @Override
    public void onNoFingerPrintRegistered() {

    }

    @Override
    public void onBelowMarshmallow() {

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {

    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {

    }
}
