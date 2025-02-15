package com.example.fitnessapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PlansActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        // Initialize buttons
        Button buttonWaterTips = findViewById(R.id.buttonWaterTips);
        Button buttonElectricityTips = findViewById(R.id.buttonElectricityTips);
        Button buttonRecyclingTips = findViewById(R.id.buttonRecyclingTips);

        // Set click listeners to open relevant articles
        buttonWaterTips.setOnClickListener(v -> openWebPage("https://www.epa.gov/watersense/start-saving"));
        buttonElectricityTips.setOnClickListener(v -> openWebPage("https://www.energy.gov/energysaver/energy-saver"));
        buttonRecyclingTips.setOnClickListener(v -> openWebPage("https://www.epa.gov/recycle"));
    }

    // Function to open links in the web browser
    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
