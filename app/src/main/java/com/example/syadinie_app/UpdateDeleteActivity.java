package com.example.syadinie_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateDeleteActivity extends AppCompatActivity {
    DatabaseHelper db;
    long buddyId;
    EditText etName, etHp, etEmail, etAddr1, etAddr2, etAddr3, etAddr4;
    Spinner spGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_delete);

        db = new DatabaseHelper(this);
        buddyId = getIntent().getLongExtra("BUDDY_ID", -1);

        etName = findViewById(R.id.etNameUp);
        spGender = findViewById(R.id.spGenderUp);
        etHp = findViewById(R.id.etHpUp);
        etEmail = findViewById(R.id.etEmailUp);
        etAddr1 = findViewById(R.id.etAddr1Up);
        etAddr2 = findViewById(R.id.etAddr2Up);
        etAddr3 = findViewById(R.id.etAddr3Up);
        etAddr4 = findViewById(R.id.etAddr4Up);

        // Ambil maklumat dari database
        SQLiteDatabase d = db.getReadableDatabase();
        Cursor c = d.rawQuery("SELECT * FROM friends WHERE _id = " + buddyId, null);
        if(c.moveToFirst()) {
            etName.setText(c.getString(1));
            spGender.setSelection(c.getString(2).equals("Male") ? 0 : 1);
            etHp.setText(c.getString(3));
            etEmail.setText(c.getString(4));
            etAddr1.setText(c.getString(5));
            etAddr2.setText(c.getString(6));
            etAddr3.setText(c.getString(7));
            etAddr4.setText(c.getString(8));
        }
        c.close();

        // Operasi UPDATE
        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            ContentValues cv = new ContentValues();
            cv.put("name", etName.getText().toString());
            cv.put("gender", spGender.getSelectedItem().toString());
            cv.put("hp", etHp.getText().toString());
            cv.put("email", etEmail.getText().toString());
            cv.put("addr1", etAddr1.getText().toString());
            cv.put("addr2", etAddr2.getText().toString());
            cv.put("addr3", etAddr3.getText().toString());
            cv.put("addr4", etAddr4.getText().toString().toUpperCase());

            db.getWritableDatabase().update("friends", cv, "_id=?", new String[]{String.valueOf(buddyId)});
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Operasi DELETE
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            db.getWritableDatabase().delete("friends", "_id=?", new String[]{String.valueOf(buddyId)});
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}