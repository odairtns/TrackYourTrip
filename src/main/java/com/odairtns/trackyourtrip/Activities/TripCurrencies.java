package com.odairtns.trackyourtrip.Activities;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odairtns.trackyourtrip.Adapters.TripCurrenciesAdapter;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Currency;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripCurrency;
import com.odairtns.trackyourtrip.R;
import com.odairtns.trackyourtrip.Util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripCurrencies extends AppCompatActivity {
    private AlertDialog.Builder alertDialog;
    private TextView mCurrency, mExgRate, mStdCurrency, dateUpdated, timeUpdated;
    private EditText mDisplayOrder;
    private Button saveButton, refreshButton;
    private Trip trip;
    private DbHandler dbHandler;
    private int tripID;
    private TripCurrency tripCurrency, stdTripCurrency;
    private RecyclerView mRecyclerView;
    private TripCurrenciesAdapter adapter;
    private List<TripCurrency> tripCurrencyList;
    private List<Currency> currencyList;
    private JsonObjectRequest objectRequest;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_currencies);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHandler = new DbHandler(this);
        tripID = getIntent().getIntExtra("TripID",0);
        trip = dbHandler.getTrip(tripID);
        mStdCurrency = findViewById(R.id.tripcurrencyStdCurrency);
        mStdCurrency.setText(trip.getStdCurrency());
        dateUpdated = findViewById(R.id.tripcurrencyDateUpdated);
        timeUpdated = findViewById(R.id.tripcurrencyTimeUpdated);


        stdTripCurrency = dbHandler.getTripCurrencyDetail(tripID);
        if(!(stdTripCurrency.getUpdatedDate() == null)) {
            dateUpdated.setText(stdTripCurrency.getUpdatedDate());
            DateFormat df = new SimpleDateFormat("hh:mm:ss");
            long time = stdTripCurrency.getUpdatedTime().longValue();
            timeUpdated.setText(df.format(new Date(time)));
        }


        tripCurrencyList = dbHandler.getTripCurrency(tripID);

        mRecyclerView = findViewById(R.id.tripcurrencyRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TripCurrenciesAdapter(tripCurrencyList, this);
        mRecyclerView.setAdapter(adapter);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                alertDialog = new AlertDialog.Builder(view.getContext());
                View newView = getLayoutInflater().inflate(R.layout.add_new_trip_currency,null);
                alertDialog.setView(newView);
                alertDialog.setCancelable(true);
                alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final Dialog newDialog = alertDialog.create();
                newDialog.show();
                mCurrency = newView.findViewById(R.id.newtripCCurrency);
               /*
                mCurrency.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder currencyDialog = new AlertDialog.Builder(v.getContext());
                        List<String> currencyList = dbHandler.getTripCurrencyID(tripID);
                        final CharSequence[] cs = currencyList.toArray(new CharSequence[currencyList.size()]);
                        currencyDialog.setItems(cs, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCurrency.setText(cs[which]);
                                dialog.dismiss();
                            }
                        });
                        currencyDialog.create().show();
                    }
                });

                */
                mExgRate = newView.findViewById(R.id.newtripCExgRate);
                mDisplayOrder = newView.findViewById(R.id.newtripCDisplayOrder);
                saveButton = newView.findViewById(R.id.newtripCSaveButton);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!mExgRate.getText().toString().isEmpty() &&
                                !mCurrency.getText().toString().isEmpty()){
                            tripCurrency = new TripCurrency();
                            tripCurrency.setExgRate(Float.valueOf(mExgRate.getText().toString()));
                            tripCurrency.setIsStdStd(Boolean.FALSE);
                            tripCurrency.setTripID(tripID);
                            tripCurrency.setCurrency(mCurrency.getText().toString());
                            tripCurrency.setDisplayOrder(Integer.valueOf(mDisplayOrder.getText().toString()));
                            try{
                                dbHandler.addTripCurrencies(tripCurrency);
                            }
                            catch (SQLiteConstraintException e) {
                                Toast.makeText(v.getContext(), getResources().getString(R.string.erro_insert_record), Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                                Toast.makeText(v.getContext(),getResources().getString(R.string.erro_insert_record),Toast.LENGTH_LONG).show();
                            }

                            tripCurrencyList.add(tripCurrency);
                            adapter.notifyDataSetChanged();
                            adapter.notifyItemRangeInserted(Integer.valueOf(mDisplayOrder.getText().toString()),adapter.getItemCount());
                            newDialog.dismiss();

                        }
                    }
                });

            }
        });


        refreshButton = findViewById(R.id.tripcurrencyRefreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExchangeRate(v.getContext(),trip.getStdCurrency());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TripCurrencies.this, ViewTripRecord.class);
        intent.putExtra("TripID", tripID);
        startActivity(intent);
        finish();
    }

    public void setExchangeRate(final Context ctx, String currency){
        currencyList = dbHandler.getCurrency();
        requestQueue = Volley.newRequestQueue(this);
        objectRequest = new JsonObjectRequest(Request.Method.GET, Constants.URL + currency, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    dbHandler.deleteAllTripCurrencies(tripID);
                    dateUpdated.setText(response.get("date").toString());
                    long time = Long.valueOf(response.get("time_last_updated").toString());
                    DateFormat df = new SimpleDateFormat("hh:mm:ss");

                    timeUpdated.setText(df.format(new Date(time)));
                    JSONObject exchange =  response.getJSONObject("rates");
                    int i = 10;
                    for(Currency c : currencyList) {
                            TripCurrency exgTripCurrency2 = new TripCurrency();
                            exgTripCurrency2.setCurrency(c.getCurrencyId());
                            exgTripCurrency2.setTripID(tripID);
                        if (!c.getCurrencyId().contentEquals(trip.getStdCurrency())) {
                            exgTripCurrency2.setIsStdStd(false);
                            exgTripCurrency2.setExgRate(1.0f/Float.valueOf(exchange.getString(c.getCurrencyId())));
                            exgTripCurrency2.setDisplayOrder(i);
                            i = i + 10;
                        }else{
                            exgTripCurrency2.setIsStdStd(true);
                            exgTripCurrency2.setExgRate(1.0f);
                            exgTripCurrency2.setDisplayOrder(0);
                        }
                            exgTripCurrency2.setUpdatedDate(response.get("date").toString());
                            exgTripCurrency2.setUpdatedTime(Float.valueOf(response.get("time_last_updated").toString()));
                            dbHandler.addTripCurrencies(exgTripCurrency2);

                    }
                    updateLayout();



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

    public void updateLayout() {
        tripCurrencyList.clear();
        List<TripCurrency> tripCurrencyList2 = dbHandler.getTripCurrency(tripID);
        int i = 0;
        for(TripCurrency t : tripCurrencyList2){
            tripCurrencyList.add(t);
            adapter.notifyItemChanged(i);
            i++;
        }

    }

}
