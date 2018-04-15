package com.oluwafemi.medmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by phemi-mint on 4/14/18.
 */

public class MedicationSectionedAdapter extends StatelessSection {

    private List<Medication> medicationList;
    private String medicationTitle;

    public MedicationSectionedAdapter(List<Medication> medications, String medicationMonth) {
        super(SectionParameters.builder()
            .itemResourceId(R.layout.list_item_row_medication)
            .headerResourceId(R.layout.category_header)
            .build());

        medicationList = medications;
        medicationTitle = medicationMonth;
    }

    @Override
    public int getContentItemsTotal() {
        return medicationList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new MedicationItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        MedicationItemHolder itemHolder = (MedicationItemHolder) holder;
        Medication medication = medicationList.get(position);
        if (medication != null) {

            itemHolder.tvMedicationName.setText(medication.getName());
//            String durationStr = Utility.formatRecyclerViewDateDaysOnly(medication.getStartDate()) + " - " + Utility.formatRecyclerViewDateDaysOnly(medication.getEndDate());
            String durationStr = Utility.formatModifyRecyclerViewDate(medication.getStartDate(), medication.getEndDate());
            itemHolder.tvMedicationDuration.setText(durationStr);
            itemHolder.tvMedicationFreq.setText(medication.getFrequency());
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new MedicationHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        MedicationHeaderViewHolder mHolder = (MedicationHeaderViewHolder)holder;
        mHolder.tvCategoryHeader.setText(medicationTitle);
    }


    class MedicationItemHolder extends RecyclerView.ViewHolder {

        TextView tvMedicationName, tvMedicationDuration, tvMedicationFreq;

        public MedicationItemHolder(View itemView) {
            super(itemView);
            tvMedicationName  = itemView.findViewById(R.id.tv_medication_name);
            tvMedicationDuration  = itemView.findViewById(R.id.tv_duration);
            tvMedicationFreq  = itemView.findViewById(R.id.tv_interval);
        }
    }

    private class MedicationHeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCategoryHeader;

        MedicationHeaderViewHolder(View view) {
            super(view);

            tvCategoryHeader = (TextView) view.findViewById(R.id.tv_category_header);
        }
    }

}
