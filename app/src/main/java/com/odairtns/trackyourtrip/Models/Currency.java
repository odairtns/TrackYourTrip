package com.odairtns.trackyourtrip.Models;

public class Currency {
    String currencyId, description;

    public Currency() {
    }

    public Currency(String currencyId, String description) {
        this.currencyId = currencyId;
        this.description = description;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
