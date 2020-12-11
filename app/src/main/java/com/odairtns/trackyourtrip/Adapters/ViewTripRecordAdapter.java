package com.odairtns.trackyourtrip.Adapters;

import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.odairtns.trackyourtrip.Activities.ViewTripRecord;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripRecord;
import com.odairtns.trackyourtrip.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewTripRecordAdapter extends RecyclerView.Adapter<ViewTripRecordAdapter.TripRecordView> {
    private List<TripRecord> tripRecordList;
    private TripRecord tripRecord;
    private DbHandler dbHandler;
    private Context context;
    private Trip trip;
    private myRecordChangeListener recordChangeListener;


    public ViewTripRecordAdapter(List<TripRecord> tripRecordList, Context context) {
        this.tripRecordList = tripRecordList;
        this.context = context;
        this.recordChangeListener = null;
        dbHandler = new DbHandler(context);
    }

    public interface myRecordChangeListener{
        void onRecordDeleted();
        void onRecordChanged();
    }

    public void setRecordChangeListener(myRecordChangeListener listener){
        this.recordChangeListener = listener;
    }


    @NonNull
    @Override
    public TripRecordView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.view_trip_records,parent,false);
        return (new TripRecordView(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final TripRecordView holder, int position) {

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        tripRecord = new TripRecord();
        trip = new Trip();
        tripRecord = tripRecordList.get(position);
        trip = dbHandler.getTrip(tripRecord.getTripID());
        holder.date.setText(tripRecord.getDate());
        holder.expType.setText(tripRecord.getExpType());
        holder.localAmount.setText(nf.format(tripRecord.getAmount()));
        holder.localCurrency.setText(tripRecord.getCurrency());
        holder.stdCurrencyText.setText(trip.getStdCurrency());
        holder.paymentMethod.setText(tripRecord.getPaymentMethod());
        if(trip.getStdCurrency().contentEquals(tripRecord.getCurrency())){
            holder.stdAmount.setVisibility(View.GONE);
            holder.stdCurrencyText.setVisibility(View.GONE);
        }else {
            double exgrate = dbHandler.getExgRate(tripRecord.getTripID(),tripRecord.getCurrency())
                    * tripRecord.getAmount();
            //holder.stdAmount.setText(nf.format(tripRecord.getAmountStdCurrency()));
            holder.stdAmount.setVisibility(View.VISIBLE);
            holder.stdCurrencyText.setVisibility(View.VISIBLE);
            holder.stdAmount.setText(nf.format(exgrate));
        }

    }

    @Override
    public int getItemCount() {
        return tripRecordList.size();
    }

    public class TripRecordView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView date, expType, stdAmount, localAmount, localCurrency, stdCurrencyText, paymentMethod;
        private Button infoButton, deleteButton, updateButton;
        public TripRecordView(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.viewtriptrecordDate);
            expType = itemView.findViewById(R.id.viewtriptrecordExpType);
            stdAmount = itemView.findViewById(R.id.viewtriptrecordStdAmount);
            stdCurrencyText =  itemView.findViewById(R.id.viewtriptrecordStdAmountText);
            localAmount = itemView.findViewById(R.id.viewtriptrecordLocalAmount);
            localCurrency = itemView.findViewById(R.id.viewtriptrecordLocalCurrency);
            infoButton = itemView.findViewById(R.id.viewtriptrecordInfoButton);
            infoButton.setOnClickListener(this);
            deleteButton = itemView.findViewById(R.id.viewtriptrecordDelete);
            deleteButton.setOnClickListener(this);
            updateButton = itemView.findViewById(R.id.viewtriptrecordUpdateButton);
            updateButton.setOnClickListener(this);
            paymentMethod = itemView.findViewById(R.id.viewtriptrecordPaymentMethod);
        }


        @Override
        public void onClick(final View v) {
            final int position = getAdapterPosition();
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            final TripRecord tripRecord2 = tripRecordList.get(position);
            switch (v.getId()){
                case R.id.viewtriptrecordInfoButton:
                    dialog.setMessage(tripRecord2.getDetails());
                    dialog.setTitle(v.getResources().getString(R.string.details));
                    dialog.create().show();
                    break;
                case R.id.viewtriptrecordDelete:
                    deleteRecord(tripRecord2,position);
                    break;
                case R.id.viewtriptrecordUpdateButton:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    final View recordView = View.inflate(context,R.layout.activity_insert_record,null);
                    alertDialog.setView(recordView);
                    setupView(recordView,tripRecord2);
                    alertDialog.setNegativeButton(v.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setPositiveButton(v.getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateRecord(tripRecord2,position,recordView );
                            dialog.dismiss();
                        }
                    });
                    alertDialog.create().show();

                    break;
            }


        }
    }

    public void deleteRecord(final TripRecord deletedRecord, final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        final Resources resources = context.getResources();
        dialog.setMessage(resources.getString(R.string.delete_warning_message));
        dialog.setTitle(resources.getString(R.string.warning));
        dialog.setCancelable(true);
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
                dbHandler = new DbHandler(context);
                dbHandler.deleteTripRecord(deletedRecord.getTripID(),deletedRecord.getId());
                tripRecordList.remove(position);
                notifyItemRemoved(position);
                recordChangeListener.onRecordDeleted();


            }
        });
        dialog.create().show();
    }

    public void updateRecord(final TripRecord updatedRecord, final int position, final View view){
        final EditText mDate, mExpType, mAmount, mCurrency, paymentMethod;
        TextInputEditText mDetails;
        dbHandler = new DbHandler(context);

        mDate = view.findViewById(R.id.insertrecordDate);
        mExpType = view.findViewById(R.id.insertrecordExpType);
        mAmount = view.findViewById(R.id.insertrecordAmount);
        mDetails = view.findViewById(R.id.insertrecordDetails);
        mCurrency = view.findViewById(R.id.insertrecordCurrency);
        paymentMethod = view.findViewById(R.id.insertrecordPaymentMethod);

        updatedRecord.setDate(mDate.getText().toString());
        updatedRecord.setExpType(mExpType.getText().toString());
        updatedRecord.setDetails(mDetails.getText().toString());
        updatedRecord.setCurrency(mCurrency.getText().toString());
        updatedRecord.setAmount(Double.valueOf(mAmount.getText().toString()));
        updatedRecord.setPaymentMethod(paymentMethod.getText().toString());

        dbHandler.updateTripRecord(updatedRecord);
        tripRecordList.set(position,updatedRecord);
        notifyItemChanged(position);
        notifyItemRangeChanged(position,tripRecordList.size());

        recordChangeListener.onRecordChanged();
    }

    public void setupView(View view, final TripRecord tripRecord) {

        Button mSaveB, mCancelB, mSaveAdd;
        LinearLayout mLinearLayout;
        final EditText mDate, mExpType, mAmount, mCurrency, paymentMethod;
        TextInputEditText mDetails;
        dbHandler = new DbHandler(context);

        mSaveB = view.findViewById(R.id.insertrecordSaveB);
        mCancelB = view.findViewById(R.id.insertrecordCancelB);
        mSaveAdd = view.findViewById(R.id.insertrecordSaveAddB);
        mDate = view.findViewById(R.id.insertrecordDate);
        mExpType = view.findViewById(R.id.insertrecordExpType);
        mAmount = view.findViewById(R.id.insertrecordAmount);
        mDetails = view.findViewById(R.id.insertrecordDetails);
        mCurrency = view.findViewById(R.id.insertrecordCurrency);
        mLinearLayout = view.findViewById(R.id.insertrecordButtonLinearLayout);
        paymentMethod = view.findViewById(R.id.insertrecordPaymentMethod);

        mSaveB.setVisibility(View.GONE);
        mCancelB.setVisibility(View.GONE);
        mSaveAdd.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.GONE);

        mDate.setText(tripRecord.getDate());
        mExpType.setText(tripRecord.getExpType());
        mAmount.setText(tripRecord.getAmount().toString());
        mDetails.setText(tripRecord.getDetails());
        mCurrency.setText(tripRecord.getCurrency());
        paymentMethod.setText(tripRecord.getPaymentMethod());

        mCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                List<String> currencyList = dbHandler.getTripCurrencyID(tripRecord.getTripID());
                final CharSequence[] cs =  currencyList.toArray(new CharSequence[currencyList.size()]);
                alertDialog.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrency.setText(cs[which].toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
            }
        });


        paymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
                List<String> paymentMethodList = dbHandler.getPaymentmethod();
                final CharSequence[] cs =  paymentMethodList.toArray(new CharSequence[paymentMethodList.size()]);
                alertDialog.setItems(cs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        paymentMethod.setText(cs[which].toString());
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();
            }
        });

        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        mExpType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder expAlertBuilder  = new androidx.appcompat.app.AlertDialog.Builder(v.getContext());
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
            }
        });

    }


}
