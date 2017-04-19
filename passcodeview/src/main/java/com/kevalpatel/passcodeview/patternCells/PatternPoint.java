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

package com.kevalpatel.passcodeview.patternCells;

/**
 * Created by Keval on 19-Apr-17.
 */

public final class PatternPoint {

    private final int row;
    private final int column;

    public PatternPoint(int row, int column) {
        this.row = row;
        this.column = column;

        if (row < 0 || column < 0) throw new RuntimeException("Invalid row or column number.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PatternPoint patternPoint = (PatternPoint) o;
        return row == patternPoint.row && column == patternPoint.column;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return "PatternPoint(" + row + ", " + column + ")";
    }

}
