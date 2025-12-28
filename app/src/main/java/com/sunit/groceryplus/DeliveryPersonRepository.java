package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunit.groceryplus.models.DeliveryPerson; // Need to create this model too if not exists

import java.util.ArrayList;
import java.util.List;

public class DeliveryPersonRepository {
    private DatabaseHelper dbHelper;

    public DeliveryPersonRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<DeliveryPerson> getAllDeliveryPersonnel() {
        List<DeliveryPerson> personnel = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, 
                null, null, null, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS));
                
                personnel.add(new DeliveryPerson(id, name, phone, status));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return personnel;
    }
    
    public long addDeliveryPerson(String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE, phone);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS, "Available");
        return db.insert(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, values);
    }
}
