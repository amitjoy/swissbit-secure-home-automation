package com.swissbit.homeautomation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.swissbit.homeautomation.model.Device;
import com.swissbit.homeautomation.model.RaspberryPi;
import com.swissbit.homeautomation.utils.DBConstants;

import java.util.Random;

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

    public long insertRaspberry(String rid, String secureElementId, String name, String desc){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.RASPBERRYID, rid);
        contentValues.put(DBConstants.SECURE_ELEMENT_ID,secureElementId);
        contentValues.put(DBConstants.RASPBERRYNAME, name);
        contentValues.put(DBConstants.RASPBERRYDESC, desc);
        long id = db.insert(DBConstants.TABLE_NAME_RASPBERRYINFO, null, contentValues);
        return id;
    }

    public String getRaspberryId(){
        String[] columns= {DBConstants.RASPBERRYID};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_RASPBERRYINFO, columns, null,
                null, null, null, null);

        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYID));
            return id;
        }
        return null;
    }

    public boolean checkRaspberryId(String id){
        String[] columns= {DBConstants.RASPBERRYID};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_RASPBERRYINFO, columns, DBConstants.RASPBERRYID + " = '" + id + "'",
                null, null, null, null);

        if(cursor.getCount() != 0)
            return false;
        else
            return true;
    }

    public RaspberryPi getRaspberry(){
        String[] columns= {DBConstants.RASPBERRYID,DBConstants.SECURE_ELEMENT_ID,DBConstants.RASPBERRYNAME,DBConstants.RASPBERRYDESC};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_RASPBERRYINFO, columns, null,
                null, null, null, null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYID));
            String secureElementId = cursor.getString(cursor.getColumnIndex(DBConstants.SECURE_ELEMENT_ID));
            String name = cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYNAME));
            String desc = cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYDESC));
            RaspberryPi raspberryPi = RaspberryPi.createRaspberryPi(id, secureElementId, name, desc, false);

            return raspberryPi;
        }
            return null;
    }

    public int checkSecretCodeDialogShow(){
        String[] columns = {DBConstants.DIALOGSHOW};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_CREDENTIALS,columns,null,null,null,null,null);
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            Log.d("Dialog","1");
            return cursor.getInt(cursor.getColumnIndex(DBConstants.DIALOGSHOW));
        }
        Log.d("Dialog","0");
        return 0;
    }

    public void setCredentials(String code, String username, String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.CODE,code);
        contentValues.put(DBConstants.USERNAME,username);
        contentValues.put(DBConstants.PASSWORD,password);
        contentValues.put(DBConstants.CLIENTID,Integer.toString(new Random().nextInt(Integer.MAX_VALUE)));
        contentValues.put(DBConstants.DIALOGSHOW,1);

        db.update(DBConstants.TABLE_NAME_CREDENTIALS, contentValues, null, null);
        Log.d("After Set", "SetCred");
    }

    public String[] getCredentials() {
        String[] columns = {DBConstants.USERNAME, DBConstants.PASSWORD, DBConstants.CLIENTID};
        String[] credentials = new String[3];
        Cursor cursor = db.query(DBConstants.TABLE_NAME_CREDENTIALS, columns, null,
                null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            credentials[0] = cursor.getString(cursor.getColumnIndex(DBConstants.USERNAME));
            credentials[1] = cursor.getString(cursor.getColumnIndex(DBConstants.PASSWORD));
            credentials[2] = cursor.getString(cursor.getColumnIndex(DBConstants.CLIENTID));
        }
        return credentials;
    }


    public long insertDevice(int deviceId,String raspberryId,String raspberryName, String raspberryDesc, String deviceStatus){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.DEVICE_NODE_ID, deviceId);
        contentValues.put(DBConstants.RASPBERRYID, raspberryId);
        contentValues.put(DBConstants.DEVICE_NAME, raspberryName);
        contentValues.put(DBConstants.DEVICE_DESCRIPTION, raspberryDesc);
        contentValues.put(DBConstants.DEVICE_STATUS,deviceStatus);
        long id = db.insert(DBConstants.TABLE_NAME_DEVICES, null, contentValues);
        return id;
    }

    public boolean checkDeviceById(int id){
        String[] columns = {DBConstants.DEVICE_NODE_ID};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_DEVICES, columns, DBConstants.DEVICE_NODE_ID + " = '" + id + "'",
                null, null, null, null);
        if(cursor.getCount() != 0)
            return true;
        return false;
    }

    public Device getDevice(){
        String[] columns= {DBConstants.DEVICE_NODE_ID,DBConstants.RASPBERRYID,DBConstants.DEVICE_NAME,DBConstants.DEVICE_DESCRIPTION,
                            DBConstants.DEVICE_STATUS};
        Cursor cursor = db.query(DBConstants.TABLE_NAME_DEVICES, columns, null,
                null, null, null, null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            int deviceId = cursor.getInt(cursor.getColumnIndex(DBConstants.DEVICE_NODE_ID));
            String raspberryId = cursor.getString(cursor.getColumnIndex(DBConstants.RASPBERRYID));
            String deviceName = cursor.getString(cursor.getColumnIndex(DBConstants.DEVICE_NAME));
            String deviceDesc = cursor.getString(cursor.getColumnIndex(DBConstants.DEVICE_DESCRIPTION));
            String deviceStatus = cursor.getString(cursor.getColumnIndex(DBConstants.DEVICE_STATUS));
            Device device = Device.createDevice(deviceId,raspberryId,deviceName,deviceDesc,deviceStatus);

            return device;
        }
        return null;
    }

    public void updateDeviceStatus(String status,int nodeId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.DEVICE_STATUS,status);
        db.update(DBConstants.TABLE_NAME_DEVICES, contentValues, null, null);
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
                db.execSQL(DBConstants.CREATE_TABLE_DEVICES);
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
                db.execSQL(DBConstants.DROP_TABLE_DEVICES);
                onCreate(db);
            } catch(SQLException e){
                Log.e("UpgradeDb", "" +e);
            }
        }
    }

}
