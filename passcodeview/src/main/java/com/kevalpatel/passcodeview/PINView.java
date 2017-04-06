package com.kevalpatel.passcodeview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Keval on 06-Apr-17.
 */

public class PINView extends View {
    public PINView(Context context) {
        super(context);
    }

    public PINView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PINView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PINView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
