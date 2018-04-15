/*
 * Copyright 2018 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel.passcodeview.internal;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.kevalpatel.passcodeview.Constants;
import com.kevalpatel.passcodeview.R;
import com.kevalpatel.passcodeview.patternCells.PatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 07-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class BoxPattern extends Box {
    /**
     * Boolean to indicate if the keyboard in the one hand operation? If this is true, the keys will be
     * shrieked horizontally to accommodate in small areas.
     */
    private boolean mIsOneHandOperation = false;

    /**
     * Number of the columns in the pattern view.
     */
    private int mNoOfColumn = 0;

    /**
     * Number of the rows in the pattern view.
     */
    private int mNoOfRows = 0;

    /**
     * List of all the {@link PatternCell} in this box. The size of this array will be
     * {@link #mNoOfColumn} * {@link #mNoOfRows}.
     */
    private ArrayList<PatternCell> mPatternCells;

    /**
     * {@link Rect} with the bound of this box.
     */
    private Rect mPatternBoxBound = new Rect();

    /**
     * builder of the {@link PatternCell}.
     *
     * @see PatternCell.Builder
     */
    private PatternCell.Builder mCellBuilder;

    /**
     * Public constructor
     *
     * @param passcodeView {@link BasePasscodeView} in which box will be displayed.
     */
    public BoxPattern(@NonNull final BasePasscodeView passcodeView) {
        super(passcodeView);
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
        //Pattern box bounds.
        mPatternBoxBound.left = mIsOneHandOperation ? (int) (rootViewBound.width() * 0.3) : 0;
        mPatternBoxBound.right = rootViewBound.width();
        mPatternBoxBound.top = (int) (rootViewBound.top + (rootViewBound.height() * Constants.KEY_BOARD_TOP_WEIGHT));
        mPatternBoxBound.bottom = (int) (rootViewBound.bottom -
                rootViewBound.height() * (getRootView().isFingerPrintEnable() ? Constants.KEY_BOARD_BOTTOM_WEIGHT : 0));

        //Prepare the list of indicators.
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

                mPatternCells.add(mCellBuilder.buildInternal(indicatorBound, new PatternPoint(rowNo, colNo)));
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
    }

    @Override
    public void onAuthenticationSuccess() {
        //Play success animation for all keys
        for (PatternCell patternCell : mPatternCells) patternCell.onAuthSuccess();
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

    /**
     * Find which key is pressed based on the ACTION_DOWN and ACTION_UP coordinates.
     */
    @Nullable
    public PatternCell findCell(float touchX, float touchY) {
        for (PatternCell patternCell : mPatternCells)
            if (patternCell.isIndicatorTouched(touchX, touchY)) return patternCell;
        return null;
    }

    ///////////////// SETTERS/GETTERS //////////////

    @InternalApi
    public boolean isOneHandOperation() {
        return mIsOneHandOperation;
    }

    @InternalApi
    public void setOneHandOperation(final boolean oneHandOperation) {
        mIsOneHandOperation = oneHandOperation;
    }

    @InternalApi
    @NonNull
    public PatternCell.Builder getCellBuilder() {
        return mCellBuilder;
    }

    @InternalApi
    public void setCellBuilder(@NonNull final PatternCell.Builder mIndicatorBuilder) {
        this.mCellBuilder = mIndicatorBuilder;
    }

    @InternalApi
    public int getNoOfColumn() {
        return mNoOfColumn;
    }

    @InternalApi
    public void setNoOfColumn(final int noOfColumn) {
        mNoOfColumn = noOfColumn;
    }

    @InternalApi
    public int getNoOfRows() {
        return mNoOfRows;
    }

    @InternalApi
    public void setNoOfRows(final int noOfRows) {
        mNoOfRows = noOfRows;
    }
}
