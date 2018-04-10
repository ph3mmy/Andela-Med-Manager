package com.oluwafemi.medmanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.oluwafemi.medmanager.db.MedicationDatabase;
import com.oluwafemi.medmanager.model.Medication;

import java.util.List;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class MedicationViewModel extends AndroidViewModel {


    private MedicationDatabase medicationDatabase;
    private LiveData<List<Medication>> medicationList;

    public MedicationViewModel(@NonNull Application application) {
        super(application);

        medicationDatabase = MedicationDatabase.getDatabase(application);
        medicationList = medicationDatabase.medicationDao().getAllMedications();
    }

    public LiveData<List<Medication>> getMedicationList() {
        return medicationList;
    }

    public void insertMedication(Medication medication) {
        new InsertMedicationAsyncTask(medicationDatabase, medication).execute();
    }

    static class InsertMedicationAsyncTask extends AsyncTask<Void, Void, Void> {

        MedicationDatabase medicationDatabase;
        Medication medication;

        public InsertMedicationAsyncTask(MedicationDatabase medicationDatabase, Medication medication) {
            this.medicationDatabase = medicationDatabase;
            this.medication = medication;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            medicationDatabase.medicationDao().insertMedication(medication);
            return null;
        }
    }

}
