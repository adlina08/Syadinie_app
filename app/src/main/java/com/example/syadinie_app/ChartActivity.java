package com.example.syadinie_app;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ChartActivity extends BaseActivity {

    private PieChart pieChartState, pieChartGender;
    private TabLayout tabLayout;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        Toast.makeText(this, "Chart Activity Opened", Toast.LENGTH_SHORT).show();

        pieChartState = findViewById(R.id.pieChartState);
        pieChartGender = findViewById(R.id.pieChartGender);
        tabLayout = findViewById(R.id.tabLayout);
        db = new DatabaseHelper(this);

        loadStateChart();
        loadGenderChart();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    pieChartState.setVisibility(View.VISIBLE);
                    pieChartGender.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    pieChartState.setVisibility(View.GONE);
                    pieChartGender.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadStateChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        Cursor cursor = db.getStateStatistics();

        while (cursor.moveToNext()) {
            String state = cursor.getString(0);
            int total = cursor.getInt(1);

            if (state != null && !state.equalsIgnoreCase("Select State")) {
                entries.add(new PieEntry(total, state));
            }
        }
        cursor.close();

        if (entries.isEmpty()) return;

        PieDataSet dataSet = new PieDataSet(entries, "");
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
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChartState.setData(data);
        pieChartState.getDescription().setEnabled(false);
        pieChartState.getLegend().setTextColor(Color.WHITE);
        pieChartState.setEntryLabelColor(Color.WHITE);
        pieChartState.setHoleColor(Color.TRANSPARENT);
        pieChartState.invalidate();
    }
    private void loadGenderChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        Cursor cursor = db.getGenderStatistics();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String gender = cursor.getString(0);
                int total = cursor.getInt(1);

                if (gender != null && !gender.equalsIgnoreCase("Select Gender")) {
                    entries.add(new PieEntry(total, gender));
                }
            }
            cursor.close();
        }

        if (entries.isEmpty()) return;

        PieDataSet dataSet = new PieDataSet(entries, "");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#42A5F5")); // Biru untuk Male
        colors.add(Color.parseColor("#EC407A")); // Pink untuk Female
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChartGender.setData(data);
        pieChartGender.getDescription().setEnabled(false);
        pieChartGender.getLegend().setTextColor(Color.WHITE);
        pieChartGender.setEntryLabelColor(Color.WHITE);
        pieChartGender.setHoleColor(Color.TRANSPARENT);
        pieChartGender.invalidate();
    }
}