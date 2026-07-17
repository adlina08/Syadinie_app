package com.example.syadinie_app;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
                } else {
                    pieChartState.setVisibility(View.GONE);
                    pieChartGender.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
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

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.parseColor("#5DADE2")); // Sky Blue
        colors.add(Color.parseColor("#58D68D")); // Emerald Green
        colors.add(Color.parseColor("#AF7AC5")); // Purple
        colors.add(Color.parseColor("#F5B041")); // Gold
        colors.add(Color.parseColor("#EC7063")); // Coral Red
        colors.add(Color.parseColor("#48C9B0")); // Turquoise
        colors.add(Color.parseColor("#5499C7")); // Ocean Blue
        colors.add(Color.parseColor("#F1948A")); // Soft Pink
        colors.add(Color.parseColor("#BB8FCE")); // Lavender
        colors.add(Color.parseColor("#52BE80")); // Green
        colors.add(Color.parseColor("#F7DC6F")); // Yellow
        colors.add(Color.parseColor("#45B39D")); // Teal
        colors.add(Color.parseColor("#DC7633")); // Orange
        colors.add(Color.parseColor("#7FB3D5")); // Light Blue

        dataSet.setColors(colors);

        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChartState.setData(data);
        pieChartState.getDescription().setEnabled(false);

        pieChartState.setUsePercentValues(false);
        pieChartState.setDrawEntryLabels(true);
        pieChartState.setEntryLabelColor(Color.WHITE);
        pieChartState.setEntryLabelTextSize(12f);

        pieChartState.setCenterText("States");
        pieChartState.setCenterTextColor(Color.WHITE);
        pieChartState.setCenterTextSize(18f);

        pieChartState.setHoleRadius(50f);
        pieChartState.setTransparentCircleRadius(55f);
        pieChartState.setHoleColor(Color.TRANSPARENT);

        pieChartState.getLegend().setTextColor(Color.WHITE);
        pieChartState.getLegend().setTextSize(13f);

        pieChartState.animateY(1200);
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
        colors.add(Color.parseColor("#6EC6FF")); // Male
        colors.add(Color.parseColor("#FF9EC7")); // Female

        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(8f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChartGender.setData(data);
        pieChartGender.getDescription().setEnabled(false);

        pieChartGender.setCenterText("Gender");
        pieChartGender.setCenterTextColor(Color.WHITE);
        pieChartGender.setCenterTextSize(18f);

        pieChartGender.setHoleRadius(50f);
        pieChartGender.setTransparentCircleRadius(55f);
        pieChartGender.setHoleColor(Color.TRANSPARENT);

        pieChartGender.setEntryLabelColor(Color.WHITE);
        pieChartGender.setEntryLabelTextSize(12f);

        pieChartGender.getLegend().setTextColor(Color.WHITE);
        pieChartGender.getLegend().setTextSize(13f);

        pieChartGender.animateY(1200);
        pieChartGender.invalidate();
    }
}