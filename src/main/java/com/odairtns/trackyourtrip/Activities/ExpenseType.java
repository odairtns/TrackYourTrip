package com.odairtns.trackyourtrip.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.odairtns.trackyourtrip.Adapters.ExpenseTypeAdapter;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.ExpenseTypeModel;
import com.odairtns.trackyourtrip.R;

import java.util.List;
import java.util.zip.Inflater;

public class ExpenseType extends AppCompatActivity {

    private DbHandler dbHandler;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<ExpenseTypeModel> expenseTypeModelList;
    private FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_type);
        setTitle(R.string.exp_type);
        recyclerView = findViewById(R.id.exptypeRecyclerView);
        dbHandler = new DbHandler(this);
        FAB = findViewById(R.id.exptypeFAB);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                View view = getLayoutInflater().inflate(R.layout.add_new_exp_type, null);
                alertDialog.setView(view);
                final EditText expType = view.findViewById(R.id.addexptypeExpType);
                alertDialog.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ExpenseTypeModel model = new ExpenseTypeModel();
                        model.setExpType(expType.getText().toString());
                        model.setDescription(expType.getText().toString());
                        dbHandler.addExpenseType(model);
                        expenseTypeModelList.add(model);
                        adapter.notifyItemInserted(expenseTypeModelList.size());
                    }
                });
                alertDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
            }
        });


        dbHandler = new DbHandler(this);
        expenseTypeModelList = dbHandler.getExpType();

        if(expenseTypeModelList.size()>0){
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ExpenseTypeAdapter(expenseTypeModelList, this);
            recyclerView.setAdapter(adapter);

        }

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                expenseTypeModelList = dbHandler.getExpType();

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ExpenseType.this,MainActivity.class));
        finish();
    }
}
