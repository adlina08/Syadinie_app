package com.example.syadinie_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome;
    Button btnLogout, btnGoToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnGoToUpdate = findViewById(R.id.btnGoToUpdate);

        String username = getIntent().getStringExtra("USERNAME");

        tvWelcome.setText("Welcome Back, " + username);

        // Open Update/Delete page
        btnGoToUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this,
                    UpdateDeleteActivity.class);
            startActivity(intent);
        });

        // Logout
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}