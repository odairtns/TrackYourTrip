package com.odairtns.trackyourtrip.Models;

public class TripCurrency {
    String currency, updatedDate;
    Float exgRate, updatedTime;
    Boolean isStd;
    int tripID;

    public Boolean getStd() {
        return isStd;
    }

    public void setStd(Boolean std) {
        isStd = std;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    int displayOrder;

    public TripCurrency() {
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getExgRate() {
        return exgRate;
    }

    public void setExgRate(Float exgRate) {
        this.exgRate = exgRate;
    }

    public Boolean getIsStdStd() {
        return isStd;
    }

    public void setIsStdStd(Boolean std) {
        isStd = std;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Float getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Float updatedTime) {
        this.updatedTime = updatedTime;
    }

}
