package com.kevalpatel.passcodeview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Keval Patel on 09/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class BoxIndicator extends Box {
    private int mPintCodeLength;

    @ColorInt
    private int mIndicatorStrokeColor;              //Empty indicator stroke color
    @ColorInt
    private int mIndicatorFilledColor;              //Filled indicator stroke color
    @ColorInt
    private int mTitleColor;                        //Title text color
    private String mTitle;                          //Title color

    //Paints
    private Paint mEmptyIndicatorPaint;             //Empty indicator color
    private Paint mSolidIndicatorPaint;             //Solid indicator color
    private Paint mTitlePaint;             //Solid indicator color

    private String mPinTyped = "";
    private ArrayList<Indicator> mDotsIndicator;
    private Rect mDotsIndicatorBound;

    BoxIndicator(@NonNull View view) {
        super(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    void setDefaults() {
        mTitle = Constants.DEF_TITLE_TEXT;
        mTitleColor = getContext().getResources().getColor(R.color.key_default_color);

        mIndicatorFilledColor = getContext().getResources().getColor(R.color.indicator_filled_color);
        mIndicatorStrokeColor = getContext().getResources().getColor(R.color.indicator_stroke_color);
    }

    @Override
    void onAuthenticationFail() {
        //Do nothing
    }

    @Override
    void onAuthenticationSuccess() {
        //Do nothing
    }

    @Override
    void draw(@NonNull Canvas canvas) {
        canvas.drawText(mTitle,
                mDotsIndicatorBound.exactCenterX(),
                mDotsIndicatorBound.top - (int) getContext().getResources().getDimension(R.dimen.divider_vertical_margin),
                mTitlePaint);

        for (int i = 0; i < mPintCodeLength; i++) {
            mDotsIndicator.get(i).draw(getContext(), canvas,
                    i < mPinTyped.length() ? mSolidIndicatorPaint : mEmptyIndicatorPaint);
        }
    }

    @Override
    void measure(@NonNull Rect rootViewBounds) {

        int indicatorWidth = 2 * (int) (getContext().getResources().getDimension(R.dimen.indicator_radius)
                + getContext().getResources().getDimension(R.dimen.indicator_padding));
        int totalSpace = indicatorWidth * mPintCodeLength;

        //Dots indicator
        mDotsIndicatorBound = new Rect();
        mDotsIndicatorBound.left = (rootViewBounds.width() - totalSpace) / 2;
        mDotsIndicatorBound.right = mDotsIndicatorBound.left + totalSpace;
        mDotsIndicatorBound.bottom = rootViewBounds.top
                + (int) (rootViewBounds.height() * BoxKeypad.KEY_BOARD_TOP_WEIGHT
                - 2 * getContext().getResources().getDimension(R.dimen.divider_vertical_margin));
        mDotsIndicatorBound.top = mDotsIndicatorBound.bottom - indicatorWidth;

        mDotsIndicator = new ArrayList<>();
        for (int i = 0; i < mPintCodeLength; i++) {
            Rect rect = new Rect();
            rect.left = mDotsIndicatorBound.left + i * indicatorWidth;
            rect.right = rect.left + indicatorWidth;
            rect.top = mDotsIndicatorBound.top;
            rect.bottom = mDotsIndicatorBound.bottom;

            mDotsIndicator.add(new Indicator(rect));
        }
    }

    @Override
    void preparePaint() {
        //Set empty dot paint
        mEmptyIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mEmptyIndicatorPaint.setStyle(Paint.Style.STROKE);
        mEmptyIndicatorPaint.setColor(mIndicatorStrokeColor);
        mEmptyIndicatorPaint.setStrokeWidth(getContext().getResources().getDimension(R.dimen.indicator_stroke_width));

        //Set filled dot paint
        mSolidIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSolidIndicatorPaint.setColor(mIndicatorFilledColor);

        //Set title paint
        mTitlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTitlePaint.setColor(mTitleColor);
        mTitlePaint.setTextAlign(Paint.Align.CENTER);
        mTitlePaint.setTextSize(getContext().getResources().getDimension(R.dimen.title_text_size));
    }

    @Override
    void onValueEntered(@NonNull String valueDigits) {
        mPinTyped = valueDigits;
    }

    @ColorInt
    int getIndicatorStrokeColor() {
        return mIndicatorStrokeColor;
    }

    void setIndicatorStrokeColor(@ColorInt int indicatorStrokeColor) {
        this.mIndicatorStrokeColor = indicatorStrokeColor;
        preparePaint();
    }

    @ColorInt
    int getIndicatorFilledColor() {
        return mIndicatorFilledColor;
    }

    void setIndicatorFilledColor(@ColorInt int indicatorFilledColor) {
        this.mIndicatorFilledColor = indicatorFilledColor;
        preparePaint();
    }

    void setPintCodeLength(int pintCodeLength) {
        this.mPintCodeLength = pintCodeLength;
    }

    String getTitle() {
        return mTitle;
    }

    void setTitle(String title) {
        this.mTitle = title;
    }

    int getTitleColor() {
        return mTitleColor;
    }

    void setTitleColor(int titleColor) {
        this.mTitleColor = titleColor;
        preparePaint();
    }
}
