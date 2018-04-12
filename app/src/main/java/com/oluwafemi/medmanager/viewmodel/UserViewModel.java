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

package com.oluwafemi.medmanager.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.oluwafemi.medmanager.db.MedicationDatabase;
import com.oluwafemi.medmanager.model.User;

import java.util.List;

/**
 * Created by phemi-mint on 4/4/18.
 */

public class UserViewModel extends AndroidViewModel {


    private MedicationDatabase medicationDatabase;
    private LiveData<List<User>> userList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        medicationDatabase = MedicationDatabase.getDatabase(application);
        userList = medicationDatabase.userDao().getAllUsers();
    }

    public LiveData<List<User>> getUserList() {
        return userList;
    }

    public void insertUser(User user) {
        new InsertUserAsyncTask(medicationDatabase, user).execute();
    }

    public void updateExistingUser(User user) {
        new UpdateUserAsyncTask(medicationDatabase, user).execute();
    }

    // AsyncTask to insert new user to DB
    static class InsertUserAsyncTask extends AsyncTask<Void, Void, Void> {

        MedicationDatabase medicationDatabase;
        User user;

        public InsertUserAsyncTask(MedicationDatabase UserDatabase, User user) {
            this.medicationDatabase = UserDatabase;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            medicationDatabase.userDao().insertUser(user);
            return null;
        }
    }

    // AsyncTask to insert new user to DB
    static class UpdateUserAsyncTask extends AsyncTask<Void, Void, Void> {

        MedicationDatabase medicationDatabase;
        User user;

        public UpdateUserAsyncTask(MedicationDatabase UserDatabase, User user) {
            this.medicationDatabase = UserDatabase;
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            medicationDatabase.userDao().updateUser(user);
            return null;
        }
    }

}
