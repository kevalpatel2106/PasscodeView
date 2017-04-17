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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;

/**
 * Created by Keval on 17-Apr-17.
 */

public class PatternView extends View {
    private Context mContext;

    private AuthenticationListener mAuthenticationListener;         //Callback listener for application to get notify when authentication successful.
    private int[] mCorrectPin;                                      //Current PIN with witch entered PIN will check.
    private InteractiveArrayList<Integer> mPinTyped = new InteractiveArrayList<>();       //PIN typed.

    //Rectangle bounds
    private Rect mRootViewBound = new Rect();       //Root bound
    private Rect mDividerBound = new Rect();        //Divider bound

    //Theme attributes
    @ColorInt
    private int mDividerColor;                              //Horizontal divider color
    private boolean mIsTactileFeedbackREnabled = true;      //Bool to indicate weather to enable tactile feedback

    //Paints
    private Paint mDividerPaint;                    //Horizontal divider paint color

    //UI boxes
    private BoxFingerprint mBoxFingerprint;

    public PatternView(Context context) {
        super(context);
        init(context, null);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize project
     *
     * @param context      instance of the caller.
     * @param attributeSet
     */
    public void init(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        mContext = context;
        mBoxFingerprint = new BoxFingerprint(this);

        if (attributeSet != null) {    //Parse all the params from the arguments.
            parseTypeArr(attributeSet);
        } else {        //Nothing's provided in XML. Set default for now.
            mDividerColor = getResources().getColor(R.color.lib_divider_color);

            mBoxFingerprint.setDefaults();
        }

        //Prepare paints.
        prepareDividerPaint();
        mBoxFingerprint.preparePaint();
    }

    /**
     * Parse the theme attribute using the parse array.
     *
     * @param attrs theme AttributeSet.
     */
    @SuppressWarnings("deprecation")
    private void parseTypeArr(@Nullable AttributeSet attrs) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.PinView, 0, 0);
        try {
            mIsTactileFeedbackREnabled = a.getBoolean(R.styleable.PinView_giveTactileFeedback, true);

            //Fet fingerprint params
            //noinspection ConstantConditions
            mBoxFingerprint.setStatusText(a.hasValue(R.styleable.PinView_titleText) ?
                    a.getString(R.styleable.PinView_fingerprintDefaultText) : BoxFingerprint.DEF_FINGERPRINT_STATUS);
            mBoxFingerprint.setStatusTextColor(a.getColor(R.styleable.PinView_fingerprintTextColor,
                    mContext.getResources().getColor(R.color.lib_key_default_color)));
            mBoxFingerprint.setStatusTextSize(a.getDimension(R.styleable.PinView_fingerprintTextSize,
                    (int) mContext.getResources().getDimension(R.dimen.lib_fingerprint_status_text_size)));
            mBoxFingerprint.setFingerPrintEnable(a.getBoolean(R.styleable.PinView_fingerprintEnable, true));
        } finally {
            a.recycle();
        }
    }

    /**
     * Create the paint to drawText divider.
     */
    private void prepareDividerPaint() {
        mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDividerPaint.setColor(mDividerColor);
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureMainView();
        mBoxFingerprint.measure(mRootViewBound);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Measure the root view and get bounds.
     */
    private Rect measureMainView() {
        getLocalVisibleRect(mRootViewBound);

        //Get the height of the actionbar if we have any actionbar and add it to the top
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mRootViewBound.top = mRootViewBound.top
                    + TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        return mRootViewBound;
    }


    ///////////////////////////////////////////////////////////////
    //                  VIEW DRAW
    ///////////////////////////////////////////////////////////////

    /**
     * Draw method of the view called every time frame refreshes.
     *
     * @param canvas view canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBoxFingerprint.draw(canvas);
    }
}
