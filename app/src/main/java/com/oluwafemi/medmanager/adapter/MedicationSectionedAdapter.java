package com.oluwafemi.medmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.oluwafemi.medmanager.R;
import com.oluwafemi.medmanager.activity.EditProfileActivity;
import com.oluwafemi.medmanager.activity.MedicationDetailsActivity;
import com.oluwafemi.medmanager.activity.ProfileActivity;
import com.oluwafemi.medmanager.model.Medication;
import com.oluwafemi.medmanager.util.Utility;

import org.parceler.Parcels;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

import static com.oluwafemi.medmanager.activity.EditProfileActivity.USER_KEY;
import static com.oluwafemi.medmanager.activity.MedicationDetailsActivity.MEDICATION_KEY;

/**
 * Created by phemi-mint on 4/14/18.
 */

public class MedicationSectionedAdapter extends StatelessSection {

    private List<Medication> medicationList;
    private String medicationTitle;
    private Context context;

    public MedicationSectionedAdapter(Context context, List<Medication> medicationList, String medicationTitle) {
        super(SectionParameters.builder()
            .itemResourceId(R.layout.list_item_row_medication)
            .headerResourceId(R.layout.category_header)
            .build());

        this.context = context;
        this.medicationList = medicationList;
        this.medicationTitle = medicationTitle;
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
        final Medication medication = medicationList.get(position);
        if (medication != null) {
            itemHolder.tvMedicationName.setText(medication.getName());
            String durationStr = Utility.dateRangeNoOfDays(medication.getStartDate(), medication.getEndDate())
                    + " "+ context.getResources().getString(R.string.bullet) + " " + Utility.dateRangeForRecyclerview(medication.getStartDate(), medication.getEndDate());
            itemHolder.tvMedicationDuration.setText(durationStr);
            itemHolder.tvMedicationFreq.setText(medication.getFrequency());
            itemHolder.tvReminderTime.setText(Utility.convert24hrsTo12hrs(medication.getReminderTime()));
            if (Utility.isEndDatePassed(medication.getEndDate())) {
                itemHolder.tvActivePassive.setText("Passed");
                itemHolder.tvActivePassive.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            } else {
                itemHolder.tvActivePassive.setText("Active");
            }
        }

        // set onclick listener to click items
        itemHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, MedicationDetailsActivity.class);
                mIntent.putExtra(MEDICATION_KEY, Parcels.wrap(medication));
                context.startActivity(mIntent);
            }
        });
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

        TextView tvMedicationName, tvMedicationDuration, tvMedicationFreq, tvReminderTime, tvActivePassive;
        View view;

        public MedicationItemHolder(View itemView) {
            super(itemView);
            tvMedicationName  = itemView.findViewById(R.id.tv_medication_name);
            tvMedicationDuration  = itemView.findViewById(R.id.tv_duration);
            tvMedicationFreq  = itemView.findViewById(R.id.tv_interval);
            tvReminderTime  = itemView.findViewById(R.id.tv_alarm_time);
            tvActivePassive  = itemView.findViewById(R.id.tv_active_passive);
            view = itemView;
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
