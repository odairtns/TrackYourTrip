package com.odairtns.trackyourtrip.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.odairtns.trackyourtrip.Activities.ViewTripRecord;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripRecord;
import com.odairtns.trackyourtrip.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AllTripAdapter extends RecyclerView.Adapter<AllTripAdapter.AlltripViewHolder> {
    private List<Trip> tripList;
    private Context context;
    private DbHandler dbHandler;
    private onTripListener tripListener;
    private Resources r;

    public interface onTripListener {
        void onTripDeleted();
        void onActivityChanged();
    }

    public void setTripListener(onTripListener tripListener) {
        this.tripListener = tripListener;
    }

    public AllTripAdapter(List<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
        this.tripListener = null;
        dbHandler = new DbHandler(context);
        r = context.getResources();
    }

    @NonNull
    @Override
    public AlltripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.list_all_trips, parent, false);
        AlltripViewHolder alltripViewHolder = new AlltripViewHolder(view);
        return alltripViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final AlltripViewHolder holder, final int position) {
        final Trip trip = tripList.get(position);

        holder.label.setText(trip.getLabel());
        holder.description.setText(trip.getDescription());

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        double getAmountStd = getAmountStd(trip.getID());

        holder.mAmount.setText(String.valueOf(nf.format(getAmountStd)));
        holder.mStdCurrency.setText(trip.getStdCurrency());

        if (trip.getBudget().equals(0.0f)) {
            holder.mBudget.setVisibility(View.GONE);
            holder.mBalance.setVisibility(View.GONE);
            holder.mBudgetText.setVisibility(View.GONE);
            holder.mBalanceText.setVisibility(View.GONE);
        } else {

            holder.mBudget.setText(String.valueOf(nf.format(trip.getBudget())));

            double balance = trip.getBudget().doubleValue() -
                    getAmountStd;
            double percent = getAmountStd / trip.getBudget().doubleValue();
            if (getAmountStd > 0)
                if (percent <= 0.5)
                    holder.mBalance.setTextColor(r.getColor(R.color.colorStdBlue, context.getTheme()));
                else if (percent <= 0.75)
                    holder.mBalance.setTextColor(r.getColor(R.color.colorYellow, context.getTheme()));
                else
                    holder.mBalance.setTextColor(r.getColor(R.color.colorRed, context.getTheme()));
            holder.mBalance.setText(String.valueOf(nf.format(balance)));

        }


    }

    public double getAmountStd(int tripRecord) {
        double totalAmount = 0;
        List<TripRecord> currentTrip = dbHandler.getTripRecords(tripRecord);
        if (currentTrip.size() == 0)
            return totalAmount;
        else {
            for (TripRecord listEntry : currentTrip) {

                totalAmount = totalAmount + dbHandler.getExgRate(tripRecord, listEntry.getCurrency()) * listEntry.getAmount();
            }
            return totalAmount;
        }
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class AlltripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView label, mAmount, mStdCurrency, mBudget, mBalance, mBudgetText, mBalanceText;
        private MultiAutoCompleteTextView description;
        private Button detailsButton, deleteButton, editButton;

        public AlltripViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.listripsLabel);
            description = itemView.findViewById(R.id.listripsDescription);
            detailsButton = itemView.findViewById(R.id.listripsDetailsButton);
            detailsButton.setOnClickListener(this);
            deleteButton = itemView.findViewById(R.id.listripsDeleteButton);
            deleteButton.setOnClickListener(this);
            editButton = itemView.findViewById(R.id.listripsEditButton);
            editButton.setOnClickListener(this);
            mAmount = itemView.findViewById(R.id.listripsTotalAmount);
            mStdCurrency = itemView.findViewById(R.id.listripsStdCurrency);
            mBudget = itemView.findViewById(R.id.listripsTripBudget);
            mBudgetText = itemView.findViewById(R.id.listripsTripBudgetText);
            mBalance = itemView.findViewById(R.id.listripsBalance);
            mBalanceText = itemView.findViewById(R.id.listripsBalanceText);


        }


        public void deleteTrip(final Trip tripToDelete, final int position) {
            Resources resources = context.getResources();
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(resources.getString(R.string.delete_warning_message));
            dialog.setTitle(resources.getString(R.string.warning));
            dialog.setCancelable(true);
            dialog.setIcon(android.R.drawable.ic_dialog_alert);
            dialog.setNegativeButton(resources.getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton(resources.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbHandler.deleteTrip(tripToDelete.getID());
                    notifyItemRemoved(position);
                    notifyItemRangeRemoved(position, tripList.size());
                    tripList = dbHandler.getTrips();
                    tripListener.onTripDeleted();
                }
            });
            dialog.create().show();
        }


        public void editTrip(final Trip selectedTrip, final int position) {
            final AlertDialog.Builder updateTrip = new AlertDialog.Builder(context);

            final View dialogView = LayoutInflater.from(context).inflate(R.layout.activity_new_trip, null);
            updateTrip.setView(dialogView);
            //updateTrip.setTitle(r.getString(R.string.update_trip));

            final EditText mLabel, mCurrency, mBudget;
            final TextInputEditText mDescription;
            LinearLayout mButtonLayout;
            Button mSave, mCancel;
            mLabel = dialogView.findViewById(R.id.newtripLabel);
            mCurrency = dialogView.findViewById(R.id.newtripCurrency);
            mBudget = dialogView.findViewById(R.id.newtripBudget);
            mDescription = dialogView.findViewById(R.id.newtripDescription);
            mSave = dialogView.findViewById(R.id.newtripSaveB);
            mSave.setVisibility(View.GONE);
            mCancel = dialogView.findViewById(R.id.newtripCancelB);
            mCancel.setVisibility(View.GONE);
            mButtonLayout = dialogView.findViewById(R.id.newtripButtonLayout);
            mButtonLayout.setVisibility(View.GONE);
            mLabel.setText(selectedTrip.getLabel());
            mCurrency.setText(selectedTrip.getStdCurrency());
            mBudget.setText(selectedTrip.getBudget().toString());
            mDescription.setText(selectedTrip.getDescription());

            updateTrip.setPositiveButton(r.getString(R.string.update), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!mCurrency.getText().toString().isEmpty()) {
                        Trip mTrip = new Trip();
                        mTrip.setID(selectedTrip.getID());
                        mTrip.setLabel(mLabel.getText().toString());
                        mTrip.setStdCurrency(mCurrency.getText().toString());
                        mTrip.setMultipleCurrency(true);
                        mTrip.setDescription(mDescription.getText().toString());
                        if (mBudget.getText().toString().isEmpty())
                            mTrip.setBudget(0.0f);
                        else
                            mTrip.setBudget(Float.valueOf(mBudget.getText().toString()));
                        dbHandler.updateTrip(mTrip);
                        tripList.set(position, mTrip);
                        notifyItemChanged(position);
                        notifyItemRangeChanged(position, tripList.size());

                    } else {
                        String message;
                        if (mCurrency.getText().toString().isEmpty())
                            message = r.getString(R.string.please_insert_currency);
                        else
                            message = r.getString(R.string.please_insert_label_currency);
                        final android.app.AlertDialog.Builder mDialog = new android.app.AlertDialog.Builder(context);
                        mDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        mDialog.setTitle(r.getString(R.string.update_infomration));
                        mDialog.setMessage(message);
                        mDialog.create().show();
                    }
                    dialog.dismiss();
                }
            });

            updateTrip.setNegativeButton(r.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            updateTrip.create().show();
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trip tripToDelete = tripList.get(position);
            switch (v.getId()) {
                case R.id.listripsDeleteButton:
                    deleteTrip(tripToDelete, position);
                    break;
                case R.id.listripsDetailsButton:
                    Intent intent = new Intent(v.getContext(), ViewTripRecord.class);
                    intent.putExtra("TripID", tripToDelete.getID());
                    v.getContext().startActivity(intent);
                    tripListener.onActivityChanged();

                    break;
                case R.id.listripsEditButton:
                    editTrip(tripToDelete, position);
                    break;
            }

        }
    }

}
