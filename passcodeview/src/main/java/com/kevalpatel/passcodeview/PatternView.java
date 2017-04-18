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
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.patternCells.PatternCell;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class PatternView extends PasscodeView implements InteractiveArrayList.ChangeListener {
    private int[] mCorrectPattern;                                      //Current PIN with witch entered PIN will check.
    private InteractiveArrayList<Integer> mPatternTyped;                //PIN typed.

    private BoxPattern mBoxPattern;
    private BoxTitle mBoxTitle;

    ///////////////////////////////////////////////////////////////
    //                  CONSTRUCTORS
    ///////////////////////////////////////////////////////////////

    public PatternView(Context context) {
        super(context);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    ///////////////////////////////////////////////////////////////
    //                  SET THEME PARAMS INITIALIZE
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize view.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void init() {
        //Initialized the typed pattern array
        mPatternTyped = new InteractiveArrayList<>();
        mPatternTyped.setChangeListener(this);

        //initialize boxes
        mBoxPattern = new BoxPattern(this);
        mBoxTitle = new BoxTitle(this);
    }

    @Override
    protected void setDefaultParams() {
        mBoxTitle.setDefaults();
        mBoxPattern.setDefaults();
    }

    @Override
    protected void preparePaint() {
        //Prepare paints.
        mBoxPattern.preparePaint();
        mBoxTitle.preparePaint();
    }

    /**
     * Parse the theme attribute using the parse array.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void parseTypeArr(@NonNull TypedArray typedArray) {
        //Parse title params
        mBoxTitle.setTitle(typedArray.hasValue(R.styleable.PinView_titleText) ?
                typedArray.getString(R.styleable.PinView_titleText) : BoxTitleIndicator.DEF_TITLE_TEXT);
        mBoxTitle.setTitleColor(typedArray.getColor(R.styleable.PinView_titleTextColor,
                mContext.getResources().getColor(R.color.lib_key_default_color)));
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
        mBoxPattern.draw(canvas);
        mBoxTitle.draw(canvas);
        mBoxFingerprint.draw(canvas);
    }

    ///////////////////////////////////////////////////////////////
    //                  VIEW MEASUREMENT
    ///////////////////////////////////////////////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBoxPattern.measure(mRootViewBound);
        mBoxTitle.measure(mRootViewBound);
        mBoxFingerprint.measure(mRootViewBound);
    }

    ///////////////////////////////////////////////////////////////
    //                  TOUCH HANDLER
    ///////////////////////////////////////////////////////////////


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_HOVER_ENTER:

                break;
            default:
                return false;
        }
        return true;
    }

    /**
     * Reset the pin code and view state.
     */
    @Override
    public void reset() {
        mPatternTyped.clear();
        invalidate();
    }

    /**
     * This method will be called when there is any change in {@link #mPatternTyped}.
     *
     * @param size this is the new size of {@link #mPatternTyped}.
     * @see InteractiveArrayList
     */
    @Override
    public void onArrayValueChange(int size) {
        //Do nothing
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////

    public boolean isOneHandOperationEnabled() {
        return mBoxPattern.isOneHandOperation();
    }

    public void enableOneHandOperation(boolean isEnable) {
        mBoxPattern.setOneHandOperation(isEnable);
        requestLayout();
        invalidate();
    }

    public void setCorrectPattern(@NonNull int[] correctPattern) {
        //Validate the pin
        if (!Utils.isValidPin(correctPattern)) throw new IllegalArgumentException("Invalid PIN.");

        mCorrectPattern = correctPattern;

        mPatternTyped.clear();
        invalidate();
    }

    public int getTitleColor() {
        return mBoxTitle.getTitleColor();
    }

    public void setTitleColor(@ColorInt int titleColor) {
        mBoxTitle.setTitleColor(titleColor);
        invalidate();
    }

    /**
     * @return Current title of the view.
     */
    public String getTitle() {
        return mBoxTitle.getTitle();
    }

    /**
     * Set the title at the top of view.
     *
     * @param title title string
     */
    public void setTitle(@NonNull String title) {
        mBoxTitle.setTitle(title);
        invalidate();
    }

    public void setIndicator(@NonNull PatternCell.Builder indicatorBuilder) {
        mBoxPattern.setCellBuilder(indicatorBuilder);
        requestLayout();
        invalidate();
    }

    @Nullable
    public PatternCell.Builder getIndicatorBuilder() {
        return mBoxPattern.getCellBuilder();
    }
}
