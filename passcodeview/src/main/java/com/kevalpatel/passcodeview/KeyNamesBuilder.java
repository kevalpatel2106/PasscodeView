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

    private Context mContext;

    public KeyNamesBuilder(@NonNull Context context) {
        mContext = context;
    }

    public String getKeyOne() {
        return mKeyOne;
    }

    public KeyNamesBuilder setKeyOne(@StringRes int keyOne) {
        mKeyOne = mContext.getString(keyOne);
        return this;
    }

    public KeyNamesBuilder setKeyOne(@NonNull String keyOne) {
        mKeyOne = keyOne;
        return this;
    }

    public String getKeyTwo() {
        return mKeyTwo;
    }

    public KeyNamesBuilder setKeyTwo(@StringRes int keyTwo) {
        mKeyTwo = mContext.getString(keyTwo);
        return this;
    }

    public KeyNamesBuilder setKeyTwo(@NonNull String keyTwo) {
        mKeyTwo = keyTwo;
        return this;
    }

    public String getKeyThree() {
        return mKeyThree;
    }

    public KeyNamesBuilder setKeyThree(@StringRes int keyThree) {
        mKeyThree = mContext.getString(keyThree);
        return this;
    }

    public KeyNamesBuilder setKeyThree(@NonNull String keyThree) {
        mKeyThree = keyThree;
        return this;
    }

    public String getmKeyFour() {
        return mKeyFour;
    }

    public KeyNamesBuilder setKeyFour(String keyFour) {
        mKeyFour = keyFour;
        return this;
    }

    public KeyNamesBuilder setKeyFour(@StringRes int keyFour) {
        mKeyFour = mContext.getString(keyFour);
        return this;
    }

    public String getKeyFive() {
        return mKeyFive;
    }

    public KeyNamesBuilder setKeyFive(@StringRes int keyFive) {
        mKeyFive = mContext.getString(keyFive);
        return this;
    }

    public KeyNamesBuilder setKeyFive(@NonNull String keyFive) {
        mKeyFive = keyFive;
        return this;
    }

    public String getKeySix() {
        return mKeySix;
    }

    public KeyNamesBuilder setKeySix(@StringRes int keySix) {
        mKeySix = mContext.getString(keySix);
        return this;
    }

    public KeyNamesBuilder setKeySix(@NonNull String keySix) {
        mKeySix = keySix;
        return this;
    }

    public String getKeySeven() {
        return mKeySeven;
    }

    public KeyNamesBuilder setKeySeven(@StringRes int keySeven) {
        mKeySeven = mContext.getString(keySeven);
        return this;
    }

    public KeyNamesBuilder setKeySeven(@NonNull String keySeven) {
        mKeySeven = keySeven;
        return this;
    }

    public String getKeyEight() {
        return mKeyEight;
    }

    public KeyNamesBuilder setKeyEight(@StringRes int keyEight) {
        mKeyEight = mContext.getString(keyEight);
        return this;
    }

    public KeyNamesBuilder setKeyEight(@NonNull String keyEight) {
        mKeyEight = keyEight;
        return this;
    }

    public String getKeyNine() {
        return mKeyNine;
    }

    public KeyNamesBuilder setKeyNine(@StringRes int keyNine) {
        mKeyNine = mContext.getString(keyNine);
        return this;
    }

    public KeyNamesBuilder setKeyNine(@NonNull String keyNine) {
        mKeyNine = keyNine;
        return this;
    }

    public String getKeyZero() {
        return mKeyZero;
    }

    public KeyNamesBuilder setKeyZero(@StringRes int keyZero) {
        mKeyZero = mContext.getString(keyZero);
        return this;
    }

    public KeyNamesBuilder setKeyZero(@NonNull String keyZero) {
        mKeyZero = keyZero;
        return this;
    }

    @SuppressWarnings("Range")
    @Size(Constants.NO_OF_ROWS * Constants.NO_OF_COLUMNS)
    public String[][] build() {
        return new String[][]{{mKeyOne, mKeyFour, mKeySeven, ""},
                {mKeyTwo, mKeyFive, mKeyEight, mKeyZero},
                {mKeyThree, mKeySix, mKeyNine, BACKSPACE_TITLE}};
    }

    public int getValueOfKey(@NonNull String keyName) {
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
