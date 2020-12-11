package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.snackbar.Snackbar;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripRecord;
import com.odairtns.trackyourtrip.R;
import com.odairtns.trackyourtrip.Util.DayAxisValueFormatter;
import com.odairtns.trackyourtrip.Util.MyValueFormatter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripChart extends AppCompatActivity {

    private Context context;
    private PieChart mPieChart;
    private DbHandler dbHandler;
    private List<TripRecord> tripRecordList;
    private Trip mTrip;
    private int tripID;
    private TextView mChartObs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_chart);
        context = this;
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID",0);
        mTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getTripRecords(tripID);
        mChartObs = findViewById(R.id.tripchartInfo);
        mChartObs.setText(getResources().getString(R.string.chart_info)+" "+mTrip.getStdCurrency());

            mPieChart = findViewById(R.id.tripchartPiechart);
            setTitle(getResources().getString(R.string.chart));
        if(tripRecordList.size()>0) {
            //Setting up de graph
            mPieChart.setUsePercentValues(true);
            mPieChart.getDescription().setEnabled(false);
            mPieChart.setExtraOffsets(5, 10, 5, 5);

            mPieChart.setDragDecelerationFrictionCoef(0.95f);

            //mPieChart.setCenterTextTypeface(tfLight);
            // mPieChart.setCenterText(generateCenterSpannableText());

            mPieChart.setDrawHoleEnabled(false);
            mPieChart.setHoleColor(Color.WHITE);

            mPieChart.setTransparentCircleColor(Color.WHITE);
            mPieChart.setTransparentCircleAlpha(110);

            mPieChart.setHoleRadius(58f);
            mPieChart.setTransparentCircleRadius(61f);

            mPieChart.setDrawCenterText(true);

            mPieChart.setRotationAngle(0);
            // enable rotation of the chart by touch
            mPieChart.setRotationEnabled(true);
            mPieChart.setHighlightPerTapEnabled(true);

            //To implement on Click Listener
            // mPieChart.setOnChartValueSelectedListener(this);

            mPieChart.animateY(1400, Easing.EaseInOutQuad);

            Legend l = mPieChart.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            mPieChart.setEntryLabelColor(Color.DKGRAY);
            //mPieChart.setEntryLabelTypeface(tfRegular);
            mPieChart.setEntryLabelTextSize(12f);

            setPiChartData();

            mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);
                    Toast.makeText(context, getResources().getString(R.string.amount)+": "+(nf.format(e.getY())),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });
        }



    }


    private void setPiChartData() {

        tripRecordList = dbHandler.getExpenseTotal(tripID);
        ArrayList<PieEntry> values = new ArrayList<>();
        int i = 0;
        for(TripRecord c : tripRecordList){
            values.add(i,new PieEntry(c.getAmountStdCurrency().floatValue(),c.getExpType()));
            i = i + 1;
        }

        PieDataSet dataSet = new PieDataSet(values, getResources().getString(R.string.expense_results));

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);


        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(mPieChart));
        data.setValueTextSize(17f);
        data.setValueTextColor(Color.DKGRAY);
        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripChart.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }
}
