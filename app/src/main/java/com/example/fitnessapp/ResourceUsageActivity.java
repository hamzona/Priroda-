package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResourceUsageActivity extends AppCompatActivity {
    private TextView textViewWaterUsage, textViewElectricityUsage, textViewFuelUsage;
    private DatabaseHelper databaseHelper;


    private void displayResourceUsage() {
        double waterUsage = databaseHelper.getWeeklyWaterUsage();
        double electricityUsage = databaseHelper.getWeeklyElectricityUsage();
        double fuelUsage = databaseHelper.getWeeklyFuelUsage();

        textViewWaterUsage.setText(String.format("Water Usage: %.2f L", waterUsage));
        textViewElectricityUsage.setText(String.format("Electricity Usage: %.2f kWh", electricityUsage));
        textViewFuelUsage.setText(String.format("Fuel Usage: %.2f L", fuelUsage));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_usage);

        databaseHelper = new DatabaseHelper(this);

        textViewWaterUsage = findViewById(R.id.textViewWaterUsage);
        textViewElectricityUsage = findViewById(R.id.textViewElectricityUsage);
        textViewFuelUsage = findViewById(R.id.textViewFuelUsage);

        displayResourceUsage();

        Button buttonBack = findViewById(R.id.buttonBackToMain);
        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }


}
