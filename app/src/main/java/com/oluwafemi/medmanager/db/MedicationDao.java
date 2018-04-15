/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oluwafemi.medmanager.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.oluwafemi.medmanager.model.Medication;

import java.util.Date;
import java.util.List;

/**
 * Created by phemi-mint on 3/28/18.
 */

@Dao
@TypeConverters(DateConverter.class)
public interface MedicationDao {

    @Query("select * from Medication order by dateCreated DESC")
    LiveData<List<Medication>> getAllMedications();

    @Query("select * from Medication where strftime('%m', datetime(startDate)) = '04'")
    LiveData<List<Medication>> getMedicationByMonth();

    // returns the list of medication for the current year only
    @Query("select * from Medication where strftime('%Y', datetime(dateCreated/1000, 'unixepoch')) = :lastYear or " +
            "strftime('%Y', datetime(dateCreated/1000, 'unixepoch')) = :thisYear order by dateCreated DESC")
    LiveData<List<Medication>> getAllMedicationsForCurrentYear(String lastYear, String thisYear);

    @Query("select * from Medication where name = :name")
    Medication loadMedicationWithName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedication(Medication... medications);

    @Update
    void updateMedication(Medication... medications);

    @Delete
    void deleteMedication(Medication Medication);
}
