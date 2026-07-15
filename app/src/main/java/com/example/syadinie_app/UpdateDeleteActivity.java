package com.example.syadinie_app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UpdateDeleteActivity extends BaseActivity { // Mewarisi BackActivity

    DatabaseHelper db;
    long buddyId;
    EditText etName, etHp, etEmail, etAddr1, etAddr2, etAddr3;
    Spinner spGender, spState;
    ImageView ivProfile;

    private byte[] imageBytes = null; // Menyimpan data byte gambar profil

    // Launcher untuk memilih gambar dari galeri peranti
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        ivProfile.setImageBitmap(bitmap);
                        ivProfile.setPadding(0, 0, 0, 0); // Buang padding ikon kamera bawaan

                        // Convert Bitmap kepada Byte Array
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
        setContentView(R.layout.activity_update_delete);

        db = new DatabaseHelper(this);
        buddyId = getIntent().getLongExtra("BUDDY_ID", -1);

        if (buddyId == -1) {
            Toast.makeText(this, "Error: No buddy selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hubungkan komponen UI
        ivProfile = findViewById(R.id.ivProfileUp);
        etName = findViewById(R.id.etNameUp);
        spGender = findViewById(R.id.spGenderUp);
        etHp = findViewById(R.id.etHpUp);
        etEmail = findViewById(R.id.etEmailUp);
        etAddr1 = findViewById(R.id.etAddr1Up);
        etAddr2 = findViewById(R.id.etAddr2Up);
        etAddr3 = findViewById(R.id.etAddr3Up);
        spState = findViewById(R.id.spStateUp);

        // SET ADAPTER SPINNER (Guna layout putih tersuai)
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, R.layout.spinner_item_white);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(
                this, R.array.state_array, R.layout.spinner_item_white);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(stateAdapter);

        // Pilih gambar apabila diklik
        ivProfile.setOnClickListener(v -> openGallery());

        // PANGGIL DATABASE: Masukkan data asal ke dalam borang
        loadBuddyDetails();

        // OPERASI 1: UPDATE (Save Changes)
        findViewById(R.id.btnUpdate).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString();
            String hp = etHp.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String addr1 = etAddr1.getText().toString().trim();
            String addr2 = etAddr2.getText().toString().trim();
            String addr3 = etAddr3.getText().toString().trim();
            String addr4 = spState.getSelectedItem().toString();

            if (name.isEmpty() || gender.equals("Select Gender") || hp.isEmpty() || email.isEmpty() || addr4.equals("Select State")) {
                Toast.makeText(this, "Please complete all required fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("gender", gender);
            cv.put("hp", hp);
            cv.put("email", email);
            cv.put("addr1", addr1);
            cv.put("addr2", addr2);
            cv.put("addr3", addr3);
            cv.put("addr4", addr4);
            cv.put("image", imageBytes); // Kemas kini sekali data gambar (boleh jadi null/lama/baru)

            try {
                SQLiteDatabase database = db.getWritableDatabase();
                int result = database.update("friends", cv, "id=?", new String[]{String.valueOf(buddyId)});
                database.close();

                if (result > 0) {
                    Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Update Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // OPERASI 2: DELETE (with confirmation dialog)
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Delete Buddy")
                    .setMessage("Are you sure you want to delete this buddy? This action cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        try {
                            SQLiteDatabase database = db.getWritableDatabase();
                            int result = database.delete("friends", "id=?", new String[]{String.valueOf(buddyId)});
                            database.close();

                            if (result > 0) {
                                Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Delete failed!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Delete Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    private void loadBuddyDetails() {
        try {
            SQLiteDatabase d = db.getReadableDatabase();
            Cursor c = d.rawQuery("SELECT * FROM friends WHERE id = ?", new String[]{String.valueOf(buddyId)});

            if (c != null && c.moveToFirst()) {
                // Set EditText
                etName.setText(c.getString(1));
                etHp.setText(c.getString(3));
                etEmail.setText(c.getString(4));
                etAddr1.setText(c.getString(5));
                etAddr2.setText(c.getString(6));
                etAddr3.setText(c.getString(7));

                // Set Pilihan Spinner Jantina
                String gender = c.getString(2);
                if (gender != null && spGender.getAdapter() != null) {
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spGender.getAdapter();
                    int position = adapter.getPosition(gender);
                    if (position >= 0) spGender.setSelection(position);
                }

                // Set Pilihan Spinner Negeri
                String state = c.getString(8);
                if (state != null && spState.getAdapter() != null) {
                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spState.getAdapter();
                    int position = adapter.getPosition(state);
                    if (position >= 0) spState.setSelection(position);
                }

                // Ambil data gambar (Kolum indeks ke-9: image BLOB)
                byte[] imgBlob = c.getBlob(9);
                if (imgBlob != null) {
                    imageBytes = imgBlob;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);
                    ivProfile.setImageBitmap(bitmap);
                    ivProfile.setPadding(0, 0, 0, 0); // Buang padding ikon lalai
                }
            }

            if (c != null) c.close();
            d.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error fetching details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}