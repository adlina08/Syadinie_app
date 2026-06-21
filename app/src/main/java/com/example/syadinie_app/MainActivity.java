package com.example.syadinie_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Membuka paparan activity_main

        Button btnGoToUpdate = findViewById(R.id.btnGoToUpdate);

        // Fungsi klik untuk lompat ke skrin UpdateDeleteActivity
        btnGoToUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UpdateDeleteActivity.class);
            startActivity(intent);
        });
    }
}