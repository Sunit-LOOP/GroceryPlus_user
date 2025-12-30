package com.sunit.groceryplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.sunit.groceryplus.models.DeliveryPerson;

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
                int availableInt = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE));
                boolean available = availableInt == 1;
                int currentOrderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID));
                
                personnel.add(new DeliveryPerson(id, name, phone, status, available, currentOrderId));
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
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE, 1);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID, -1);
        return db.insert(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, null, values);
    }

    public boolean updateDeliveryPerson(int personId, String name, String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME, name);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE, phone);
        int rows = db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)});
        return rows > 0;
    }

    public boolean deleteDeliveryPerson(int personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseContract.DeliveryPersonEntry.TABLE_NAME,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)});
        return rows > 0;
    }

    public boolean setAvailability(int personId, boolean available) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE, available ? 1 : 0);
        if (!available) {
            values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID, -1);
        }
        int rows = db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)});
        return rows > 0;
    }

    public DeliveryPerson getNextAvailableDeliveryPerson() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE + " = 1";
        Cursor cursor = db.query(DatabaseContract.DeliveryPersonEntry.TABLE_NAME,
                null, selection, null, null, null,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " ASC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_NAME));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PHONE));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_STATUS));
            boolean available = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE)) == 1;
            int currentOrderId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID));
            cursor.close();
            return new DeliveryPerson(id, name, phone, status, available, currentOrderId);
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean assignOrderToDeliveryPerson(int personId, int orderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE, 0);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID, orderId);
        int rows = db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)});
        return rows > 0;
    }

    public boolean releaseDeliveryPerson(int personId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_AVAILABLE, 1);
        values.put(DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_CURRENT_ORDER_ID, -1);
        int rows = db.update(DatabaseContract.DeliveryPersonEntry.TABLE_NAME, values,
                DatabaseContract.DeliveryPersonEntry.COLUMN_NAME_PERSON_ID + " = ?",
                new String[]{String.valueOf(personId)});
        return rows > 0;
    }
}
