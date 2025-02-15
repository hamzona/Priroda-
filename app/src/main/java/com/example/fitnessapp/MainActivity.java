package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonProfile = findViewById(R.id.buttonProfile);
        Button buttonPlans = findViewById(R.id.buttonPlans);
        Button buttonTracking = findViewById(R.id.buttonTracking);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        String loggedInUsername = getIntent().getStringExtra("username");

        buttonProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("username", loggedInUsername);  // Prosljeđivanje korisničkog imena
            startActivity(intent);
        });

        buttonPlans.setOnClickListener(v -> navigateTo(PlansActivity.class));
        buttonTracking.setOnClickListener(v -> navigateTo(TrackingActivity.class));

        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
    }
}
