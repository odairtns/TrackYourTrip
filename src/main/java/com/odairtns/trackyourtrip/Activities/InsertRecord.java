package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripRecord;
import com.odairtns.trackyourtrip.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InsertRecord extends AppCompatActivity implements View.OnClickListener {
    private Button mSaveB, mCancelB, mSaveAdd;
    private EditText mDate, mExpType, mAmount, mCurrency, mPaymentMethod;
    private TextInputEditText mDetails;
    private DbHandler dbHandler;
    private TripRecord tripRecord;
    private int tripID;
    private Boolean getTripMultiCurrency;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_record);
        setTitle(getResources().getString(R.string.insert_record_text));
        mSaveB = findViewById(R.id.insertrecordSaveB);
        mSaveB.setOnClickListener(this);
        mCancelB = findViewById(R.id.insertrecordCancelB);
        mCancelB.setOnClickListener(this);
        mSaveAdd = findViewById(R.id.insertrecordSaveAddB);
        mSaveAdd.setOnClickListener(this);
        mDate = findViewById(R.id.insertrecordDate);
        mDate.setOnClickListener(this);
        mExpType = findViewById(R.id.insertrecordExpType);
        mExpType.setOnClickListener(this);
        mAmount = findViewById(R.id.insertrecordAmount);
        mDetails = findViewById(R.id.insertrecordDetails);
        mCurrency = findViewById(R.id.insertrecordCurrency);
        mCurrency.setOnClickListener(this);
        mPaymentMethod = findViewById(R.id.insertrecordPaymentMethod);
        mPaymentMethod.setOnClickListener(this);
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID",0);
        trip = new Trip();
        trip = dbHandler.getTrip(tripID);
        mCurrency.setText(trip.getStdCurrency());
        getTripMultiCurrency = trip.getMultipleCurrency();

    }

    @Override
    public void onClick(View v) {
        int i;
        Intent intent = new Intent(v.getContext(),ViewTripRecord.class);
        intent.putExtra("TripID",tripID);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
        final CharSequence[] cs;
        switch (v.getId()){
            case R.id.insertrecordCancelB:
                startActivity(intent);
                finish();
                break;
            case R.id.insertrecordSaveB:
                i = InsertTripRecord(v.getContext());
                if(i == 1){
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.insertrecordSaveAddB:
                i = InsertTripRecord(v.getContext());
                if(i == 1)
                    ClearFields();
                break;
            case R.id.insertrecordCurrency:
                //AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                List<String> currencyList = dbHandler.getTripCurrencyID(tripID);
                //final CharSequence[] cs =  currencyList.toArray(new CharSequence[currencyList.size()]);
                cs =  currencyList.toArray(new CharSequence[currencyList.size()]);
                alertDialog.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrency.setText(cs[which].toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
                break;
            case R.id.insertrecordPaymentMethod:
                List<String> paymentMethodlist = dbHandler.getPaymentmethod();
                cs =  paymentMethodlist.toArray(new CharSequence[paymentMethodlist.size()]);
                alertDialog.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPaymentMethod.setText(cs[which].toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
                break;
            case R.id.insertrecordDate:
                int year, month, day;
                final Calendar calendar = Calendar.getInstance();
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy",
                        Locale.getDefault());

                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH) ;
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.YEAR,year);
                        mDate.setText(simpleDateFormat.format(calendar.getTime()));

                    }
                },year, month, day);
                datePickerDialog.show();
                break;
            case  R.id.insertrecordExpType:
                AlertDialog.Builder expAlertBuilder  = new AlertDialog.Builder(v.getContext());
                List<String> expenseTypeList = dbHandler.getExpTypeID();
                final CharSequence[] cs2 = expenseTypeList.toArray(new CharSequence[expenseTypeList.size()]);
                expAlertBuilder.setItems(cs2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mExpType.setText(cs2[which]);
                        dialog.dismiss();
                    }
                });
                expAlertBuilder.create().show();
                break;
        }
    }

    public int InsertTripRecord(Context ctx){
        if(!mDate.getText().toString().isEmpty() && !mExpType.getText().toString().isEmpty() &&
                !mAmount.getText().toString().isEmpty() && !mCurrency.getText().toString().isEmpty()) {
            tripRecord = new TripRecord();
            tripRecord.setDate(mDate.getText().toString());
            tripRecord.setExpType(mExpType.getText().toString());
            tripRecord.setCurrency(mCurrency.getText().toString());
            tripRecord.setAmount(Double.valueOf(mAmount.getText().toString()));
            tripRecord.setDetails(mDetails.getText().toString());
            tripRecord.setPaymentMethod(mPaymentMethod.getText().toString());

            if (getTripMultiCurrency == Boolean.TRUE) {
                Float exgRate = dbHandler.getExgRate(tripID, mCurrency.getText().toString());
                if (exgRate == 0.0f) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
                    alertDialog.setTitle(getResources().getString(R.string.currency));
                    alertDialog.setMessage(getResources().getString(R.string.wrong_currency));
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.create().show();
                    return 0;
                } else {
                    dbHandler.addTripRecord(tripRecord, tripID);
                    return 1;
                }
            } else {
                if (!mCurrency.getText().toString().contentEquals(trip.getStdCurrency())) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
                    alertDialog.setTitle(getResources().getString(R.string.trip_currency));
                    alertDialog.setMessage(getResources().getString(R.string.not_std_currency) +
                            System.lineSeparator() + getResources().getString(R.string.correct_before_continue));
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.create().show();
                    return 0;
                } else {
                    dbHandler.addTripRecord(tripRecord, tripID);
                    return 1;
                }
            }
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle(getResources().getString(R.string.error));
            alertDialog.setMessage(getResources().getString(R.string.mandatory_fields));
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            alertDialog.create().show();
            return 0;
        }

    }

    public void ClearFields(){
        mDate.setText("");
        mExpType.setText("");
        mAmount.setText("");
        mDetails.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(InsertRecord.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }

}
