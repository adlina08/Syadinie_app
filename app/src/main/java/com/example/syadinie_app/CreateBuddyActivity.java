package com.example.syadinie_app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateBuddyActivity extends BaseActivity { // Guna BackActivity untuk butang kembali

    EditText etName, etPhone, etEmail;
    EditText etAddress1, etAddress2, etAddress3;
    Spinner spGender, spState;
    Button btnSave;
    ImageView ivProfile;
    DatabaseHelper db;

    // Untuk simpan byte array gambar (lalai adalah null sekiranya user tak upload gambar)
    private byte[] imageBytes = null;

    // Launcher untuk memilih gambar dari galeri telefon
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Tukarkan Uri jepada Bitmap dan paparkan pada ImageView
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ivProfile.setImageBitmap(bitmap);
                        ivProfile.setPadding(0, 0, 0, 0); // Buang padding ikon kamera asal

                        // Tukar terus Bitmap kepada Byte Array untuk SQLite BLOB
                        imageBytes = getBytesFromBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_buddy);

        db = new DatabaseHelper(this);

        // Hubungkan semua komponen UI
        ivProfile = findViewById(R.id.ivProfile); // ID ImageView baru
        etName = findViewById(R.id.etName);
        spGender = findViewById(R.id.spGender);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etAddress3 = findViewById(R.id.etAddress3);
        spState = findViewById(R.id.spState);
        btnSave = findViewById(R.id.btnSave);

        // Bila pengguna ketik gambar profil, buka galeri telefon
        ivProfile.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> saveFriend());
    }

    // Fungsi membuka galeri gambar
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // Fungsi menukar objek Bitmap ke Byte Array (BLOB)
    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress gambar kepada JPEG dengan kualiti 70% supaya database tak terlalu berat
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
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

        // Sekarang kita hantar 'imageBytes' (yang mengandungi data gambar) ke pangkalan data!
        boolean inserted = db.insertFriend(name, gender, hp, email, addr1, addr2, addr3, addr4, imageBytes);

        if (inserted) {
            Toast.makeText(this, "Buddy saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save buddy", Toast.LENGTH_SHORT).show();
        }
    }
}