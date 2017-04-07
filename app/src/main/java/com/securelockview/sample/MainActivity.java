package com.securelockview.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.kevalpatel.passcodeview.interfaces.AuthenticationListener;
import com.kevalpatel.passcodeview.pinView.PinView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PinView pinView = (PinView) findViewById(R.id.pin_view);
        pinView.setPinToCheck("1234");
        pinView.setAuthenticationListener(new AuthenticationListener() {
            @Override
            public void onAuthenticationSuccessful() {
                Toast.makeText(MainActivity.this, "Auth successful.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(MainActivity.this, "Auth failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
