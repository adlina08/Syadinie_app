package com.example.syadinie_app;

import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Kelas induk untuk semua Activity yang memerlukan butang kembali secara automatik.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        // Aktifkan butang kembali pada Action Bar secara automatik
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    // Mengawal fungsi klik pada butang kembali di Toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}