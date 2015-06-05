package com.swissbit.homeautomation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by manit on 02/06/15.
 */
public class DevicesInfoDbAdapter  {

    DevicesInfoDb helper;

    public DevicesInfoDbAdapter(Context context){
        helper = new DevicesInfoDb(context);
    }

    public long insertData(String rid,String name, String desc){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DevicesInfoDb.RASPBERRYID, rid);
        contentValues.put(DevicesInfoDb.RASPBERRYNAME, name);
        contentValues.put(DevicesInfoDb.RASPBERRYDESC, desc);
        long id = db.insert(DevicesInfoDb.TABLE_NAME,null,contentValues);
        return id;
    }

    public boolean getRaspberryId(String id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String[] columns= {DevicesInfoDb.RASPBERRYID};
        Cursor cursor = db.query(DevicesInfoDb.TABLE_NAME,columns,DevicesInfoDb.RASPBERRYID + " = '"+id+"'",
                null,null,null,null);

        if(cursor.getCount() != 0)
            return false;
        else
            return true;
    }

    static class DevicesInfoDb extends SQLiteOpenHelper{

        private static final String DATABASE_NAME="DevicesInfoDb";
        private static final String TABLE_NAME="RaspberryInfo";
        private static final int DATABASE_VERSION = 1;
        private static final String UID = "_id";
        private static final String RASPBERRYID = "RaspberryId";
        private static final String RASPBERRYNAME = "RaspberryName";
        private static final String RASPBERRYDESC = "RaspberryDesc";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RASPBERRYID
                + " VARCHAR(15), " + RASPBERRYNAME
                + " VARCHAR(25), " + RASPBERRYDESC
                + " VARCHAR(50));";

        private  static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private Context context;
        public DevicesInfoDb(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d("1","Constructor Called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_TABLE);
            } catch(SQLException e){
                Log.e("CreateDb", "" +e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch(SQLException e){
                Log.e("UpgradeDb", "" +e);
            }
        }
    }

}
