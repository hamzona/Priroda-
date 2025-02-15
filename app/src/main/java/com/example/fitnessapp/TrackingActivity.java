package com.example.fitnessapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TrackingActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private TextView textViewWaterSummary;
    private EditText editTextWaterUsage, editTextElectricityUsage, editTextFuelUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        databaseHelper = new DatabaseHelper(this);

        textViewWaterSummary = findViewById(R.id.textViewWaterSummary);
        editTextWaterUsage = findViewById(R.id.editTextWaterUsage);
        editTextElectricityUsage = findViewById(R.id.editTextElectricityUsage);
        editTextFuelUsage = findViewById(R.id.editTextFuelUsage);
        Button buttonSaveWaterUsage = findViewById(R.id.buttonSaveWaterUsage);
        Button buttonSaveElectricityUsage = findViewById(R.id.buttonSaveElectricityUsage);
        Button buttonSaveFuelUsage = findViewById(R.id.buttonSaveFuelUsage);
        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);

        displayWaterUsageSummary();

        buttonSaveWaterUsage.setOnClickListener(v -> saveWaterUsage());
        buttonSaveElectricityUsage.setOnClickListener(v -> saveElectricityUsage());
        buttonSaveFuelUsage.setOnClickListener(v -> saveFuelUsage());

        buttonBackToMain.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveWaterUsage() {
        saveConsumption(editTextWaterUsage, "Water", databaseHelper::addWaterUsage);
        displayWaterUsageSummary();
    }

    private void saveElectricityUsage() {
        saveConsumption(editTextElectricityUsage, "Electricity", databaseHelper::addElectricityUsage);
    }

    private void saveFuelUsage() {
        saveConsumption(editTextFuelUsage, "Fuel", databaseHelper::addFuelUsage);
    }

    private void saveConsumption(EditText inputField, String type, ConsumptionSaver saver) {
        String input = inputField.getText().toString().trim();

        if (!input.isEmpty()) {
            try {
                double usage = Double.parseDouble(input);
                long result = saver.save(usage);

                if (result != -1) {
                    Toast.makeText(this, type + " usage saved!", Toast.LENGTH_SHORT).show();
                    inputField.setText("");
                } else {
                    Toast.makeText(this, "Failed to save " + type + " usage.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input for " + type + ". Enter a valid number.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Enter " + type + " usage.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    private void displayWaterUsageSummary() {
        double weeklyWaterUsage = databaseHelper.getWeeklyWaterUsage();
        textViewWaterSummary.setText(String.format("Weekly Water Usage: %.2f L", weeklyWaterUsage));
    }

    @FunctionalInterface
    interface ConsumptionSaver {
        long save(double value);
    }
}
