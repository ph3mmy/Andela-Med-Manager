package com.oluwafemi.medmanager.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by phemi-mint on 4/11/18.
 */

@Entity
public class User {

    @NonNull
    @PrimaryKey
    private String Id;
    private String userName;
    private String email;
    private String userImageUrl;
    private String activeMedication;
    private Date activeMedStartDate;
    private Date activeMedEndDate;

    @NonNull
    public String getId() {
        return Id;
    }

    public void setId(@NonNull String id) {
        Id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getActiveMedication() {
        return activeMedication;
    }

    public void setActiveMedication(String activeMedication) {
        this.activeMedication = activeMedication;
    }

    public Date getActiveMedStartDate() {
        return activeMedStartDate;
    }

    public void setActiveMedStartDate(Date activeMedStartDate) {
        this.activeMedStartDate = activeMedStartDate;
    }

    public Date getActiveMedEndDate() {
        return activeMedEndDate;
    }

    public void setActiveMedEndDate(Date activeMedEndDate) {
        this.activeMedEndDate = activeMedEndDate;
    }
}
