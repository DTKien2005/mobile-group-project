package com.example.covid19app.features.vndashboard.data.model;

import com.google.gson.annotations.SerializedName;

public class CovidStats {

    @SerializedName("updated")
    private long updated;

    @SerializedName("country")
    private String country;

    @SerializedName("cases")
    private int cases;

    @SerializedName("todayCases")
    private int todayCases;

    @SerializedName("deaths")
    private int deaths;

    @SerializedName("todayDeaths")
    private int todayDeaths;

    @SerializedName("recovered")
    private int recovered;

    @SerializedName("todayRecovered")
    private int todayRecovered;

    @SerializedName("active")
    private int active;

    @SerializedName("critical")
    private int critical;

    @SerializedName("tests")
    private int tests;

    @SerializedName("population")
    private int population;

    // Constructor
    public CovidStats(long updated, String country, int cases, int todayCases, int deaths,
                      int todayDeaths, int recovered, int todayRecovered, int active,
                      int critical, int tests, int population) {
        this.updated = updated;
        this.country = country;
        this.cases = cases;
        this.todayCases = todayCases;
        this.deaths = deaths;
        this.todayDeaths = todayDeaths;
        this.recovered = recovered;
        this.todayRecovered = todayRecovered;
        this.active = active;
        this.critical = critical;
        this.tests = tests;
        this.population = population;
    }

    // Getters and Setters
    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCases() {
        return cases;
    }

    public void setCases(int cases) {
        this.cases = cases;
    }

    public int getTodayCases() {
        return todayCases;
    }

    public void setTodayCases(int todayCases) {
        this.todayCases = todayCases;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getTodayDeaths() {
        return todayDeaths;
    }

    public void setTodayDeaths(int todayDeaths) {
        this.todayDeaths = todayDeaths;
    }

    public int getRecovered() {
        return recovered;
    }

    public void setRecovered(int recovered) {
        this.recovered = recovered;
    }

    public int getTodayRecovered() {
        return todayRecovered;
    }

    public void setTodayRecovered(int todayRecovered) {
        this.todayRecovered = todayRecovered;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getTests() {
        return tests;
    }

    public void setTests(int tests) {
        this.tests = tests;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }
}
