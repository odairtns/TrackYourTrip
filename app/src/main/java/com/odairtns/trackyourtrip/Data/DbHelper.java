package com.odairtns.trackyourtrip.Data;

public class DbHelper {
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "TRACK_YOUR_TRIP";

    public static final String TABLE_TRIP = "TRIP";
    public static final String TABLE_TRIP_RECORD = "TRIP_RECORD";
    public static final String TABLE_EXPENSE_TYPE = "EXPENSE_TYPE";
    public static final String TABLE_CURRENCY = "CURRENCY";
    public static final String TABLE_TRIP_CURRENCIES = "TRIP_CURRENCIES";
    public static final String TABLE_PAYMENT_METHOD = "PAYMENT_METHOD";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_LABEL = "LABEL";
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    public static final String COLUMN_STD_CURRENCY = "STD_CURRENCY";
    public static final String COLUMN_MULTI_CURRENCY = "STD_MULTI_CURRENCY";
    public static final String COLUMN_BUDGET = "BUDGET";

    public static final String COLUMN_TRIP_ID = "TRIP_ID";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_EXP_TYPE = "EXP_TYPE";
    public static final String COLUMN_CURRENCY_ID = "CURRENCY_ID";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_DETAIL = "DETAIL";
    public static final String COLUMN_PAYMENT_METHOD = "PAYMENT_METHOD";

    public static final String COLUMN_IS_STD_CURRENCY = "STD_IS_STD_CURRENCY";
    public static final String COLUMN_EXG_RATE = "EXG_RATE";
    public static final String COLUMN_UPDATED_DATE = "UPDATED_DATE";
    public static final String COLUMN_UPDATED_TIME = "UPDATED_TIME";
    public static final String COLUMN_DISPLAY_ORDER = "DISPLAY_ORDER";


}
