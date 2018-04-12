package com.oluwafemi.medmanager.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by phemi-mint on 4/11/18.
 */

@Parcel
@Entity
public class User {

    @NonNull
    @PrimaryKey
    public String Id;
    public String userName;
    public String email;
    public String userImageUrl;
    public String age;
    public String address;
    public String genotype;
    public String gender;
    public String activeMedication;
    public Date activeMedStartDate;
    public Date activeMedEndDate;

    // required empty constructor for Parceler
    public User() {
    }

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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGenotype() {
        return genotype;
    }

    public void setGenotype(String genotype) {
        this.genotype = genotype;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
