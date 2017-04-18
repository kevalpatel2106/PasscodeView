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
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.StringRes;

/**
 * Created by Keval Patel on 13/04/17.
 * This is the builder class to set the names to display on each key. The keys name should be appropriate
 * to their value. By using this you can localize the key names based on users device's current locale.
 * {@link "https://github.com/kevalpatel2106/PasscodeView/wiki/Add-localized-key-names"}
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class KeyNamesBuilder {
    public static final String BACKSPACE_TITLE = "-1";

    private String mKeyOne = "1";
    private String mKeyTwo = "2";
    private String mKeyThree = "3";
    private String mKeyFour = "4";
    private String mKeyFive = "5";
    private String mKeySix = "6";
    private String mKeySeven = "7";
    private String mKeyEight = "8";
    private String mKeyNine = "9";
    private String mKeyZero = "0";


    public KeyNamesBuilder() {
        //Do nothing
    }

    public String getKeyOne() {
        return mKeyOne;
    }

    public KeyNamesBuilder setKeyOne(@NonNull String keyOne) {
        mKeyOne = keyOne;
        return this;
    }

    public KeyNamesBuilder setKeyOne(@NonNull Context context, @StringRes int keyOne) {
        mKeyOne = context.getString(keyOne);
        return this;
    }

    public String getKeyTwo() {
        return mKeyTwo;
    }

    public KeyNamesBuilder setKeyTwo(@NonNull String keyTwo) {
        mKeyTwo = keyTwo;
        return this;
    }

    public KeyNamesBuilder setKeyTwo(@NonNull Context context, @StringRes int keyTwo) {
        mKeyTwo = context.getString(keyTwo);
        return this;
    }

    public String getKeyThree() {
        return mKeyThree;
    }

    public KeyNamesBuilder setKeyThree(@NonNull String keyThree) {
        mKeyThree = keyThree;
        return this;
    }

    public KeyNamesBuilder setKeyThree(@NonNull Context context, @StringRes int keyThree) {
        mKeyThree = context.getString(keyThree);
        return this;
    }

    public String getmKeyFour() {
        return mKeyFour;
    }

    public KeyNamesBuilder setKeyFour(@NonNull String keyFour) {
        mKeyFour = keyFour;
        return this;
    }

    public KeyNamesBuilder setKeyFour(@NonNull Context context, @StringRes int keyFour) {
        mKeyFour = context.getString(keyFour);
        return this;
    }

    public String getKeyFive() {
        return mKeyFive;
    }

    public KeyNamesBuilder setKeyFive(@NonNull String keyFive) {
        mKeyFive = keyFive;
        return this;
    }

    public KeyNamesBuilder setKeyFive(@NonNull Context context, @StringRes int keyFive) {
        mKeyFive = context.getString(keyFive);
        return this;
    }

    public String getKeySix() {
        return mKeySix;
    }

    public KeyNamesBuilder setKeySix(@NonNull String keySix) {
        mKeySix = keySix;
        return this;
    }

    public KeyNamesBuilder setKeySix(@NonNull Context context, @StringRes int keySix) {
        mKeySix = context.getString(keySix);
        return this;
    }

    public String getKeySeven() {
        return mKeySeven;
    }

    public KeyNamesBuilder setKeySeven(@NonNull String keySeven) {
        mKeySeven = keySeven;
        return this;
    }

    public KeyNamesBuilder setKeySeven(@NonNull Context context, @StringRes int keySeven) {
        mKeySeven = context.getString(keySeven);
        return this;
    }

    public String getKeyEight() {
        return mKeyEight;
    }

    public KeyNamesBuilder setKeyEight(@NonNull String keyEight) {
        mKeyEight = keyEight;
        return this;
    }

    public KeyNamesBuilder setKeyEight(@NonNull Context context, @StringRes int keyEight) {
        mKeyEight = context.getString(keyEight);
        return this;
    }

    public String getKeyNine() {
        return mKeyNine;
    }

    public KeyNamesBuilder setKeyNine(@NonNull String keyNine) {
        mKeyNine = keyNine;
        return this;
    }

    public KeyNamesBuilder setKeyNine(@NonNull Context context, @StringRes int keyNine) {
        mKeyNine = context.getString(keyNine);
        return this;
    }

    public String getKeyZero() {
        return mKeyZero;
    }

    public KeyNamesBuilder setKeyZero(@NonNull String keyZero) {
        mKeyZero = keyZero;
        return this;
    }

    public KeyNamesBuilder setKeyZero(@NonNull Context context, @StringRes int keyZero) {
        mKeyZero = context.getString(keyZero);
        return this;
    }

    @SuppressWarnings("Range")
    @Size(Constants.NO_OF_KEY_BOARD_ROWS * Constants.NO_OF_KEY_BOARD_COLUMNS)
    String[][] build() {
        return new String[][]{{mKeyOne, mKeyFour, mKeySeven, ""},
                {mKeyTwo, mKeyFive, mKeyEight, mKeyZero},
                {mKeyThree, mKeySix, mKeyNine, BACKSPACE_TITLE}};
    }

    int getValueOfKey(@NonNull String keyName) {
        if (keyName.equals(mKeyOne)) return 1;
        else if (keyName.equals(mKeyTwo)) return 2;
        else if (keyName.equals(mKeyThree)) return 3;
        else if (keyName.equals(mKeyFour)) return 4;
        else if (keyName.equals(mKeyFive)) return 5;
        else if (keyName.equals(mKeySix)) return 6;
        else if (keyName.equals(mKeySeven)) return 7;
        else if (keyName.equals(mKeyEight)) return 8;
        else if (keyName.equals(mKeyNine)) return 9;
        else if (keyName.equals(mKeyZero)) return 0;
        else if (keyName.equals(BACKSPACE_TITLE)) return -1;
        else throw new IllegalArgumentException("Invalid key name.");
    }
}
