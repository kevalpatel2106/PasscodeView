/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.kevalpatel.passcodeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.box.BoxPattern;
import com.kevalpatel.passcodeview.box.BoxTitle;
import com.kevalpatel.passcodeview.patternCells.PatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class PatternView extends BasePasscodeView {
    private PatternPoint[] mCorrectPattern;                  //Current PIN with witch entered PIN will check.
    private ArrayList<PatternCell> mPatternTyped;            //PIN typed.

    private float mPatternPathEndX;
    private float mPatternPathEndY;

    /**
     * Color of the path of the pattern.
     */
    @ColorInt
    private int mPatternPathColor;

    /**
     * {@link BoxPattern} to display the {@link PatternPoint}. User can enter the correct pattern
     * in this box.
     */
    private BoxPattern mBoxPattern;

    /**
     * {@link BoxTitle} to display the title message.
     */
    private BoxTitle mBoxTitle;

    /**
     * {@link Paint} for the path between two {@link PatternPoint} while user is entering the pattern.
     *
     * @see PatternPoint
     */
    private Paint mNormalPathPaint;

    /**
     * {@link Paint} for the path between two {@link PatternPoint} when entered pattern is wrong.
     *
     * @see PatternPoint
     */
    private Paint mErrorPathPaint;

    /**
     * Boolean to set <code>true</code> if the error animations are currently being played or <code>false</code>.
     */
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
    //                  LIFE CYCLE CALLBACKS
    ///////////////////////////////////////////////////////////////

    /**
     * Initialize view.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void init() {
        //Initialized the typed pattern array
        mPatternTyped = new ArrayList<>();

        //initialize boxes
        mBoxPattern = new BoxPattern(this);
        mBoxTitle = new BoxTitle(this);

        mBoxPattern.init();
        mBoxTitle.init();
    }

    /**
     * Set the default values.
     */
    @Override
    public void setDefaults() {
        mBoxTitle.setDefaults();
        mBoxPattern.setDefaults();

        mPatternPathColor = mContext.getResources().getColor(android.R.color.holo_green_dark);
    }

    /**
     * Prepare the {@link #mNormalPathPaint} and {@link #mErrorPathPaint}.
     */
    @Override
    public void preparePaint() {
        mNormalPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mNormalPathPaint.setStrokeWidth(10);
        mNormalPathPaint.setColor(mPatternPathColor);

        mErrorPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mErrorPathPaint.setStrokeWidth(10);
        mErrorPathPaint.setColor(Color.RED);

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
    public void parseTypeArr(@NonNull AttributeSet typedArray) {
        TypedArray a = mContext.getTheme()
                .obtainStyledAttributes(typedArray, R.styleable.PatternView, 0, 0);

        try { //Parse title params
            mPatternPathColor = a.getColor(R.styleable.PatternView_patternLineColor,
                    mContext.getResources().getColor(android.R.color.holo_green_dark));
        } finally {
            a.recycle();
        }
    }

    @Override
    public void measureView(@NonNull Rect rootViewBounds) {
        mBoxPattern.measureView(mRootViewBound);
        mBoxTitle.measureView(mRootViewBound);
    }

    @Override
    public void onAuthenticationFail() {
        super.onAuthenticationFail();
        mBoxPattern.onAuthenticationFail();
        mBoxTitle.onAuthenticationFail();
        isErrorShowing = true;
    }

    @Override
    public void onAuthenticationSuccess() {
        super.onAuthenticationSuccess();
        mBoxPattern.onAuthenticationSuccess();
        mBoxTitle.onAuthenticationSuccess();
        isErrorShowing = false;
    }

    /**
     * Draw method of the view called every time frame refreshes.
     *
     * @param canvas view canvas
     */
    @Override
    public void drawView(@NonNull Canvas canvas) {
        mBoxPattern.drawView(canvas);
        mBoxTitle.drawView(canvas);

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
                    isErrorShowing ? mErrorPathPaint : mNormalPathPaint);
        }

        canvas.drawLine(mPatternTyped.get(lastElementPos).getCenterX(),
                mPatternTyped.get(lastElementPos).getCenterY(),
                mPatternPathEndX, mPatternPathEndY,
                isErrorShowing ? mErrorPathPaint : mNormalPathPaint);
    }

    /**
     * Reset the pin code and view state.
     */
    @Override
    public void reset() {
        super.reset();
        isErrorShowing = false;
        mPatternTyped.clear();
        invalidate();
    }

    ///////////////////////////////////////////////////////////////
    //                  TOUCH HANDLER
    ///////////////////////////////////////////////////////////////


    @SuppressLint("ClickableViewAccessibility")
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

                mPatternPathEndX = touchX;
                mPatternPathEndY = touchY;

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
            onAuthenticationSuccess();
        } else {
            onAuthenticationFail();
        }
        invalidate();
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////

    public void setCorrectPattern(@NonNull PatternPoint[] correctPattern) {
        mCorrectPattern = correctPattern;

        mPatternTyped.clear();
        invalidate();
    }

    @Nullable
    public PatternCell.Builder getPatternCellBuilder() {
        return mBoxPattern.getCellBuilder();
    }

    public boolean isOneHandOperationEnabled() {
        return mBoxPattern.isOneHandOperation();
    }

    public void enableOneHandOperation(boolean isEnable) {
        mBoxPattern.setOneHandOperation(isEnable);
        requestLayout();
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
    public void setTitleColorRes(@ColorRes int titleColor) {
        mBoxTitle.setTitleColor(Utils.getColorCompat(getContext(), titleColor));
        invalidate();
    }


    public int getPatternPathColor() {
        return mPatternPathColor;
    }

    public void setPatternPathColor(@ColorInt int pathColor) {
        mPatternPathColor = pathColor;
        invalidate();
    }

    @SuppressWarnings("deprecation")
    public void setPatternPathColorRes(@ColorRes int pathColor) {
        mPatternPathColor = Utils.getColorCompat(getContext(), pathColor);
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
     * @param titleRes String resource for the title.
     */
    public void setTitle(@StringRes int titleRes) {
        mBoxTitle.setTitle(getContext().getString(titleRes));
        invalidate();
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
