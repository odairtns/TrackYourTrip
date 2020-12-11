package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.odairtns.trackyourtrip.Adapters.ViewTripRecordAdapter;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripRecord;
import com.odairtns.trackyourtrip.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ViewTripRecord extends AppCompatActivity implements View.OnClickListener {
    private TextView mTrip, mDescription, mAmount, mStdCurrency, mNoRecordAdded, mBudget, mBalance,
            mBudgetText, mBalanceText, mTripSummary;
    private ViewTripRecordAdapter adapter;
    private  RecyclerView recyclerView;
    private DbHandler dbHandler;
    private Trip selectedTrip;
    private int tripID;
    private List<TripRecord> tripRecordList;
    private FloatingActionButton fab;
    private Button  viewChart, editExgRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_trip_record);
        setTitle(getResources().getString(R.string.view_expenses));

        dbHandler = new DbHandler(this);
        selectedTrip = new Trip();
        tripID = getIntent().getIntExtra("TripID",0);
        selectedTrip = dbHandler.getTrip(tripID);
        tripRecordList = dbHandler.getTripRecords(tripID);


        mTrip = findViewById(R.id.viewrecordTripLabel);
        mTrip.setText(selectedTrip.getLabel());
        mTripSummary = findViewById(R.id.viewrecordSummaryText);
        mTripSummary.setText(getResources().getString(R.string.trip_summary) + " " +selectedTrip.getLabel());
        mTrip.setVisibility(View.GONE);

        mDescription = findViewById(R.id.viewrecordTripDescription);
        mAmount = findViewById(R.id.viewrecordTotalAmount);
        mAmount.setText(getResources().getString(R.string.no_expense_added));

        mDescription.setText(selectedTrip.getDescription());
        mStdCurrency = findViewById(R.id.viewrecordStdCurrency);
        mStdCurrency.setText(selectedTrip.getStdCurrency());
        mNoRecordAdded = findViewById(R.id.viewrecordNoRecordText);
        mBudget  = findViewById(R.id.viewrecordTripBudget);

        mBalance  = findViewById(R.id.viewrecordBalance);
        mBudgetText  = findViewById(R.id.viewrecordTripBudgetText);
        mBalanceText  = findViewById(R.id.viewrecordBalanceText);

        viewChart = findViewById(R.id.viewrecordViewChart);
        viewChart.setOnClickListener(this);

        editExgRate = findViewById(R.id.viewrecordExchanceRate);
        editExgRate.setOnClickListener(this);

        recyclerView = findViewById(R.id.viewrecordRecyclerView);

        setLayout();

    }
    private void setLayout(){

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        if(tripRecordList.size()>0) {
            if(selectedTrip.getMultipleCurrency() == Boolean.TRUE){
                tripRecordList = getStandardAmountList(tripRecordList);
                Collections.reverse(tripRecordList);
                mAmount.setText(String.valueOf(nf.format(getAmountStd(tripRecordList))));
            }else{
                mAmount.setText(String.valueOf(nf.format(getAmountStd(tripRecordList))));
            }
            displayBudget();
            recyclerView.setVisibility(View.VISIBLE);
            mNoRecordAdded.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ViewTripRecordAdapter(tripRecordList, this);
            recyclerView.setAdapter(adapter);

            adapter.setRecordChangeListener(new ViewTripRecordAdapter.myRecordChangeListener() {
                @Override
                public void onRecordDeleted() {
                    tripRecordList = dbHandler.getTripRecords(tripID);
                    setNewLayout();
                }

                @Override
                public void onRecordChanged() {
                    tripRecordList = dbHandler.getTripRecords(tripID);
                    setNewLayout();
                }
            });


        }else{
            displayBudget();
            recyclerView.setVisibility(View.GONE);
            mNoRecordAdded.setVisibility(View.VISIBLE);
            String text = getResources().getString(R.string.no_record_added);
            mNoRecordAdded.setText(text);
        }

        fab = findViewById(R.id.viewrecordFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),InsertRecord.class);
                intent.putExtra("TripID",selectedTrip.getID());
                v.getContext().startActivity(intent);
                finish();
            }
        });

    }

private void setNewLayout(){

    NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    if(tripRecordList.size()>0) {
        if(selectedTrip.getMultipleCurrency() == Boolean.TRUE){
            tripRecordList = getStandardAmountList(tripRecordList);
            Collections.reverse(tripRecordList);
            mAmount.setText(String.valueOf(nf.format(getAmountStd(tripRecordList))));
        }else{
            mAmount.setText(String.valueOf(nf.format(getAmountStd(tripRecordList))));
        }
        displayBudget();
        recyclerView.setVisibility(View.VISIBLE);
        mNoRecordAdded.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }else{
        displayBudget();
        recyclerView.setVisibility(View.GONE);
        mNoRecordAdded.setVisibility(View.VISIBLE);
        String text = getResources().getString(R.string.no_record_added);
        mNoRecordAdded.setText(text);
    }

    fab = findViewById(R.id.viewrecordFAB);
    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(),InsertRecord.class);
            intent.putExtra("TripID",selectedTrip.getID());
            v.getContext().startActivity(intent);
            finish();
        }
    });

}
/*
    private double getAmount(List<TripRecord> recordList){
        double totalAmount = 0;
        float exgRate;
        for(TripRecord listEntry : recordList){
            exgRate = dbHandler.getExgRate(tripID, listEntry.getCurrency());
            totalAmount = totalAmount + (exgRate * listEntry.getAmount());
        }
        return totalAmount;
    }

 */

    public List<TripRecord> getStandardAmountList (List<TripRecord> recordList){
        List<TripRecord> newTripRecordList = new ArrayList<>();
        TripRecord currentTrip;
        for( TripRecord c : recordList){
            currentTrip = new TripRecord();
            currentTrip.setDetails(c.getDetails());
            currentTrip.setCurrency(c.getCurrency());
            currentTrip.setExpType(c.getExpType());
            currentTrip.setDate(c.getDate());
            currentTrip.setAmount(c.getAmount());
            currentTrip.setId(c.getId());
            currentTrip.setTripID(c.getTripID());
            currentTrip.setPaymentMethod(c.getPaymentMethod());
            currentTrip.setAmountStdCurrency(dbHandler.getExgRate(c.getTripID(),c.getCurrency()) * c.getAmount());
            newTripRecordList.add(currentTrip);
        }

        return newTripRecordList;
    }

    public double getAmountStd (List<TripRecord> recordList){
        double totalAmount = 0;
        for(TripRecord listEntry : recordList){
            totalAmount = totalAmount + listEntry.getAmountStdCurrency();
        }
        return totalAmount;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     /*   if(item.getItemId() == R.id.mainmenuAddCurrency){
            Intent intent = new Intent(this,TripCurrencies.class);
            intent.putExtra("TripID",tripID);
            startActivity(intent);
            finish();
        }

      */
        if(item.getItemId() == R.id.share){
            shareTrip();
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayBudget(){
        if(selectedTrip.getBudget().equals(0.0f)){
            mBudget.setVisibility(View.GONE);
            mBalance.setVisibility(View.GONE);
            mBudgetText.setVisibility(View.GONE);
            mBalanceText.setVisibility(View.GONE);
        }else {
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            mBudget.setText(String.valueOf(nf.format(selectedTrip.getBudget())));

            double balance = selectedTrip.getBudget().doubleValue() -
                    getAmountStd(tripRecordList);
            double percent = getAmountStd(tripRecordList) / selectedTrip.getBudget().doubleValue();
            if(getAmountStd(tripRecordList)>0)
            if(percent <= 0.5)
                mBalance.setTextColor(getResources().getColor(R.color.colorStdBlue,getTheme()));
            else if(percent <= 0.75)
                mBalance.setTextColor(getResources().getColor(R.color.colorYellow,getTheme()));
            else
                mBalance.setTextColor(getResources().getColor(R.color.colorRed,getTheme()));
            mBalance.setText(String.valueOf(nf.format(balance)));

        }
    }

    public void shareTrip(){
        Trip shareTrip = selectedTrip;
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        double stdAmount, balance;
        stdAmount = getAmountStd(tripRecordList);
        balance = selectedTrip.getBudget().doubleValue() -
                stdAmount;
        StringBuilder emailDetails = new StringBuilder();

        emailDetails.append(getResources().getString(R.string.std_currency_)+": "+shareTrip.getStdCurrency()+"\n");
        emailDetails.append(getResources().getString(R.string.budget)+": "+nf.format(shareTrip.getBudget())+"\n");
        emailDetails.append(getResources().getString(R.string.balance)+": "+nf.format(balance)+"\n");
        emailDetails.append(getResources().getString(R.string.total_amount)+" "+shareTrip.getStdCurrency()+": "+nf.format(stdAmount)+"\n");
        emailDetails.append(getResources().getString(R.string.description)+": "+shareTrip.getDescription()+"\n");
        emailDetails.append("\n\n");


        for(TripRecord c : tripRecordList){
            emailDetails.append(getResources().getString(R.string.date)+": "+c.getDate()+"\n");
            emailDetails.append(getResources().getString(R.string.type)+": "+c.getExpType()+"\n");
            emailDetails.append(getResources().getString(R.string.payment)+": "+c.getPaymentMethod()+"\n");
            emailDetails.append(getResources().getString(R.string.amount)+": "+nf.format(c.getAmount())+" "+c.getCurrency()+"\n");
            emailDetails.append(getResources().getString(R.string.amount_std_currency)+": "+nf.format(c.getAmountStdCurrency())+"\n");
            emailDetails.append(getResources().getString(R.string.details)+": "+c.getDetails()+"\n");
            emailDetails.append("\n");
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.trip_share_subject)+
                ": "+shareTrip.getLabel());
        intent.putExtra(Intent.EXTRA_TEXT, emailDetails.toString());

      try{
            startActivity(Intent.createChooser(intent,getResources().getString(R.string.send_email)));
        }catch(ActivityNotFoundException e){
            Toast.makeText(ViewTripRecord.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewTripRecord.this,SearchTrip.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
    /*        case R.id.viewrecordEditButton:
                final AlertDialog.Builder updateTrip = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.activity_new_trip, null);
                updateTrip.setView(dialogView);
                updateTrip.setTitle(getResources().getString(R.string.update_trip));

                final EditText mLabel, mCurrency, mBudget;
                final TextInputEditText mDescription;
                Button mSave, mCancel;

                mLabel = dialogView.findViewById(R.id.newtripLabel);
                mCurrency = dialogView.findViewById(R.id.newtripCurrency);
                mBudget = dialogView.findViewById(R.id.newtripBudget);

                mDescription = dialogView.findViewById(R.id.newtripDescription);
                mSave = dialogView.findViewById(R.id.newtripSaveB);
                mSave.setVisibility(View.GONE);
                mCancel = dialogView.findViewById(R.id.newtripCancelB);
                mCancel.setVisibility(View.GONE);


                mLabel.setText(selectedTrip.getLabel());
                mCurrency.setText(selectedTrip.getStdCurrency());
                mBudget.setText(selectedTrip.getBudget().toString());
                mDescription.setText(selectedTrip.getDescription());
                final Context context = this;

                updateTrip.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!mCurrency.getText().toString().isEmpty()){
                            Trip mTrip = new Trip();
                            mTrip.setID(selectedTrip.getID());
                            mTrip.setLabel(mLabel.getText().toString());
                            mTrip.setStdCurrency(mCurrency.getText().toString());
                            mTrip.setMultipleCurrency(true);
                            mTrip.setDescription(mDescription.getText().toString());
                            if(mBudget.getText().toString().isEmpty())
                                mTrip.setBudget(0.0f);
                            else
                                mTrip.setBudget(Float.valueOf(mBudget.getText().toString()));
                            dbHandler.updateTrip(mTrip);
                            updateHeader();
                        }
                        else{
                            String message;
                            if(mCurrency.getText().toString().isEmpty())
                                message = getResources().getString(R.string.please_insert_currency);
                            else
                                message = getResources().getString(R.string.please_insert_label_currency);
                            final android.app.AlertDialog.Builder mDialog = new android.app.AlertDialog.Builder(context);
                            mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            mDialog.setTitle(getResources().getString(R.string.update_infomration));
                            mDialog.setMessage(message);
                            mDialog.create().show();
                        }
                        dialog.dismiss();
                    }
                });

                updateTrip.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        updateTrip.create().show();
                break;

     */
            case R.id.viewrecordExchanceRate:
                intent = new Intent(this,TripCurrencies.class);
                intent.putExtra("TripID",tripID);
                startActivity(intent);
                finish();
                break;

            case R.id.viewrecordViewChart:
                intent = new Intent(v.getContext(), TripChart.class);
                intent.putExtra("TripID",tripID);
                v.getContext().startActivity(intent);
                finish();
                break;
        }

    }

    /*
    public void updateHeader(){
        selectedTrip = dbHandler.getTrip(tripID);
        mTrip.setText(selectedTrip.getLabel());
        mDescription.setText(selectedTrip.getDescription());
        mStdCurrency.setText(selectedTrip.getStdCurrency());
        displayBudget();
    }

     */

}
