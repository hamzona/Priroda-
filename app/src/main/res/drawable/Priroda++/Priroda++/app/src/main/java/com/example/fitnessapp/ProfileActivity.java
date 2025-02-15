package com.example.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView textViewUsername = findViewById(R.id.textViewUsername);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        try (DatabaseHelper databaseHelper = new DatabaseHelper(this)) {
            String loggedInUsername = getIntent().getStringExtra("username");

            if (loggedInUsername != null && !loggedInUsername.isEmpty()) {
                Log.d("ProfileActivity", "Proslijeđeno korisničko ime: " + loggedInUsername);
                User user = databaseHelper.getUserByUsername(loggedInUsername);

                if (user != null) {
                    Log.d("ProfileActivity", "Korisnik pronađen: " + user.getUsername());
                    textViewUsername.setText("Korisničko ime: " + user.getUsername());
                    textViewEmail.setText("Email: " + user.getEmail());
                } else {
                    Log.e("ProfileActivity", "Korisnik nije pronađen u bazi.");
                }
            } else {
                Log.e("ProfileActivity", "Nema proslijeđenog korisničkog imena.");
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Greška pri dohvaćanju podataka korisnika: " + e.getMessage());
        }

        buttonBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
