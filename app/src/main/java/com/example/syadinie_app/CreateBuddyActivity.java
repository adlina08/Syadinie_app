package com.example.syadinie_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateBuddyActivity extends AppCompatActivity {

    EditText etName, etPhone, etEmail;
    EditText etAddress1, etAddress2, etAddress3;
    Spinner spGender, spState;
    Button btnSave;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_buddy);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        spGender = findViewById(R.id.spGender);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etAddress3 = findViewById(R.id.etAddress3);
        spState = findViewById(R.id.spState);
        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveFriend());
    }

    private void saveFriend() {
        String name = etName.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();
        String hp = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String addr1 = etAddress1.getText().toString().trim();
        String addr2 = etAddress2.getText().toString().trim();
        String addr3 = etAddress3.getText().toString().trim();
        String addr4 = spState.getSelectedItem().toString();

        if (name.isEmpty()
                || gender.equals("Select Gender")
                || hp.isEmpty()
                || email.isEmpty()
                || addr4.equals("Select State")) {

            Toast.makeText(this, "Please complete all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = db.insertFriend(name, gender, hp, email, addr1, addr2, addr3, addr4, null);

        if (inserted) {
            Toast.makeText(this, "Buddy saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save buddy", Toast.LENGTH_SHORT).show();
        }
    }
}