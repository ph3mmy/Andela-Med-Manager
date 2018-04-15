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

package com.oluwafemi.medmanager.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;

import java.util.List;

/**
 * Created by phemi-mint on 3/28/18.
 */

public class MedicationRecyclerAdapter extends RecyclerView.Adapter<MedicationRecyclerAdapter.MedicationHolder> {

    private List<Medication> medicationList;

    public MedicationRecyclerAdapter(List<Medication> medications) {
        this.medicationList = medications;
    }

    @NonNull
    @Override
    public MedicationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row_medication, parent, false);
        return new MedicationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationHolder holder, int position) {

        Medication medication = medicationList.get(position);
        if (medication != null) {

            holder.tvMedicationName.setText(medication.getName());
//            String durationStr = Utility.formatRecyclerViewDateDaysOnly(medication.getStartDate()) + " - " + Utility.formatRecyclerViewDateDaysOnly(medication.getEndDate());
            String durationStr = Utility.formatModifyRecyclerViewDate(medication.getStartDate(), medication.getEndDate());
            holder.tvMedicationDuration.setText(durationStr);
            holder.tvMedicationFreq.setText(medication.getFrequency());
        }
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }


    class MedicationHolder extends RecyclerView.ViewHolder {

        TextView tvMedicationName, tvMedicationDuration, tvMedicationFreq;

        public MedicationHolder(View itemView) {
            super(itemView);
            tvMedicationName  = itemView.findViewById(R.id.tv_medication_name);
            tvMedicationDuration  = itemView.findViewById(R.id.tv_duration);
            tvMedicationFreq  = itemView.findViewById(R.id.tv_interval);
        }
    }
}
