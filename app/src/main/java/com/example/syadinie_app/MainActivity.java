package com.example.syadinie_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnGoToCreate, btnGoToSearch, btnGoToUpdate, btnChart, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Membuka paparan menu utama

        DatabaseHelper db = new DatabaseHelper(this);

        android.database.sqlite.SQLiteDatabase forceOpen = db.getWritableDatabase();

        tvWelcome = findViewById(R.id.tvWelcome);
        btnGoToCreate = findViewById(R.id.btnGoToCreate);
        btnGoToSearch = findViewById(R.id.btnGoToSearch);
        btnLogout = findViewById(R.id.btnLogout);
        btnChart = findViewById(R.id.btnChart);

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null && !username.isEmpty()) {
            tvWelcome.setText("Welcome Back, " + username);
        } else {
            tvWelcome.setText("Welcome Back, User");
        }

        // 3. FUNGSI BUTANG 1: Lompat ke skrin CreateBuddyActivity
        btnGoToCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateBuddyActivity.class);
            startActivity(intent);
        });
        btnChart.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChartActivity.class);
            startActivity(intent);
        });

        // 4. FUNGSI BUTANG 2: Lompat ke skrin ViewFriendsActivity (Search/View)
        btnGoToSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewFriendsActivity.class);
            startActivity(intent);
        });


        // 6. FUNGSI BUTANG 4: Log Keluar (Logout) balik ke LoginActivity
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup skrin dashboard
        });
    }
}