package com.example.syadinie_app;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ChartActivity extends AppCompatActivity {

    PieChart pieChart;
    DatabaseHelper db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "Chart Activity Opened", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        pieChart = findViewById(R.id.pieChart);
        db = new DatabaseHelper(this);

        loadChart();
    }

    private void loadChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        Cursor cursor = db.getStateStatistics();

        Toast.makeText(this,
                "Rows: " + cursor.getCount(),
                Toast.LENGTH_LONG).show();

        while (cursor.moveToNext()) {
            String state = cursor.getString(0);
            int total = cursor.getInt(1);

            entries.add(new PieEntry(total, state));
        }

        cursor.close();

        PieDataSet dataSet = new PieDataSet(entries, "Buddies by State");

        dataSet.setColors(
                Color.RED,
                Color.BLUE,
                Color.GREEN,
                Color.YELLOW,
                Color.MAGENTA,
                Color.CYAN,
                Color.GRAY,
                Color.DKGRAY
        );
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }
}