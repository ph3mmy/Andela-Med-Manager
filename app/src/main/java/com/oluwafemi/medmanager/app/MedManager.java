package com.oluwafemi.medmanager.app;

import android.app.Application;

import com.evernote.android.job.JobManager;
import com.oluwafemi.medmanager.job.MedicationNotificationCreator;

/**
 * Created by phemi-mint on 4/18/18.
 */

public class MedManager extends Application {

    public MedManager() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.create(this).addJobCreator(new MedicationNotificationCreator());
    }
}
