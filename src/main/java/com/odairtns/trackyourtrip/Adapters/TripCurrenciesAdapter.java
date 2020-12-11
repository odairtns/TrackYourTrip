package com.odairtns.trackyourtrip.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.odairtns.trackyourtrip.Activities.TripCurrencies;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripCurrency;
import com.odairtns.trackyourtrip.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TripCurrenciesAdapter extends RecyclerView.Adapter<TripCurrenciesAdapter.TripCurrenciesViewHolder> {
    private TripCurrency tripCurrency;
    private List<TripCurrency> tripCurrencyList;
    private DbHandler dbHandler;
    private Context context;

    public TripCurrenciesAdapter(List<TripCurrency> tripCurrenciesList, Context context) {
        this.tripCurrencyList = tripCurrenciesList;
        this.context = context;
    }

    @NonNull
    @Override
    public TripCurrenciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_currencies_list, parent,false);
                return new TripCurrenciesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripCurrenciesViewHolder holder, int position) {
        tripCurrency = tripCurrencyList.get(position);
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        holder.adpCurrency.setText(tripCurrency.getCurrency());
        holder.adpgRate.setText(nf.format(tripCurrency.getExgRate()));
        holder.mdisplayOrder.setText(String.valueOf(tripCurrency.getDisplayOrder()));

    }

    @Override
    public int getItemCount() {
        return tripCurrencyList.size();
    }

    public class TripCurrenciesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView adpCurrency, adpgRate, mdisplayOrder;
        private Button editButton;

        public TripCurrenciesViewHolder(@NonNull View itemView) {
            super(itemView);
            adpCurrency = itemView.findViewById(R.id.viewtripcurrencyCurrency);
            adpgRate = itemView.findViewById(R.id.viewtripcurrencyExgRate);
            mdisplayOrder = itemView.findViewById(R.id.viewtripcurrencyDisplayOrder);
            editButton = itemView.findViewById(R.id.viewtripcurrencyEditButton);
            editButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            tripCurrency = tripCurrencyList.get(position);
            switch (v.getId()){
                case R.id.viewtripcurrencyEditButton:
                    updateCurrency(tripCurrency, position);
                    break;
            }

        }

        private void updateCurrency(final TripCurrency tripCurrency, final int position) {
            dbHandler = new DbHandler(context);
            Trip trip = dbHandler.getTrip(tripCurrency.getTripID());
            final Resources resources = context.getResources();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            if (trip.getStdCurrency().contentEquals(tripCurrency.getCurrency())) {
                alertDialog.setTitle(resources.getString(R.string.error));
                alertDialog.setIcon(R.drawable.icons_cancel);
                alertDialog.setMessage(resources.getString(R.string.std_currency_update_error));
                alertDialog.setCancelable(true);
                alertDialog.create().show();

            } else {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.update_trip_currency, null);

                alertDialog.setView(view);
                TextView stdCurrency, currency;
                final EditText exgRate, displayOrder;
                stdCurrency = view.findViewById(R.id.updatetripcurrencyStdCurrency);
                currency = view.findViewById(R.id.updatetripcurrencyCurrency);
                exgRate = view.findViewById(R.id.updatetripcurrencyExgRate);
                displayOrder = view.findViewById(R.id.updatetripcurrencyDisplayOrder);
                stdCurrency.setText(trip.getStdCurrency());
                currency.setText(tripCurrency.getCurrency());
                displayOrder.setText(String.valueOf(tripCurrency.getDisplayOrder()));
                exgRate.setText(String.valueOf(tripCurrency.getExgRate()));

                alertDialog.setPositiveButton(resources.getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (exgRate.getText().toString().isEmpty()){
                            Toast.makeText(context,resources.getString(R.string.non_empty),Toast.LENGTH_LONG).show();
                        }else{
                            if(Float.valueOf(exgRate.getText().toString())<0)
                                Toast.makeText(context,resources.getString(R.string.non_zero),Toast.LENGTH_LONG).show();
                            else{
                        tripCurrency.setExgRate(Float.valueOf(exgRate.getText().toString()));
                        tripCurrency.setDisplayOrder(Integer.valueOf(displayOrder.getText().toString()));
                        dbHandler.updateTripCurrency(tripCurrency);
                        notifyItemChanged(position);

                            }
                        }
                    }
                });
                alertDialog.setNegativeButton(resources.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();

            }
        }
    }
}
