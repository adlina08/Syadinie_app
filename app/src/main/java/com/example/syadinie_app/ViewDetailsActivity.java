package com.example.syadinie_app;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewDetailsActivity extends BaseActivity {

    DatabaseHelper db;
    long buddyId;
    TextView tvDetName, tvDetGender, tvDetHp, tvDetEmail, tvDetAddress;
    ImageView ivDetProfile;
    Button btnGoToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        db = new DatabaseHelper(this);
        buddyId = getIntent().getLongExtra("BUDDY_ID", -1);

        if (buddyId == -1) {
            Toast.makeText(this, "Error: No buddy selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ivDetProfile = findViewById(R.id.ivDetProfile);
        tvDetName = findViewById(R.id.tvDetName);
        tvDetGender = findViewById(R.id.tvDetGender);
        tvDetHp = findViewById(R.id.tvDetHp);
        tvDetEmail = findViewById(R.id.tvDetEmail);
        tvDetAddress = findViewById(R.id.tvDetAddress);
        btnGoToEdit = findViewById(R.id.btnGoToEdit);

        displayDetails();

        btnGoToEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewDetailsActivity.this, UpdateDeleteActivity.class);
            intent.putExtra("BUDDY_ID", buddyId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDetails(); // Dipanggil semula bila kembali dari skrin Edit supaya data ter-refresh
    }

    private void displayDetails() {
        try {
            SQLiteDatabase d = db.getReadableDatabase();
            Cursor c = d.rawQuery("SELECT * FROM friends WHERE id = ?", new String[]{String.valueOf(buddyId)});

            if (c != null && c.moveToFirst()) {
                // Urutan indeks: id(0), name(1), gender(2), hp(3), email(4), addr1(5), addr2(6), addr3(7), addr4(8), image(9)
                String name = c.getString(1);
                String gender = c.getString(2);
                String hp = c.getString(3);
                String email = c.getString(4);
                String addr1 = c.getString(5);
                String addr2 = c.getString(6);
                String addr3 = c.getString(7);
                String addr4 = c.getString(8);

                tvDetName.setText(name != null ? name : "-");
                tvDetGender.setText(gender != null ? gender : "-");
                tvDetHp.setText(hp != null ? hp : "-");
                tvDetEmail.setText(email != null ? email : "-");

                // Mengurus susunan alamat ke bawah baris demi baris secara kemas
                StringBuilder fullAddress = new StringBuilder();
                if (addr1 != null && !addr1.trim().isEmpty()) fullAddress.append(addr1).append("\n");
                if (addr2 != null && !addr2.trim().isEmpty()) fullAddress.append(addr2).append("\n");
                if (addr3 != null && !addr3.trim().isEmpty()) fullAddress.append(addr3).append("\n");
                if (addr4 != null && !addr4.trim().isEmpty()) fullAddress.append(addr4);

                tvDetAddress.setText(fullAddress.toString().trim());

                // AMBIL DATA GAMBAR DAN PAPARKAN (Indeks Kolum 9)
                byte[] imgBlob = c.getBlob(9);
                if (imgBlob != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);
                    ivDetProfile.setImageBitmap(bitmap);
                    ivDetProfile.setPadding(0, 0, 0, 0); // Buang padding ikon kamera asal
                } else {
                    // Set balik gambar default kamera kalau memang data gambar tiada
                    ivDetProfile.setImageResource(android.R.drawable.ic_menu_camera);
                    ivDetProfile.setPadding(10, 10, 10, 10);
                }
            }
            if (c != null) c.close();
            d.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error showing details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}