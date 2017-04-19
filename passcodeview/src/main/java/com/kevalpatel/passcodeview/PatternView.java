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
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.patternCells.PatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class PatternView extends PasscodeView {
    private PatternPoint[] mCorrectPattern;                                      //Current PIN with witch entered PIN will check.
    private ArrayList<PatternCell> mPatternTyped;            //PIN typed.

    private float mPathEndX;
    private float mPathEndY;

    @ColorInt
    private int mPathColor;

    private BoxPattern mBoxPattern;
    private BoxTitle mBoxTitle;

    private Paint mNormalPaint;
    private Paint mErrorPaint;

    private boolean isErrorShowing = false;
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
        mPatternTyped = new ArrayList<>();

        //initialize boxes
        mBoxPattern = new BoxPattern(this);
        mBoxTitle = new BoxTitle(this);
    }

    @Override
    protected void setDefaultParams() {
        mBoxTitle.setDefaults();
        mBoxPattern.setDefaults();

        mPathColor = mContext.getResources().getColor(android.R.color.holo_green_dark);
    }

    @Override
    protected void preparePaint() {
        mNormalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalPaint.setStrokeWidth(10);
        mNormalPaint.setColor(mPathColor);

        mErrorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorPaint.setStrokeWidth(10);
        mErrorPaint.setColor(Color.RED);

        //Prepare paints.
        mBoxPattern.preparePaint();
        mBoxTitle.preparePaint();
    }

    /**
     * Parse the theme attribute using the parse array.
     *
     * @param typedArray
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void parseTypeArr(@NonNull AttributeSet typedArray) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(typedArray, R.styleable.PatternView, 0, 0);

        try { //Parse title params
            mBoxTitle.setTitle(a.hasValue(R.styleable.PatternView_titleText) ?
                    a.getString(R.styleable.PatternView_titleText) : BoxTitleIndicator.DEF_TITLE_TEXT);
            mBoxTitle.setTitleColor(a.getColor(R.styleable.PatternView_titleTextColor,
                    mContext.getResources().getColor(R.color.lib_key_default_color)));

            mBoxPattern.setNoOfRows(a.getInt(R.styleable.PatternView_noOfRows, Constants.DEF_PATTERN_LENGTH));
            mBoxPattern.setNoOfColumn(a.getInt(R.styleable.PatternView_noOfColumns, Constants.DEF_PATTERN_LENGTH));

            mPathColor = a.getColor(R.styleable.PatternView_patternLineColor,
                    mContext.getResources().getColor(android.R.color.holo_green_dark));
        } finally {
            a.recycle();
        }
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

        drawPaths(canvas);
    }

    private void drawPaths(Canvas canvas) {
        if (mPatternTyped.size() == 0) return;

        int lastElementPos = mPatternTyped.size() - 1;
        for (int i = 0; i < lastElementPos; i++) {
            PatternCell startCell = mPatternTyped.get(i);
            PatternCell endCell = mPatternTyped.get(i + 1);
            canvas.drawLine(startCell.getCenterX(), startCell.getCenterY(),
                    endCell.getCenterX(), endCell.getCenterY(),
                    isErrorShowing ? mErrorPaint : mNormalPaint);
        }

        canvas.drawLine(mPatternTyped.get(lastElementPos).getCenterX(),
                mPatternTyped.get(lastElementPos).getCenterY(),
                mPathEndX, mPathEndY,
                isErrorShowing ? mErrorPaint : mNormalPaint);
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
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                reset();
            case MotionEvent.ACTION_MOVE:
                PatternCell cellNumber = mBoxPattern.findCell(touchX, touchY);
                if (cellNumber != null && !mPatternTyped.contains(cellNumber)) {
                    mPatternTyped.add(cellNumber);
                    if (isTactileFeedbackEnable()) Utils.giveTactileFeedbackForKeyPress(mContext);
                }

                mPathEndX = touchX;
                mPathEndY = touchY;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mPatternTyped.size() == 0) return true;

                validatePattern();

                //Reset the view.
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reset();
                    }
                }, 350);
                break;
            default:
                return false;
        }
        return true;
    }

    private void validatePattern() {
        if (mCorrectPattern.length == mPatternTyped.size() && Utils.isPatternMatched(mCorrectPattern, mPatternTyped)) {
            mBoxPattern.onAuthenticationSuccess();
            mBoxTitle.onAuthenticationSuccess();
            isErrorShowing = false;

            mAuthenticationListener.onAuthenticationSuccessful();
            if (isTactileFeedbackEnable()) Utils.giveTactileFeedbackForAuthSuccess(mContext);
        } else {
            mBoxPattern.onAuthenticationFail();
            mBoxTitle.onAuthenticationFail();
            isErrorShowing = true;

            mAuthenticationListener.onAuthenticationFailed();
            if (isTactileFeedbackEnable()) Utils.giveTactileFeedbackForAuthFail(mContext);
        }
        invalidate();
    }

    /**
     * Reset the pin code and view state.
     */
    @Override
    public void reset() {
        isErrorShowing = false;
        mPatternTyped.clear();
        invalidate();
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

    public void setCorrectPattern(@NonNull PatternPoint[] correctPattern) {
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

    @SuppressWarnings("deprecation")
    public void setTitleColorResource(@ColorRes int titleColor) {
        mBoxTitle.setTitleColor(mContext.getResources().getColor(titleColor));
        invalidate();
    }


    public int getPatternPathColor() {
        return mPathColor;
    }

    public void setPatternPathColor(@ColorInt int pathColor) {
        mPathColor = pathColor;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setPatternPathColorResource(@ColorRes int pathColor) {
        mPathColor = mContext.getResources().getColor(pathColor);
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

    public void setPatternCell(@NonNull PatternCell.Builder indicatorBuilder) {
        mBoxPattern.setCellBuilder(indicatorBuilder);
        requestLayout();
        invalidate();
    }

    @Nullable
    public PatternCell.Builder getPatternCellBuilder() {
        return mBoxPattern.getCellBuilder();
    }

    public int getNoOfColumn() {
        return mBoxPattern.getNoOfColumn();
    }

    public void setNoOfColumn(int noOfColumn) {
        mBoxPattern.setNoOfColumn(noOfColumn);
        requestLayout();
        invalidate();
    }

    public int getNoOfRows() {
        return mBoxPattern.getNoOfRows();
    }

    public void setNoOfRows(int noOfRows) {
        mBoxPattern.setNoOfRows(noOfRows);
        requestLayout();
        invalidate();
    }
}
