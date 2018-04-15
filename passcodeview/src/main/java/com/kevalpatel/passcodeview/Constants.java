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

/**
 * Created by Keval on 06-Apr-17.
 * Constants for the library.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

public final class Constants {

    /**
     * Number of columns in the PIN keyboard.
     */
    public static final int NO_OF_KEY_BOARD_COLUMNS = 3;

    /**
     * Number of rows in the PIN keyboard.
     */
    public static final int NO_OF_KEY_BOARD_ROWS = 4;

    /**
     * Default number of rows and columns for the the pattern box.
     */
    public static final int DEF_PATTERN_LENGTH = 3;

    /**
     * Weight for the keyboard bottom line.
     */
    public static final float KEY_BOARD_BOTTOM_WEIGHT = 0.14F;

    /**
     * Weight for the keyboard top line.
     */
    public static final float KEY_BOARD_TOP_WEIGHT = 0.2F;

    private Constants() {
    }
}
