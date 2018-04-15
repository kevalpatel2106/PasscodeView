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

package com.kevalpatel.passcodeview.patternCells;

/**
 * Created by Keval on 19-Apr-17.
 */

public final class PatternPoint {

    private final int mRow;
    private final int mColumn;

    public PatternPoint(final int row, final int column) {
        mRow = row;
        mColumn = column;

        if (row < 0 || column < 0) throw new RuntimeException("Invalid mRow or mColumn number.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternPoint patternPoint = (PatternPoint) o;
        return mRow == patternPoint.mRow && mColumn == patternPoint.mColumn;
    }

    @Override
    public int hashCode() {
        int result = mRow;
        result = 31 * result + mColumn;
        return result;
    }

    @Override
    public String toString() {
        return "PatternPoint(" + mRow + ", " + mColumn + ")";
    }

}
