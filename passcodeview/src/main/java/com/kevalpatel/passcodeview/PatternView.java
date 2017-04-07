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

public class PatternView extends View
{
    public PatternView(Context context) {
        super(context);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
