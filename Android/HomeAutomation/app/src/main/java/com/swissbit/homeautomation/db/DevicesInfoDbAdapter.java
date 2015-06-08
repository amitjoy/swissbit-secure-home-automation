package com.swissbit.homeautomation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swissbit.homeautomation.utils.DBConstants;

import java.util.ArrayList;

/**
 * Created by manit on 02/06/15.
 */
public class DevicesInfoDbAdapter  {

    DevicesInfoDb helper;
    SQLiteDatabase db;

    public DevicesInfoDbAdapter(Context context){
        helper = new DevicesInfoDb(context);
        db = helper.getWritableDatabase();
    }

    public long insertData(String rid,String name, String desc){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.RASPBERRYID, rid);
        contentValues.put(DBConstants.RASPBERRYNAME, name);
        contentValues.put(DBConstants.RASPBERRYDESC, desc);
        long id = db.insert(DBConstants.TABLE_NAME_RASPBERRYINFO, null, contentValues);
        return id;
    }

    public boolean getRaspberryId(String id){
        String[] columns= {DBConstants.RASPBERRYID};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_RASPBERRYINFO, columns, DBConstants.RASPBERRYID + " = '" + id + "'",
                null, null, null, null);

        if(cursor.getCount() != 0)
            return false;
        else
            return true;
    }

    public ArrayList getRaspberrys(){
        ArrayList<String> raspberryDetails = new ArrayList<String>();
        String[] columns= {DBConstants.RASPBERRYID,DBConstants.RASPBERRYNAME,DBConstants.RASPBERRYDESC};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_RASPBERRYINFO, columns, null,
                null, null, null, null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            raspberryDetails.add(cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYID)));
            raspberryDetails.add(cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYNAME)));
            raspberryDetails.add(cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYDESC)));

            return raspberryDetails;
        }
        else
            return raspberryDetails;
    }

    public int checkSecretCodeDialogShow(){
        String[] columns = {DBConstants.DIALOGSHOW};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_CREDENTIALS,columns,null,null,null,null,null);
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(DBConstants.DIALOGSHOW));
        }
        return 0;
    }

    public void setCredentials(String code, String username, String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.CODE,code);
        contentValues.put(DBConstants.USERNAME,username);
        contentValues.put(DBConstants.PASSWORD,password);
        contentValues.put(DBConstants.DIALOGSHOW,1);
        db.update(DBConstants.TABLE_NAME_CREDENTIALS,contentValues,null,null);
    }

    static class DevicesInfoDb extends SQLiteOpenHelper{

        private Context context;
        public DevicesInfoDb(Context context) {
            super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
            Log.d("1","Constructor Called");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DBConstants.CREATE_TABLE_RASPBERRYINFO);
                db.execSQL(DBConstants.CREATE_TABLE_CREDENTIALS);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DBConstants.DIALOGSHOW, 0);
                db.insert(DBConstants.TABLE_NAME_CREDENTIALS, null, contentValues);

            } catch(SQLException e){
                Log.e("CreateDb", "" +e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DBConstants.DROP_TABLE_RASPBERRYINFO);
                db.execSQL(DBConstants.DROP_TABLE_CREDENTIALS);
                onCreate(db);
            } catch(SQLException e){
                Log.e("UpgradeDb", "" +e);
            }
        }
    }

}
