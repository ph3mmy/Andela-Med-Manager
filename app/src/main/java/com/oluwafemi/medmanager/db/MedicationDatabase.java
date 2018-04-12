package com.oluwafemi.medmanager.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.model.User;

/**
 * Created by phemi-mint on 3/28/18.
 */

@Database(entities = {Medication.class, User.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class MedicationDatabase extends RoomDatabase {

    private static MedicationDatabase INSTANCE;

    public static MedicationDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MedicationDatabase.class, "medication_db").build();
        }
        return INSTANCE;
    }

    public abstract MedicationDao medicationDao();
    public abstract UserDao userDao();

}
