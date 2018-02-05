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

package com.kevalpatel.passcodeview.internal.box;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;
import com.kevalpatel.passcodeview.internal.Constants;
import com.kevalpatel.passcodeview.patternCells.PatternCell;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class BoxPattern extends Box {
    private boolean mIsOneHandOperation = false;    //Bool to set true if you want to display one hand key board.

    private int mNoOfColumn;
    private int mNoOfRows;

    private ArrayList<PatternCell> mPatternCells;
    private Rect mPatternBoxBound = new Rect();

    private PatternCell.Builder mCellBuilder;    //Pattern indicator builder

    /**
     * Public constructor
     *
     * @param basePasscodeView {@link BasePasscodeView} in which box will be displayed.
     */
    public BoxPattern(@NonNull BasePasscodeView basePasscodeView) {
        super(basePasscodeView);
    }

    @Override
    public void init() {
        mNoOfColumn = 0;
        mNoOfRows = 0;
    }

    /**
     * Measure and display the keypad box.
     * |------------------------|=|
     * |                        | |
     * |                        | | => The title and the indicator. ({@link BoxTitleIndicator#measureView(Rect)})
     * |                        | |
     * |                        | |
     * |------------------------|=| => {@link Constants#KEY_BOARD_TOP_WEIGHT} of the total height.
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | | => Keypad height.
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |                        | |
     * |------------------------|=|=> {@link Constants#KEY_BOARD_BOTTOM_WEIGHT} of the total weight if the fingerprint is available. Else it touches to the bottom of the main view.
     * |                        | |
     * |                        | |=> Section for fingerprint. If the fingerprint is enabled. Otherwise keyboard streaches to the bottom of the root view.
     * |------------------------|=|
     * Don't change until you know what you are doing. :-)
     *
     * @param rootViewBound bound of the main view.
     */
    @Override
    public void measureView(@NonNull Rect rootViewBound) {
        mPatternBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mPatternBoxBound.right = rootViewBound.width();
        mPatternBoxBound.top = (int) (rootViewBound.top + (rootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT));
        mPatternBoxBound.bottom = (int) (rootViewBound.bottom -
                rootViewBound.height() * (getRootView().isFingerPrintEnable() ? Constants.KEY_BOARD_BOTTOM_WEIGHT : 0));

        float singleIndicatorHeight = mPatternBoxBound.height() / mNoOfRows;
        float singleIndicatorWidth = mPatternBoxBound.width() / mNoOfColumn;

        mPatternCells = new ArrayList<>();
        for (int colNo = 0; colNo < mNoOfColumn; colNo++) {
            for (int rowNo = 0; rowNo < mNoOfRows; rowNo++) {
                Rect indicatorBound = new Rect();
                indicatorBound.left = (int) ((colNo * singleIndicatorWidth) + mPatternBoxBound.left);
                indicatorBound.right = (int) (indicatorBound.left + singleIndicatorWidth);
                indicatorBound.top = (int) ((rowNo * singleIndicatorHeight) + mPatternBoxBound.top);
                indicatorBound.bottom = (int) (indicatorBound.top + singleIndicatorHeight);

                mPatternCells.add(mCellBuilder.getCell(indicatorBound, new Point(rowNo, colNo)));
            }
        }
    }

    @Override
    public void preparePaint() {
        //Do nothing
    }

    @Override
    public void parseTypeArr(@NonNull AttributeSet typedArray) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(typedArray,
                R.styleable.PatternView, 0, 0);

        try { //Parse title params
            mNoOfRows = a.getInt(R.styleable.PatternView_noOfRows, Constants.DEF_PATTERN_LENGTH);
            mNoOfColumn = a.getInt(R.styleable.PatternView_noOfColumns, Constants.DEF_PATTERN_LENGTH);
        } finally {
            a.recycle();
        }
    }

    /**
     * Set the default theme parameters.
     */
    @Override
    public void setDefaults() {
        mNoOfRows = Constants.DEF_PATTERN_LENGTH;
        mNoOfColumn = Constants.DEF_PATTERN_LENGTH;
    }

    @Override
    public void onAuthenticationFail() {
        //Play failed animation for all keys
        for (PatternCell patternCell : mPatternCells) patternCell.onAuthFailed();
        getRootView().invalidate();
    }

    @Override
    public void onAuthenticationSuccess() {
        //Play success animation for all keys
        for (PatternCell patternCell : mPatternCells) patternCell.onAuthSuccess();
        getRootView().invalidate();
    }

    @Override
    public void reset() {
        //TODO reset the view
    }

    /**
     * Draw pattern box on the canvas.
     *
     * @param canvas canvas on which the keyboard will be drawn.
     */
    public void drawView(@NonNull Canvas canvas) {
        for (PatternCell patternCell : mPatternCells) patternCell.draw(canvas);
    }

    ///////////////// SETTERS/GETTERS //////////////

    /**
     * Find which key is pressed based on the ACTION_DOWN and ACTION_UP coordinates.
     */
    @Nullable
    public PatternCell findCell(float touchX, float touchY) {
        for (PatternCell patternCell : mPatternCells)
            if (patternCell.isIndicatorTouched(touchX, touchY)) return patternCell;
        return null;
    }

    public ArrayList<PatternCell> getPatternCells() {
        return mPatternCells;
    }

    public Rect getBounds() {
        return mPatternBoxBound;
    }

    public boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    public void setOneHandOperation(boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }

    public PatternCell.Builder getCellBuilder() {
        return mCellBuilder;
    }

    public void setCellBuilder(@NonNull PatternCell.Builder mIndicatorBuilder) {
        this.mCellBuilder = mIndicatorBuilder;
    }

    public int getNoOfColumn() {
        return mNoOfColumn;
    }

    public void setNoOfColumn(int noOfColumn) {
        mNoOfColumn = noOfColumn;
    }

    public int getNoOfRows() {
        return mNoOfRows;
    }

    public void setNoOfRows(int noOfRows) {
        mNoOfRows = noOfRows;
    }
}
