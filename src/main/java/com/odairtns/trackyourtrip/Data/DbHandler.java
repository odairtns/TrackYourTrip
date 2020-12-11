package com.odairtns.trackyourtrip.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.odairtns.trackyourtrip.Models.Currency;
import com.odairtns.trackyourtrip.Models.ExpenseTypeModel;
import com.odairtns.trackyourtrip.Models.Trip;
import com.odairtns.trackyourtrip.Models.TripCurrency;
import com.odairtns.trackyourtrip.Models.TripRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DbHandler extends SQLiteOpenHelper {
Context ctx;
    public DbHandler(Context context) {
        super(context, DbHelper.DB_NAME, null, DbHelper.DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Creation of Database Tables
        String CreateTripTable = "CREATE TABLE "+DbHelper.TABLE_TRIP + "(" +
                DbHelper.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                DbHelper.COLUMN_LABEL + " TEXT, " +
                DbHelper.COLUMN_DESCRIPTION + " TEXT, " +
                DbHelper.COLUMN_BUDGET + " REAL, " +
                DbHelper.COLUMN_STD_CURRENCY + " TEXT NOT NULL, " +
                DbHelper.COLUMN_MULTI_CURRENCY + " INTEGER);";
        db.execSQL(CreateTripTable);

        String CreateTripRecord = "CREATE TABLE "+DbHelper.TABLE_TRIP_RECORD + "(" +
                DbHelper.COLUMN_ID + " INTEGER, " +
                DbHelper.COLUMN_TRIP_ID + " INTEGER, " +
                DbHelper.COLUMN_DATE + " REAL NOT NULL, " +
                DbHelper.COLUMN_EXP_TYPE + " TEXT NOT NULL, " +
                DbHelper.COLUMN_CURRENCY_ID + " TEXT NOT NULL, " +
                DbHelper.COLUMN_AMOUNT + " REAL NOT NULL, " +
                DbHelper.COLUMN_DETAIL + " TEXT, " +
                DbHelper.COLUMN_PAYMENT_METHOD + " TEXT, " +
                " PRIMARY KEY (" + DbHelper.COLUMN_ID + ", " + DbHelper.COLUMN_TRIP_ID + "));";
        db.execSQL(CreateTripRecord);

        String CreateExpenseType = "CREATE TABLE " + DbHelper.TABLE_EXPENSE_TYPE + "(" +
                DbHelper.COLUMN_EXP_TYPE + " TEXT PRIMARY KEY, " +
                DbHelper.COLUMN_DESCRIPTION + " TEXT);";
        db.execSQL(CreateExpenseType);


        String CreateCurrency = "CREATE TABLE " + DbHelper.TABLE_CURRENCY + "(" +
                DbHelper.COLUMN_DISPLAY_ORDER + " INTEGER, " +
                DbHelper.COLUMN_CURRENCY_ID + " TEXT PRIMARY KEY, " +
                DbHelper.COLUMN_DESCRIPTION + " TEXT);";
        db.execSQL(CreateCurrency);

        String CreateTripCurrencies = "CREATE TABLE "+DbHelper.TABLE_TRIP_CURRENCIES + "(" +
                DbHelper.COLUMN_TRIP_ID + " INTEGER, " +
                DbHelper.COLUMN_CURRENCY_ID + " TEXT, " +
                DbHelper.COLUMN_EXG_RATE + " REAL, " +
                DbHelper.COLUMN_IS_STD_CURRENCY + " INTEGER, " +
                DbHelper.COLUMN_UPDATED_DATE + " TEXT, " +
                DbHelper.COLUMN_UPDATED_TIME + " REAL, " +
                DbHelper.COLUMN_DISPLAY_ORDER + " INTEGER, " +
                " PRIMARY KEY (" + DbHelper.COLUMN_TRIP_ID + ", " + DbHelper.COLUMN_CURRENCY_ID + "));";
        db.execSQL(CreateTripCurrencies);

        String CreatePaymentMethod = "CREATE TABLE " + DbHelper.TABLE_PAYMENT_METHOD + "(" +
                DbHelper.COLUMN_PAYMENT_METHOD + " TEXT PRIMARY KEY);";
        db.execSQL(CreatePaymentMethod);

        //Insertion of basic data on the tables
        String InsertCurrency = "INSERT INTO " + DbHelper.TABLE_CURRENCY + "(" +
                DbHelper.COLUMN_DISPLAY_ORDER + ", "+
                DbHelper.COLUMN_CURRENCY_ID + "," + DbHelper.COLUMN_DESCRIPTION + ") VALUES " +
                "(10,'AED', 'UAE Dirham'), " +
                "(20,'ARS', 'Argentine Peso'), " +
                "(30,'AUD', 'Australian Dollar'), " +
                "(40,'BGN', 'Bulgarian Lev'), " +
                "(50,'BRL', 'Brazilian Real'), " +
                "(60,'BSD', 'Bahamian Dollar'), " +
                "(70,'CAD', 'Canadian Dollar'), " +
                "(80,'CHF', 'Swiss Franc'), " +
                "(90,'CLP', 'Chilean Peso'), " +
                "(100,'CNY', 'Chinese Renminbi'), " +
                "(110,'COP', 'Colombian Peso'), " +
                "(120,'CZK', 'Czech Koruna'), " +
                "(130,'DKK', 'Danish Krone'), " +
                "(140,'DOP', 'Dominican Peso'), " +
                "(150,'EGP', 'Egyptian Pound'), " +
                "(160,'EUR', 'Euro'), " +
                "(170,'FJD', 'Fiji Dollar'), " +
                "(180,'GBP', 'Pound Sterling'), " +
                "(190,'GTQ', 'Guatemalan Quetzal'), " +
                "(200,'HKD', 'Hong Kong Dollar'), " +
                "(210,'HRK', 'Croatian Kuna'), " +
                "(220,'HUF', 'Hungarian Forint'), " +
                "(230,'IDR', 'Indonesian Rupiah'), " +
                "(240,'ILS', 'Israeli Shekel'), " +
                "(250,'INR', 'Indian Rupee'), " +
                "(260,'ISK', 'Icelandic Krona'), " +
                "(270,'JPY', 'Japanese Yen'), " +
                "(280,'KRW', 'South Korean Won'), " +
                "(290,'KZT', 'Kazakhstani Tenge'), " +
                "(300,'MXN', 'Mexican Peso'), " +
                "(310,'MYR', 'Malaysian Ringgit'), " +
                "(320,'NOK', 'Norwegian Krone'), " +
                "(330,'NZD', 'New Zealand Dollar'), " +
                "(340,'PAB', 'Panamanian Balboa'), " +
                "(350,'PEN', 'Peruvian Nuevo Sol'), " +
                "(360,'PHP', 'Philippine Peso'), " +
                "(370,'PKR', 'Pakistani Rupee'), " +
                "(380,'PLN', 'Polish Zloty'), " +
                "(390,'PYG', 'Paraguayan Guarani'), " +
                "(400,'RON', 'Romanian Leu'), " +
                "(410,'RUB', 'Russian Ruble'), " +
                "(420,'SAR', 'Saudi Riyal'), " +
                "(430,'SEK', 'Swedish Krona'), " +
                "(440,'SGD', 'Singapore Dollar'), " +
                "(450,'THB', 'Thai Baht'), " +
                "(460,'TRY', 'Turkish Lira'), " +
                "(470,'TWD', 'New Taiwan Dollar'), " +
                "(480,'UAH', 'Ukrainian Hryvnia'), " +
                "(490,'USD', 'US Dollar'), " +
                "(500,'UYU', 'Uruguayan Peso'), " +
                "(510,'VND', 'Vietnamese Dong'), " +
                "(520,'ZAR', 'South African Rand');";
                db.execSQL(InsertCurrency);

        String InsertExpType;
        if(Locale.getDefault().getLanguage().contentEquals("pt")){
            InsertExpType = "INSERT INTO " + DbHelper.TABLE_EXPENSE_TYPE + "(" +
                    DbHelper.COLUMN_EXP_TYPE + "," + DbHelper.COLUMN_DESCRIPTION + ") VALUES " +
                    "('Refeição', 'Refeição'), ('Bebida', 'Bebida'),('Transporte', 'Transporte')," +
                    "('Acomodação', 'Acomodação'), ('Entretenimento', 'Entretenimento')," +
                    "('Internet', 'Internet'), ('Comida', 'Comida')," +
                    "('Presentes', 'Presentes'), ('Ingressos', 'Ingressos')," +
                    "('Despesas', 'Despesas')";
        }else{
            InsertExpType = "INSERT INTO " + DbHelper.TABLE_EXPENSE_TYPE + "(" +
                DbHelper.COLUMN_EXP_TYPE + "," + DbHelper.COLUMN_DESCRIPTION + ") VALUES " +
                "('Meals', 'Meals'), ('Drink', 'Drinks'),('Transport', 'Transport')," +
                "('Accomodation', 'Accomodation'), ('Entertainment', 'Entertainment')," +
                "('Internet', 'Internet'), ('Food', 'Food')," +
                "('Gifts', 'Gifts'), ('Tickets', 'Tickets')," +
                "('Charges', 'Charges')";}
        db.execSQL(InsertExpType);


        String InsertPaymentMethod;
        if(Locale.getDefault().getLanguage().contentEquals("pt")){
            InsertPaymentMethod = "INSERT INTO " + DbHelper.TABLE_PAYMENT_METHOD + "(" +
                    DbHelper.COLUMN_PAYMENT_METHOD + ") VALUES " +
                    "('Dinheiro'), ('Débito'), ('Crédito'), ('Crédito - VISA'),('Crédito - MasterCard'),('Crédito - AMEX')," +
                    "('Cartão Pré Pago'), ('Transferência'),('APP')";
        }else{
            InsertPaymentMethod = "INSERT INTO " + DbHelper.TABLE_PAYMENT_METHOD + "(" +
                    DbHelper.COLUMN_PAYMENT_METHOD + ") VALUES " +
                    "('Cash'), ('Debit'), ('Credit Card'), ('Credit Card - VISA'),('Credit Card - MasterCard'),('Credit Card - AMEX')," +
                    "('Pre Paid Credit Card'), ('Cash Transfer'),('APP')";}
        db.execSQL(InsertPaymentMethod);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do not need to drop the tables
        //Need to create the code to update tables when update occurs
        //This will be done when new version is released
        String drop = "drop table IF EXISTS "+DbHelper.TABLE_TRIP +";";
        db.execSQL(drop);
        drop = " drop table IF EXISTS "+DbHelper.TABLE_TRIP_RECORD+";";
        db.execSQL(drop);
        drop = " drop table IF EXISTS "+DbHelper.TABLE_EXPENSE_TYPE+";";
                        db.execSQL(drop);
        drop = " drop table IF EXISTS "+DbHelper.TABLE_CURRENCY+";";
                        db.execSQL(drop);
        drop = " drop table IF EXISTS "+DbHelper.TABLE_TRIP_CURRENCIES+";";
                        db.execSQL(drop);
        drop = " drop table IF EXISTS "+DbHelper.TABLE_PAYMENT_METHOD+";";
        db.execSQL(drop);
        onCreate(db);
    }

    //  Custom Methods to Handle DB Operations

    public void addExpenseType(ExpenseTypeModel expenseTypeModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_EXP_TYPE, expenseTypeModel.getExpType());
        values.put(DbHelper.COLUMN_DESCRIPTION, expenseTypeModel.getDescription());
        db.insert(DbHelper.TABLE_EXPENSE_TYPE, null, values);
        db.close();
    }

    public void deleteExpenseType(String expType){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTripRecord = "DELETE FROM "+DbHelper.TABLE_EXPENSE_TYPE +
                " WHERE " + DbHelper.COLUMN_EXP_TYPE + " = '"+ expType + "';";
        db.execSQL(deleteTripRecord);
        db.close();
    }

    public void updateExpenseType(ExpenseTypeModel expenseTypeModel ){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateTripRecord = "UPDATE "+DbHelper.TABLE_EXPENSE_TYPE +
                " set " +DbHelper.COLUMN_DESCRIPTION + " = '" + expenseTypeModel.getDescription() +
                "' where " + DbHelper.COLUMN_EXP_TYPE + " = '" + expenseTypeModel.getExpType() + "';";
        db.execSQL(updateTripRecord);
        db.close();
    }

    public int addTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

      int i = getNextTripID();
        values.put(DbHelper.COLUMN_ID,i);

        values.put(DbHelper.COLUMN_LABEL,trip.getLabel());
        values.put(DbHelper.COLUMN_DESCRIPTION, trip.getDescription());
        values.put(DbHelper.COLUMN_STD_CURRENCY, trip.getStdCurrency());
        values.put(DbHelper.COLUMN_MULTI_CURRENCY, trip.getMultipleCurrency());
        values.put(DbHelper.COLUMN_BUDGET, trip.getBudget());
        db.insert(DbHelper.TABLE_TRIP, null, values);

        TripCurrency tripCurrency = new TripCurrency();
        tripCurrency.setTripID(i);
        tripCurrency.setCurrency(trip.getStdCurrency());
        tripCurrency.setIsStdStd(Boolean.TRUE);
        tripCurrency.setExgRate((float) 1.0);
        addTripCurrencies(tripCurrency);

        db.close();

        return i;
    }

    public void deleteTrip(int tripID){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTripRecord = "DELETE FROM "+DbHelper.TABLE_TRIP_RECORD +
                " WHERE " + DbHelper.COLUMN_TRIP_ID + " = " + tripID + ";";
        db.execSQL(deleteTripRecord);
        deleteAllTripCurrencies(tripID);
        db.delete(DbHelper.TABLE_TRIP,DbHelper.COLUMN_ID + " = ? ",new String[]{String.valueOf(tripID)});
        db.close();
    }

    public void updateTrip(Trip trip){
        SQLiteDatabase db = this.getWritableDatabase();
        int tripID;
        String label, description, std_currency;
        float budget;

        tripID = trip.getID();
        label = trip.getLabel();
        description = trip.getDescription();
        std_currency = trip.getStdCurrency();
        budget = trip.getBudget();

        String updateTripRecord = "UPDATE "+DbHelper.TABLE_TRIP +
                " set " +DbHelper.COLUMN_LABEL + " = '" + label +"', "+
                DbHelper.COLUMN_DESCRIPTION + " = '" + description +"', "+
                DbHelper.COLUMN_STD_CURRENCY + " = '" + std_currency +"', "+
                DbHelper.COLUMN_BUDGET + " = " + budget +
                " where " + DbHelper.COLUMN_ID + " = " + tripID + ";";

        db.execSQL(updateTripRecord);
        db.close();
    }

    public int getNextTripID(){
        SQLiteDatabase db = this.getReadableDatabase();
        int i;

        String query = "select  "+DbHelper.COLUMN_ID+" from "+DbHelper.TABLE_TRIP+" order by "+DbHelper.COLUMN_ID+" asc";
        Cursor cursor = db.rawQuery(query,null);


        if(cursor.moveToFirst()){
            cursor.moveToLast();
            i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID));
            i = i + 1;
        }else
            i = 1;

        return i;
    }

    public int getNextTripRecordID(int tripID){
        SQLiteDatabase db = this.getReadableDatabase();
        int i;

        String query = "Select "+DbHelper.COLUMN_ID+" from "+DbHelper.TABLE_TRIP_RECORD +
                " where "+DbHelper.COLUMN_TRIP_ID+" = "+tripID+" order by "+DbHelper.COLUMN_ID+
                " ASC";
        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            cursor.moveToLast();
            i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID));
            i = i + 1;
        }else
            i = 1;

        return i;
    }

    public void addTripRecord(TripRecord tripRecord, int tripID){
        SQLiteDatabase db = this.getWritableDatabase();
        int tripRecordID = getNextTripRecordID(tripID);
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID, tripRecordID);
        values.put(DbHelper.COLUMN_TRIP_ID, tripID);
        values.put(DbHelper.COLUMN_DATE,tripRecord.getDate());
        values.put(DbHelper.COLUMN_EXP_TYPE, tripRecord.getExpType());
        values.put(DbHelper.COLUMN_CURRENCY_ID, tripRecord.getCurrency());
        values.put(DbHelper.COLUMN_AMOUNT, tripRecord.getAmount());
        values.put(DbHelper.COLUMN_DETAIL, tripRecord.getDetails());
        values.put(DbHelper.COLUMN_PAYMENT_METHOD, tripRecord.getPaymentMethod());
        db.insert(DbHelper.TABLE_TRIP_RECORD, null, values);
        db.close();
    }

    public void updateTripRecord(TripRecord tripRecord){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateTripRecord = "UPDATE "+DbHelper.TABLE_TRIP_RECORD +
                " set " +DbHelper.COLUMN_DATE + " = '" + tripRecord.getDate() +"', "+
                DbHelper.COLUMN_EXP_TYPE + " = '" + tripRecord.getExpType() +"', "+
                DbHelper.COLUMN_CURRENCY_ID + " = '" + tripRecord.getCurrency() +"', "+
                DbHelper.COLUMN_AMOUNT + " = '" + tripRecord.getAmount() + "', "+
                DbHelper.COLUMN_DETAIL + " = '" + tripRecord.getDetails() + "', "+
                DbHelper.COLUMN_PAYMENT_METHOD + " = '" + tripRecord.getPaymentMethod() +
                "' where " + DbHelper.COLUMN_ID + " = " + tripRecord.getId() + " and "+
                DbHelper.COLUMN_TRIP_ID + " = "+ tripRecord.getTripID();
        db.execSQL(updateTripRecord);
        db.close();
    }

    public void deleteTripRecord(int tripID, int TripRecordID){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTripRecord = "DELETE FROM "+DbHelper.TABLE_TRIP_RECORD +
                " WHERE " + DbHelper.COLUMN_ID + " = "+ TripRecordID + " AND "+
                DbHelper.COLUMN_TRIP_ID + " = " + tripID;
        db.execSQL(deleteTripRecord);
        db.close();
    }

    public Float getExgRate(int tripID, String currency){
        SQLiteDatabase db = this.getReadableDatabase();
        Float exgRate = 0f;

        String query = "select "+DbHelper.COLUMN_EXG_RATE+" from "+ DbHelper.TABLE_TRIP_CURRENCIES +
                " where "+DbHelper.COLUMN_TRIP_ID+" = "+tripID+" and "+DbHelper.COLUMN_CURRENCY_ID +
                " = '"+currency+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            exgRate = cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_EXG_RATE));
        }
        return exgRate;
    }

    public Trip getTrip (int tripID){
        Trip trip = new Trip();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+DbHelper.TABLE_TRIP+" where "+DbHelper.COLUMN_ID+" = "+
                tripID;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
                trip.setID(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID)));
                trip.setLabel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_LABEL)));
                trip.setDescription(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)));
                trip.setStdCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_STD_CURRENCY)));
                trip.setBudget(cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_BUDGET)));

                int i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_MULTI_CURRENCY));
                if(i == 1)
                    trip.setMultipleCurrency(Boolean.TRUE);
                else
                    trip.setMultipleCurrency(Boolean.FALSE);
        }
        cursor.close();
        return trip;
    }

  /*  public Boolean getTripMultiCurrency (int tripID){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select "+DbHelper.COLUMN_MULTI_CURRENCY+" from "+DbHelper.TABLE_TRIP+
                " where "+DbHelper.COLUMN_ID + " = " +tripID;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            int i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_MULTI_CURRENCY));
            if(i == 1)
                return Boolean.TRUE;
            else
                return  Boolean.FALSE;
        }
        return  Boolean.FALSE;
    }

   */

    public List<Trip> getTrips (){
        List<Trip> list;
        list = new ArrayList<>();
        Trip trip;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+DbHelper.TABLE_TRIP;

        Cursor cursor = db.rawQuery(query,null);

        if(cursor.moveToFirst()){
            do {
                trip = new Trip();
                trip.setID(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID)));
                trip.setLabel(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_LABEL)));
                trip.setDescription(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)));
                trip.setStdCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_STD_CURRENCY)));
                trip.setBudget(cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_BUDGET)));

                int i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_MULTI_CURRENCY));
                if(i == 1)
                    trip.setMultipleCurrency(Boolean.TRUE);
                else
                    trip.setMultipleCurrency(Boolean.FALSE);

                list.add(trip);

            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void addTripCurrencies(TripCurrency tripCurrency){
        SQLiteDatabase db = this.getWritableDatabase();

        int stdCurr, displayOrder;

        if(tripCurrency.getIsStdStd()) {
            stdCurr = 1;
        }
        else {
            stdCurr = 0;
        }


        displayOrder = tripCurrency.getDisplayOrder();

        String InsertTripCurrency = "INSERT INTO " + DbHelper.TABLE_TRIP_CURRENCIES + "(" +
                DbHelper.COLUMN_TRIP_ID
                + ", " + DbHelper.COLUMN_CURRENCY_ID
                + ", " + DbHelper.COLUMN_IS_STD_CURRENCY
                + ", " + DbHelper.COLUMN_EXG_RATE
                + ", " + DbHelper.COLUMN_UPDATED_DATE
                + ", " + DbHelper.COLUMN_UPDATED_TIME
                + ", " + DbHelper.COLUMN_DISPLAY_ORDER
                + ") VALUES " +
                "(" + tripCurrency.getTripID() + " , '" + tripCurrency.getCurrency() + "', '" +
                stdCurr + "', " + tripCurrency.getExgRate() + ", '" +
                tripCurrency.getUpdatedDate() + "', "+ tripCurrency.getUpdatedTime()
                 + ", "+ displayOrder +")";

        try{
            db.execSQL(InsertTripCurrency);
        }catch (SQLiteConstraintException e){
            Toast.makeText(ctx,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public List<TripCurrency> getTripCurrency (int tripID){
        List<TripCurrency> list;
        list = new ArrayList<>();
        TripCurrency tripCurrency;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DbHelper.TABLE_TRIP_CURRENCIES+" where "+DbHelper.COLUMN_TRIP_ID +
                " = " + tripID + " order by "+DbHelper.COLUMN_DISPLAY_ORDER + " asc;";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                tripCurrency = new TripCurrency();
                tripCurrency.setTripID(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_TRIP_ID)));
                tripCurrency.setCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
                tripCurrency.setExgRate(cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_EXG_RATE)));
                tripCurrency.setDisplayOrder(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_DISPLAY_ORDER)));

                int i = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_IS_STD_CURRENCY));
                if(i == 1)
                    tripCurrency.setIsStdStd(Boolean.TRUE);
                else
                    tripCurrency.setIsStdStd(Boolean.FALSE);
                list.add(tripCurrency);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void deleteTripCurrencies(int tripID, String currencyID){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTripRecord = "DELETE FROM "+DbHelper.TABLE_TRIP_CURRENCIES +
                " WHERE " + DbHelper.COLUMN_CURRENCY_ID + " = '"+ currencyID + "' AND "+
                DbHelper.COLUMN_TRIP_ID + " = " + tripID + ";";
        db.execSQL(deleteTripRecord);
        db.close();
    }

    public void deleteAllTripCurrencies(int tripID){
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTripRecord = "DELETE FROM "+DbHelper.TABLE_TRIP_CURRENCIES +
                " WHERE " + DbHelper.COLUMN_TRIP_ID + " = " + tripID + ";";
        db.execSQL(deleteTripRecord);

    }

    public void updateTripCurrency(TripCurrency tripCurrency){
        SQLiteDatabase db = this.getWritableDatabase();
        String updateTripCurrency = "UPDATE "+DbHelper.TABLE_TRIP_CURRENCIES +
                " set " +DbHelper.COLUMN_EXG_RATE + " = " + tripCurrency.getExgRate() +" ,"+
                DbHelper.COLUMN_DISPLAY_ORDER + " = " + tripCurrency.getDisplayOrder() +
                " where " + DbHelper.COLUMN_TRIP_ID + " = "+ tripCurrency.getTripID() +
                " and " + DbHelper.COLUMN_CURRENCY_ID + " = '" + tripCurrency.getCurrency() + "';";
        db.execSQL(updateTripCurrency);
        db.close();
    }

    public List<TripRecord> getTripRecords (int tripID){
        List<TripRecord> list;
        list = new ArrayList<>();
        TripRecord tripRecord;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DbHelper.TABLE_TRIP_RECORD+" where "+DbHelper.COLUMN_TRIP_ID +
                " = " + tripID;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                tripRecord = new TripRecord();
                tripRecord.setTripID(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_TRIP_ID)));
                tripRecord.setId(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_ID)));
                tripRecord.setDate(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DATE)));
                tripRecord.setExpType(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_EXP_TYPE)));
                tripRecord.setCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
                tripRecord.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.COLUMN_AMOUNT)));
                tripRecord.setDetails(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DETAIL)));
                tripRecord.setPaymentMethod(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_PAYMENT_METHOD)));
                list.add(tripRecord);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public TripCurrency getTripCurrencyDetail (int tripID){

        TripCurrency tripCurrency = new TripCurrency();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DbHelper.TABLE_TRIP_CURRENCIES+" where "+DbHelper.COLUMN_TRIP_ID +
                " = " + tripID + " and "+DbHelper.COLUMN_IS_STD_CURRENCY+" = "+1;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
                tripCurrency.setTripID(cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_TRIP_ID)));
                tripCurrency.setCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
                tripCurrency.setExgRate(cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_EXG_RATE)));
                tripCurrency.setUpdatedDate(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_UPDATED_DATE)));
                tripCurrency.setUpdatedTime(cursor.getFloat(cursor.getColumnIndex(DbHelper.COLUMN_UPDATED_TIME)));
                tripCurrency.setIsStdStd(true);

            }
        cursor.close();
        return tripCurrency;
    }

    public List<Currency> getCurrency (){
        List<Currency> list;
        list = new ArrayList<>();
        Currency currency;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DbHelper.TABLE_CURRENCY + " order by "+DbHelper.COLUMN_DISPLAY_ORDER
                +" asc;";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                currency = new Currency();
                currency.setCurrencyId(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
                currency.setDescription(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)));
                list.add(currency);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getCurrencyID (){
        List<String> list;
        list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select "+DbHelper.COLUMN_CURRENCY_ID+" from "+DbHelper.TABLE_CURRENCY;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getPaymentmethod (){
        List<String> list;
        list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select "+DbHelper.COLUMN_PAYMENT_METHOD+" from "+DbHelper.TABLE_PAYMENT_METHOD;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_PAYMENT_METHOD)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getTripCurrencyID (int tripID){
        List<String> list;
        list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select distinct "+DbHelper.COLUMN_CURRENCY_ID+" from "+DbHelper.TABLE_TRIP_CURRENCIES
                +" where "+ DbHelper.COLUMN_TRIP_ID + " = "+ tripID + " order by "+DbHelper.COLUMN_DISPLAY_ORDER
                + " asc;";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<ExpenseTypeModel> getExpType (){
        List<ExpenseTypeModel> list;
        list = new ArrayList<>();
        ExpenseTypeModel expenseTypeModel;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+DbHelper.TABLE_EXPENSE_TYPE;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                expenseTypeModel = new ExpenseTypeModel();
                expenseTypeModel.setExpType(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_EXP_TYPE)));
                expenseTypeModel.setDescription(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)));
                list.add(expenseTypeModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getExpTypeID (){
        List<String> list;
        list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select "+DbHelper.COLUMN_DESCRIPTION+" from "+DbHelper.TABLE_EXPENSE_TYPE;
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_DESCRIPTION)));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<TripRecord> getExpenseTotal(int tripID){
        List<TripRecord> tripRecordList = new ArrayList<>();
        TripRecord tripRecord;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select "+DbHelper.COLUMN_EXP_TYPE+", "+DbHelper.COLUMN_CURRENCY_ID+
                " , sum("+DbHelper.COLUMN_AMOUNT+") "+DbHelper.COLUMN_AMOUNT+" from "+
                DbHelper.TABLE_TRIP_RECORD+" where "+DbHelper.COLUMN_TRIP_ID+" = "+tripID+" group by "+
                DbHelper.COLUMN_EXP_TYPE + ", "+DbHelper.COLUMN_CURRENCY_ID;
        Cursor cursor = db.rawQuery(query,null);

        String queryExpType = "Select "+DbHelper.COLUMN_EXP_TYPE+" from "+
                DbHelper.TABLE_TRIP_RECORD+" where "+DbHelper.COLUMN_TRIP_ID+" = "+tripID+" group by "+
                DbHelper.COLUMN_EXP_TYPE;
        Cursor cursorCurrency = db.rawQuery(queryExpType,null);
        double stdAmount = 0.0;
        String expType;
        if(cursorCurrency.moveToFirst()){
            do{
                expType = cursorCurrency.getString(cursorCurrency.getColumnIndex(DbHelper.COLUMN_EXP_TYPE));
                if(cursor.moveToFirst()){
                    stdAmount = 0.0;
                    do {
                        if(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_EXP_TYPE)).contentEquals(expType)){
                            stdAmount = stdAmount +
                                    (cursor.getDouble(cursor.getColumnIndex(DbHelper.COLUMN_AMOUNT)) *
                                            getExgRate(tripID,cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID))));
                        }
                    }while (cursor.moveToNext());
                }
                tripRecord = new TripRecord();
                tripRecord.setTripID(tripID);
                tripRecord.setExpType(expType);
                tripRecord.setAmountStdCurrency(stdAmount);
                tripRecordList.add(tripRecord);


            }while (cursorCurrency.moveToNext());
        }

  /*      if(cursor.moveToFirst()){
            do {
            tripRecord = new TripRecord();
            tripRecord.setTripID(tripID);
            tripRecord.setAmount(cursor.getDouble(cursor.getColumnIndex(DbHelper.COLUMN_AMOUNT)));
            tripRecord.setExpType(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_EXP_TYPE)));
            tripRecord.setCurrency(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
            double stdAmount = cursor.getDouble(cursor.getColumnIndex(DbHelper.COLUMN_AMOUNT)) *
                    getExgRate(tripID,cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_CURRENCY_ID)));
            tripRecord.setAmountStdCurrency(stdAmount);
            tripRecordList.add(tripRecord);
            }while(cursor.moveToNext());}

   */
        return tripRecordList;
    }

}
