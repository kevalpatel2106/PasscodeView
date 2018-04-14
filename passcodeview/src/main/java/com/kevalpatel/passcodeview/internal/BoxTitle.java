/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview.internal;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.kevalpatel.passcodeview.Constants;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.Utils;

/**
 * Created by Keval Patel on 09/04/17.
 * This {@link Box} is to display title and passcode indicator. The number of the passcode indicator
 * will depend on the length of the passcode indicator.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class BoxTitle extends Box {
    private static final String DEF_TITLE_TEXT = "Enter pattern";

    /**
     * Color of the indicator title.
     */
    @ColorInt
    private int mTitleColor;

    /**
     * Title text of the indicator.
     */
    @NonNull
    private String mTitle = DEF_TITLE_TEXT;

    /**
     * {@link android.text.TextPaint} of the title with the {@link #mTitleColor} as the text color.
     */
    private Paint mTitlePaint;

    /**
     * {@link Rect} containing the bound of this box.
     */
    private Rect mBounds;

    /**
     * Public constructor.
     *
     * @param view {@link BasePasscodeView}.
     */
    public BoxTitle(@NonNull final BasePasscodeView view) {
        super(view);
    }

    @Override
    public void init() {
        //Do nothing
    }

    @Override
    public void setDefaults() {
        mTitle = DEF_TITLE_TEXT;
        mTitleColor = Utils.getColorCompat(getContext(), R.color.lib_key_default_color);
    }

    @Override
    public void onAuthenticationFail() {
        //TODO handle failure
    }

    @Override
    public void onAuthenticationSuccess() {
        //TODO handle success
    }

    @Override
    public void reset() {
        //TODO Reset the view.
    }

    @Override
    public void drawView(@NonNull final Canvas canvas) {
        //Write title text
        canvas.drawText(mTitle,
                mBounds.exactCenterX(),
                mBounds.top - (int) getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin),
                mTitlePaint);
    }

    /**
     * |------------------------|=|
     * |                        | |
     * |                        | |
     * |         TITLE          | | => 2 * {@link R.dimen#lib_divider_vertical_margin} px up from the bottom of the indicators.
     * |------------------------|=| => {@link Constants#KEY_BOARD_TOP_WEIGHT} of the total height.
     * |                        | |
     * |                        | |
     * |                        | | => Keypad height. ({@link BoxKeypad#measureView(Rect)})
     * |                        | |
     * |                        | |
     * |------------------------|=| => {@link Constants#KEY_BOARD_BOTTOM_WEIGHT} of the total weight if the fingerprint is available. Else it touches to the bottom of the main view.
     * |                        | | => Section for fingerprint. If the fingerprint is enabled. Otherwise keyboard streaches to the bottom of the root view.
     * |------------------------|=|
     * Don't change until you know what you are doing. :-)
     *
     * @param rootViewBounds {@link Rect} bounds of the main view.
     */
    @Override
    public void measureView(@NonNull final Rect rootViewBounds) {
        //Title box bounds
        mBounds = new Rect();
        mBounds.left = rootViewBounds.left;
        mBounds.right = rootViewBounds.right;
        mBounds.bottom = rootViewBounds.top
                + (int) (rootViewBounds.height() * Constants.KEY_BOARD_TOP_WEIGHT
                - 2 * getContext().getResources().getDimension(R.dimen.lib_divider_vertical_margin));
        mBounds.top = (int) (mBounds.bottom - mTitlePaint.getTextSize());
    }

    @Override
    public void preparePaint() {
        //Set title paint
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(getContext().getResources().getDimension(R.dimen.lib_title_text_size));
    }

    @Override
    public void parseTypeArr(@NonNull final AttributeSet typedArray) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(typedArray,
                R.styleable.PatternView, 0, 0);

        try { //Parse title params
            //noinspection ConstantConditions
            mTitle = a.hasValue(R.styleable.PatternView_titleText) ?
                    a.getString(R.styleable.PatternView_titleText) : DEF_TITLE_TEXT;
            mTitleColor = a.getColor(R.styleable.PatternView_titleTextColor,
                    getContext().getResources().getColor(R.color.lib_key_default_color));
        } finally {
            a.recycle();
        }
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull final String title) {
        this.mTitle = title;
    }

    @ColorInt
    public int getTitleColor() {
        return mTitleColor;
    }

    public void setTitleColor(@ColorInt final int titleColor) {
        this.mTitleColor = titleColor;
        preparePaint();
    }
}
