package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.odairtns.trackyourtrip.Adapters.AllTripAdapter;
import com.odairtns.trackyourtrip.Adapters.ViewTripRecordAdapter;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchTrip extends AppCompatActivity {
private DbHandler dbHandler;
private RecyclerView recyclerView;
private AllTripAdapter adapter;
private List<Trip> tripList;
private TextView mNoRecordAdded;
private FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        dbHandler = new DbHandler(this);
        tripList = dbHandler.getTrips();
        recyclerView = findViewById(R.id.searchtripRecyclerView);
        mNoRecordAdded = findViewById(R.id.searchtripNoRecordText);
        FAB = findViewById(R.id.searchtripFAB);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchTrip.this,NewTrip.class));
                finish();
            }
        });

        displayContent();
        if(tripList.size()>0) {
            adapter.setTripListener(new AllTripAdapter.onTripListener() {
                @Override
                public void onTripDeleted() {
                    if(dbHandler.getTrips().size() == 0 ){
                    recyclerView.setVisibility(View.GONE);
                    mNoRecordAdded.setVisibility(View.VISIBLE);
                    String text = getResources().getString(R.string.no_record_added);
                    mNoRecordAdded.setText(text);
                    }
                }

                @Override
                public void onActivityChanged() {
                    finish();
                }
            });

        }

    }

    public void displayContent(){
        if(tripList.size()>0) {
            mNoRecordAdded.setVisibility(View.GONE);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            tripList = dbHandler.getTrips();
            Collections.reverse(tripList);
            adapter = new AllTripAdapter(tripList, this);
            recyclerView.setAdapter(adapter);

        }else{
            recyclerView.setVisibility(View.GONE);
            mNoRecordAdded.setVisibility(View.VISIBLE);
            String text = getResources().getString(R.string.no_record_added);
            mNoRecordAdded.setText(text);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SearchTrip.this,MainActivity.class));
        finish();
    }


}
