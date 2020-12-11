package com.odairtns.trackyourtrip.Models;

public class Trip {
    int ID;
    String label, description, stdCurrency;
    Boolean multipleCurrency;
    Float budget;

    public Trip() {
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStdCurrency() {
        return stdCurrency;
    }

    public void setStdCurrency(String stdCurrency) {
        this.stdCurrency = stdCurrency;
    }

    public Boolean getMultipleCurrency() {
        return multipleCurrency;
    }

    public void setMultipleCurrency(Boolean multipleCurrency) {
        this.multipleCurrency = multipleCurrency;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        this.budget = budget;
    }
}
