package com.thebaileybrew.videogameinventory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.thebaileybrew.videogameinventory.database.InventoryContract.*;


public class InventoryDbHelper extends SQLiteOpenHelper {
    public static final String TAG = InventoryDbHelper.class.getSimpleName();

    //Name of Database
    private static final String DATABASE_NAME = "allinventory.db";

    //Database version. If schema is changed, the version must be incremented
    private static final int DATABASE_VERSION = 1;

    //Construct the new instance of the Inventory DB Helper
    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        addInventoryTable(db);
        addGamesTable(db);
    }

    private void addInventoryTable(SQLiteDatabase db){
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME
                + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.GAME_NAME + " TEXT NOT NULL, "
                + InventoryEntry.GAME_SYSTEM + " INTEGER NOT NULL DEFAULT 5, "
                + InventoryEntry.GAME_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.GAME_SALE_PRICE + " REAL, "
                + InventoryEntry.GAME_SUGGESTED_PRICE + " REAL, "
                + InventoryEntry.GAME_CONDITION + " TEXT, "
                + InventoryEntry.GAME_UPC_CODE + " TEXT);";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }
    private void addGamesTable(SQLiteDatabase db) {
        String SQL_CREATE_GAME_TABLE = "CREATE TABLE " + GameEntry.TABLE_NAME + " ("
                + GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GameEntry.GAME_NAME + " TEXT NOT NULL, "
                + GameEntry.GAME_GENRE + " TEXT, "
                + GameEntry.GAME_SYSTEM + " TEXT NOT NULL, "
                + GameEntry.GAME_RELEASE_DATE + " TEXT NOT NULL, "
                + GameEntry.GAME_DEVELOPER + " TEXT NOT NULL, "
                + GameEntry.GAME_DEV_COUNTRY + " TEXT, "
                + GameEntry.GAME_DEV_HQ + " TEXT, "
                + GameEntry.GAME_DEV_ESTB + " TEXT);";
        db.execSQL(SQL_CREATE_GAME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            dropInventoryTable(db);
            dropGamesTable(db);
        }
        onCreate(db);
    }

    private void dropGamesTable(SQLiteDatabase db) {
        String sql = "DROP TABLE IF EXISTS " + GameEntry.TABLE_NAME;
        db.execSQL(sql);
    }

    private void dropInventoryTable(SQLiteDatabase db) {
        String sql = "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;
        db.execSQL(sql);
    }
}
