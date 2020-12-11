package com.odairtns.trackyourtrip.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.odairtns.trackyourtrip.Activities.ExpenseType;
import com.odairtns.trackyourtrip.Data.DbHandler;
import com.odairtns.trackyourtrip.Models.ExpenseTypeModel;
import com.odairtns.trackyourtrip.R;

import java.util.List;

public class ExpenseTypeAdapter extends RecyclerView.Adapter<ExpenseTypeAdapter.ExpenseViewHolder> {
   private List<ExpenseTypeModel> expenseTypeList;
   private ExpenseTypeModel expenseType;
   private Context context;
   private DbHandler dbHandler;

    public ExpenseTypeAdapter(List<ExpenseTypeModel> expenseTypeList, Context context) {
        this.expenseTypeList = expenseTypeList;
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.exp_type_list,parent,false);
        ExpenseViewHolder viewHolder = new ExpenseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        expenseType = expenseTypeList.get(position);
        holder.expType.setText(expenseType.getDescription());
    }

    @Override
    public int getItemCount() {
        return expenseTypeList.size();
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView expType;
        private Button editButton, deleteButton;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            expType = itemView.findViewById(R.id.exptypelistExpType);
            editButton = itemView.findViewById(R.id.exptypelistExpTypeEditButton);
            deleteButton = itemView.findViewById(R.id.exptypelistExpTypeDeleteButton);
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ExpenseTypeModel model = expenseTypeList.get(position);
            switch (v.getId()){
                case R.id.exptypelistExpTypeDeleteButton:
                    deleteExpType(model.getExpType(), position);
                    break;
                case R.id.exptypelistExpTypeEditButton:
                    updateExpType(model, position);
                    break;
            }


        }

        public void deleteExpType(String expType, int position){
            dbHandler = new DbHandler(context);
            dbHandler.deleteExpenseType(expType);
            notifyItemRangeRemoved(position,1);
            expenseTypeList = dbHandler.getExpType();
        }

        public void updateExpType(final ExpenseTypeModel expType, final int position){
            dbHandler = new DbHandler(context);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.add_new_exp_type, null);
            alertDialog.setView(view);
            final EditText updatedExpType = view.findViewById(R.id.addexptypeExpType);
            updatedExpType.setText(expType.getDescription());
            Resources resources = context.getResources();
            alertDialog.setPositiveButton(resources.getString(R.string.update), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  expType.setDescription(updatedExpType.getText().toString());
                  dbHandler.updateExpenseType(expType);
                    notifyItemChanged(position);
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
