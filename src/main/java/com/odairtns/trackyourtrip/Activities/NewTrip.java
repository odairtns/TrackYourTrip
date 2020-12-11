package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Currency;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripCurrency;
import com.odairtns.trackyourtrip.R;
import com.odairtns.trackyourtrip.Util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewTrip extends AppCompatActivity implements View.OnClickListener {
    private EditText mLabel, mCurrency, mBudget;
    private TextInputEditText mDescription;
    private Button mSave, mCancel;
    private DbHandler dbHandler;
    private Trip mTrip;
    private AlertDialog.Builder alertDialog;

    private List<Currency> currencyList;
    private JsonObjectRequest objectRequest;
    private RequestQueue requestQueue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        setTitle(R.string.new_trip);
        mLabel = findViewById(R.id.newtripLabel);
        mCurrency = findViewById(R.id.newtripCurrency);
        mBudget = findViewById(R.id.newtripBudget);
        mCurrency.setOnClickListener(this);
        mDescription = findViewById(R.id.newtripDescription);
        mSave = findViewById(R.id.newtripSaveB);
        mCancel = findViewById(R.id.newtripCancelB);
        mSave.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        dbHandler = new DbHandler(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newtripSaveB:
                int i = createNewTrip(v.getContext());
                if(i != 0) {
                    Intent intent = new Intent(NewTrip.this, ViewTripRecord.class);
                    intent.putExtra("TripID",i);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.newtripCancelB:
                startActivity(new Intent(NewTrip.this,MainActivity.class));
                finish();
                break;
            case R.id.newtripCurrency:
                alertDialog = new AlertDialog.Builder(v.getContext());
                List<String> currencyList = dbHandler.getCurrencyID();
                final CharSequence[] cs = currencyList.toArray(new CharSequence[currencyList.size()]);
                alertDialog.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrency.setText(cs[which].toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
                break;
        }

    }

    public int createNewTrip(Context ctx){
        if(!mLabel.getText().toString().isEmpty() && !mCurrency.getText().toString().isEmpty()){
            mTrip = new Trip();
            mTrip.setLabel(mLabel.getText().toString());
            mTrip.setStdCurrency(mCurrency.getText().toString());
            mTrip.setMultipleCurrency(true);
            mTrip.setDescription(mDescription.getText().toString());
            if(mBudget.getText().toString().isEmpty())
                mTrip.setBudget(0.0f);
            else
                mTrip.setBudget(Float.valueOf(mBudget.getText().toString()));
            int tripID = dbHandler.addTrip(mTrip);
            setExchangeRate(ctx,mCurrency.getText().toString(),tripID);
            return tripID;
        }
        else{
            String message;
            if(mLabel.getText().toString().isEmpty() && mCurrency.getText().toString().isEmpty())
                message = getResources().getString(R.string.please_insert_label_currency);
            else if(mLabel.getText().toString().isEmpty())
                    message = getResources().getString(R.string.please_insert_label);
            else
                message = getResources().getString(R.string.please_insert_currency);

            final AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
            mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            mDialog.setTitle(getResources().getString(R.string.update_infomration));
            mDialog.setMessage(message);
            mDialog.create().show();
            return 0;
        }
    }

    public void setExchangeRate(final Context ctx, String currency, final int tripID){
        currencyList = dbHandler.getCurrency();
        requestQueue = Volley.newRequestQueue(this);
        objectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL + currency, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject exchange =  response.getJSONObject("rates");
                    int i = 10;
                    for(Currency c : currencyList) {
                        TripCurrency exgTripCurrency2 = new TripCurrency();
                        if (!c.getCurrencyId().contentEquals(mCurrency.getText().toString())){
                            exgTripCurrency2.setIsStdStd(true);
                            exgTripCurrency2.setCurrency(c.getCurrencyId());
                            exgTripCurrency2.setTripID(tripID);
                            exgTripCurrency2.setExgRate(1.0f/Float.valueOf(exchange.getString(c.getCurrencyId())));
                            exgTripCurrency2.setUpdatedDate(response.get("date").toString());
                            exgTripCurrency2.setUpdatedTime(Float.valueOf(response.get("time_last_updated").toString()));
                            exgTripCurrency2.setDisplayOrder(i);
                            dbHandler.addTripCurrencies(exgTripCurrency2);
                            i = i + 10;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.getMessage();
                Toast.makeText(ctx, errorMessage,Toast.LENGTH_LONG);
            }
        });

        requestQueue.add(objectRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(NewTrip.this,MainActivity.class));
        finish();
    }
}
