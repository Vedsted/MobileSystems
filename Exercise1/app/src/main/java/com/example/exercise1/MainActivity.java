package com.example.exercise1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToLocationActivity(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void goToLightActivity(View view) {
        Intent intent = new Intent(this, LightActivity.class);
        startActivity(intent);
    }

    public void goToTemperatureActivity(View view) {
        Intent intent = new Intent(this, ProximityActivity.class);
        startActivity(intent);
    }

    public void goToGyroscopeActivity(View view) {
        Intent intent = new Intent(this, GyroscopeActivity.class);
        startActivity(intent);
    }

    public void goToGravityActivity(View view) {
        Intent intent = new Intent(this, GravityActivity.class);
        startActivity(intent);
    }
}
