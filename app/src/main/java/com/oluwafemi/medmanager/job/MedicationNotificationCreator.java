package com.oluwafemi.medmanager.job;

import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by phemi-mint on 4/18/18.
 */

public class MedicationNotificationCreator implements JobCreator {

        @Override
        public Job create(@NonNull String tag) {
            switch (tag) {
                case MedicationReminderJob.TAG:
                    return new MedicationReminderJob();
                default:
                    return null;
            }
        }

}
