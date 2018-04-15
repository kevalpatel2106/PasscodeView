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

package com.securelockview.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kevalpatel.passcodeview.PatternView;
import com.kevalpatel.passcodeview.authenticator.PasscodeViewPatternAuthenticator;
import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.patternCells.CirclePatternCell;
import com.kevalpatel.passcodeview.patternCells.PatternPoint;

/**
 * Created by Keval on 06-Apr-17.
 *
 *@author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
public class PatternViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_view);

        PatternView patternView = findViewById(R.id.pattern_view);

        //Set number of pattern counts.
        //REQUIRED
        patternView.setNoOfColumn(3);   //Number of columns
        patternView.setNoOfRows(3);     //Number of rows

        //Set the correct pin code.
        //Display row and column number of the pattern point sequence.
        //REQUIRED
        final PatternPoint[] correctPattern = new PatternPoint[]{
                new PatternPoint(0, 0),
                new PatternPoint(1, 0),
                new PatternPoint(2, 0),
                new PatternPoint(2, 1)
        };
        patternView.setAuthenticator(new PasscodeViewPatternAuthenticator(correctPattern));

        //Build the desired indicator shape and pass the theme attributes.
        //REQUIRED
        patternView.setPatternCell(new CirclePatternCell.Builder(patternView)
                .setRadius(R.dimen.pattern_cell_radius)
                .setCellColorResource(R.color.colorAccent));

        patternView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                //User authenticated successfully.
                //Navigate to secure screens.
                startActivity(new Intent(PatternViewActivity.this, AuthenticatedActivity.class));
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                //Calls whenever authentication is failed or user is unauthorized.
                //Do something
            }
        });
    }
}
