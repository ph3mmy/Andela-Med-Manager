package com.oluwafemi.medmanager.job;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.activity.MainActivity;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by phemi-mint on 4/18/18.
 */

public class MedicationReminderJob extends Job {

    public static final String TAG = "MedicationReminderJob";

        @NonNull
        @Override
        protected Result onRunJob(@NonNull Params params) {

            PersistableBundleCompat bundleCompat = params.getExtras();
            NotificationManager mNotificationManager = (NotificationManager)getContext().getSystemService(NOTIFICATION_SERVICE);
            Intent myIntent = new Intent(getContext() , MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, myIntent, 0);

            String channelId = getContext().getString(R.string.app_name);
            String appName = getContext().getString(R.string.app_name);
            String title = appName + " Reminder";
            String reminderName = bundleCompat.getString("name", "");
            String reminderDesc = bundleCompat.getString("desc", "");
            long from = bundleCompat.getLong("from", 0);
            long to = bundleCompat.getLong("to", 0);
            long id = bundleCompat.getLong("id", 0);
            boolean isFirstTime = bundleCompat.getBoolean("isFirstTime", true);
            String contentText = reminderName +" is to be used now";
            Notification notification = new NotificationCompat.Builder(getContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentIntent)
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(reminderDesc))
                    .setColor(Color.GREEN)

                    .build();
            if (mNotificationManager != null && !medicationDateLimitPassed(to)) {
                int NOTIFICATION = (int) id;
                mNotificationManager.notify(NOTIFICATION, notification);
            }

            if(isFirstTime){
                bundleCompat.putBoolean("isFirstTime", false);
                schedulePeriodic(bundleCompat);
            }
            Set<JobRequest> requests = JobManager.create(getContext()).getAllJobRequests();
            for (JobRequest request: requests) {
                PersistableBundleCompat requestExtras = request.getExtras();
                long maxDate = requestExtras.getLong("to", 0);
                if(medicationDateLimitPassed(maxDate)){
                    cancelJob(request.getJobId());
                }
            }
            return Result.SUCCESS;
        }

        private void cancelJob(int jobId) {
            JobManager.instance().cancel(jobId);
        }

        private boolean medicationDateLimitPassed(long to){
            return System.currentTimeMillis() > to;
        }
        public static void setMedicationReminder(Medication medication, Map<String, Integer> reminderRepeatMap) {
            long oneHourIncrement = 60 * 60 * 1000; //an hour
            int timeInterval = reminderRepeatMap.get(medication.getReminderRepeatFrequency());
            int hoursInterval = 24 / timeInterval;
            long startDateLong = Utility.dateTimeLong(medication.getStartDate(), medication.getReminderTime());
            long frequencyInterval = hoursInterval * oneHourIncrement;
            PersistableBundleCompat bundle = new PersistableBundleCompat();
            bundle.putString("name", medication.getName());
            bundle.putString("desc", medication.getDescription());
            bundle.putLong("from", medication.getStartDate().getTime());
            bundle.putLong("to", medication.getEndDate().getTime());
            bundle.putString("id", medication.getId());
            bundle.putLong("interval", frequencyInterval);
            bundle.putLong("start_long", startDateLong);
            bundle.putBoolean("isFirstTime", true);

            scheduleExactJob(bundle);
        }
        private static void scheduleExactJob(PersistableBundleCompat bundle) {
            long from = bundle.getLong("from", 0);
            long fromLong = bundle.getLong("start_long", 0);
            long timeToStart = from - System.currentTimeMillis();
            long timeToStartLong = fromLong - System.currentTimeMillis();
            new JobRequest.Builder(MedicationReminderJob.TAG)
                    .setExact(timeToStartLong)
                    .setExtras(bundle)
                    .build()
                    .schedule();
        }
        private static void schedulePeriodic(PersistableBundleCompat bundle) {
            long frequencyInterval = bundle.getLong("interval", 0);
            new JobRequest.Builder(MedicationReminderJob.TAG)
                    .setPeriodic(frequencyInterval, TimeUnit.MINUTES.toMillis(5))
                    .setUpdateCurrent(true)
                    .setExtras(bundle)
                    .build()
                    .schedule();
        }


    }
