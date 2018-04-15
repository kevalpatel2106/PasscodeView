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

package com.kevalpatel.passcodeview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.kevalpatel.passcodeview.authenticator.PatternAuthenticator;
import com.kevalpatel.passcodeview.internal.BasePasscodeView;
import com.kevalpatel.passcodeview.internal.BoxPattern;
import com.kevalpatel.passcodeview.internal.BoxTitle;
import com.kevalpatel.passcodeview.patternCells.PatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

import java.util.ArrayList;

/**
 * Created by Keval on 06-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class PatternView extends BasePasscodeView {

    /**
     * {@link ArrayList} that holds the list of all the {@link PatternCell} touched by the user
     * while drawing the pattern.
     */
    private ArrayList<PatternCell> mPatternTyped;

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
     *
     * @see BoxPattern
     */
    private BoxPattern mBoxPattern;

    /**
     * {@link BoxTitle} to display the title message.
     *
     * @see BoxTitle
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

    /**
     * {@link PatternAuthenticator} to perform authentication on the pattern entered by the user.
     * This field is required to set.
     *
     * @see #setAuthenticator(PatternAuthenticator)
     * @see #getAuthenticator()
     */
    private PatternAuthenticator mAuthenticator;

    /**
     * Instance of the currently running {@link PatternAuthenticatorTask}. If the value is null that indicates,
     * no {@link PatternAuthenticatorTask} is running currently.
     *
     * @see PatternAuthenticatorTask
     */
    @Nullable
    private PatternAuthenticatorTask mPatternAuthenticatorTask;

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

        mPatternPathColor = getResources().getColor(android.R.color.holo_green_dark);
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
        final TypedArray a = getContext().getTheme()
                .obtainStyledAttributes(typedArray, R.styleable.PatternView, 0, 0);

        try { //Parse title params
            mPatternPathColor = a.getColor(R.styleable.PatternView_patternLineColor,
                    getResources().getColor(android.R.color.holo_green_dark));
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPatternAuthenticatorTask != null) mPatternAuthenticatorTask.cancel(true);
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

                final PatternCell cellNumber = mBoxPattern.findCell(touchX, touchY);

                if (cellNumber != null && !mPatternTyped.contains(cellNumber)) {
                    mPatternTyped.add(cellNumber);
                    giveTactileFeedbackForKeyPress();
                }

                mPatternPathEndX = touchX;
                mPatternPathEndY = touchY;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //Validate the current state
                if (mPatternTyped.size() == 0) return true;
                if (mAuthenticator == null) {
                    throw new IllegalStateException("Set authenticator first.");
                }

                if (mPatternAuthenticatorTask != null
                        && mPatternAuthenticatorTask.getStatus() == AsyncTask.Status.RUNNING)
                    mPatternAuthenticatorTask.cancel(true);

                mPatternAuthenticatorTask = new PatternAuthenticatorTask(mAuthenticator);
                //noinspection unchecked
                mPatternAuthenticatorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mPatternTyped);
                break;
            default:
                return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////
    //                  GETTERS/SETTERS
    ///////////////////////////////////////////////////////////////


    public PatternAuthenticator getAuthenticator() {
        return mAuthenticator;
    }

    public void setAuthenticator(final PatternAuthenticator authenticator) {
        mAuthenticator = authenticator;
    }

    //********************** For pattern box

    public boolean isOneHandOperationEnabled() {
        return mBoxPattern.isOneHandOperation();
    }

    public void enableOneHandOperation(boolean isEnable) {
        mBoxPattern.setOneHandOperation(isEnable);
        requestLayout();
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

    //********************** For title box

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


    @SuppressLint("StaticFieldLeak")
    private final class PatternAuthenticatorTask extends AsyncTask<ArrayList<PatternCell>, Void, Boolean> {

        @NonNull
        private final PatternAuthenticator mAuthenticator;

        @NonNull
        private final Handler mHandler;

        @NonNull
        private final Runnable mResetRunnable;

        private PatternAuthenticatorTask(@NonNull final PatternAuthenticator authenticator) {
            mAuthenticator = authenticator;
            mHandler = new Handler(Looper.getMainLooper());
            mResetRunnable = new Runnable() {
                @Override
                public void run() {
                    reset();
                }
            };
        }

        @SafeVarargs
        @Override
        protected final Boolean doInBackground(final ArrayList<PatternCell>... pinTyped) {
            //Prepare the pattern points
            final ArrayList<PatternPoint> patternPoints = new ArrayList<>(pinTyped[0].size());
            for (PatternCell cell : pinTyped[0]) {
                patternPoints.add(cell.getPoint());
            }

            return this.mAuthenticator.isValidPattern(patternPoints);
        }

        @Override
        protected void onPostExecute(final Boolean isAuthenticated) {
            super.onPostExecute(isAuthenticated);

            if (isAuthenticated) {
                //Hurray!!! Authentication is successful.
                onAuthenticationSuccess();
            } else {
                //:-( Authentication failed.
                onAuthenticationFail();
            }

            //Reset the view.
            mHandler.postDelayed(mResetRunnable, 350);
            mPatternAuthenticatorTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mHandler.removeCallbacks(mResetRunnable);
            mPatternAuthenticatorTask = null;
        }
    }
}
