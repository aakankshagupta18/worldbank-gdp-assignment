package com.example.worldbank;

import java.util.Objects;

/** Minimal record for World Bank GDP entries (year/value) */
public class GdpRecord {
    private final String year;   // e.g. "2022"
    private final Double value;  // may be null

    public GdpRecord(String year, Double value) {
        this.year = year;
        this.value = value;
    }

    public String getYear() { return year; }
    public Double getValue() { return value; }

    public int getYearAsInt() {
        try {
            return Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE; // push bad years to the front if any
        }
    }

    @Override
    public String toString() {
        return "GdpRecord{year=" + year + ", value=" + value + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GdpRecord)) return false;
        GdpRecord that = (GdpRecord) o;
        return Objects.equals(year, that.year) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, value);
    }
}
