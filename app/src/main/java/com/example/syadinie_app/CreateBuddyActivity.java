package com.example.syadinie_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateBuddyActivity extends AppCompatActivity {

    EditText etName, etGender, etPhone, etEmail;
    EditText etAddress1, etAddress2, etAddress3, etAddress4;
    Button btnSave;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_buddy);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etGender = findViewById(R.id.etGender);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etAddress3 = findViewById(R.id.etAddress3);
        etAddress4 = findViewById(R.id.etAddress4);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveFriend());
    }

    private void saveFriend() {
        String name = etName.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String hp = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String addr1 = etAddress1.getText().toString().trim();
        String addr2 = etAddress2.getText().toString().trim();
        String addr3 = etAddress3.getText().toString().trim();
        String addr4 = etAddress4.getText().toString().trim();

        if (name.isEmpty() || gender.isEmpty() || hp.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill in name, gender, phone and email", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.insertFriend(
                name,
                gender,
                hp,
                email,
                addr1,
                addr2,
                addr3,
                addr4,
                null
        );

        if (inserted) {
            Toast.makeText(this, "Buddy saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save buddy", Toast.LENGTH_SHORT).show();
        }
    }
}