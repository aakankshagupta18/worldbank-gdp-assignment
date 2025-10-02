package com.example.worldbank;

import java.util.*;
import java.util.stream.Collectors;

/** Sorting, searching, and comparing GDP data */
public class DataOps {

    /** 1) Sort by year ascending (e.g., 1960 ... 2024) */
    public static List<GdpRecord> sortByYearAsc(List<GdpRecord> in) {
        return in.stream()
                 .sorted(Comparator.comparingInt(GdpRecord::getYearAsInt))
                 .collect(Collectors.toList());
    }

    /** 2) Search by year prefix, e.g. "19" -> 1900-1999; "202" -> 2020-2029 */
    public static List<GdpRecord> searchByYearStartsWith(List<GdpRecord> in, String prefix) {
        if (prefix == null) prefix = "";
        final String p = prefix.trim();
        return in.stream()
                 .filter(r -> r.getYear() != null && r.getYear().startsWith(p))
                 .sorted(Comparator.comparingInt(GdpRecord::getYearAsInt))
                 .collect(Collectors.toList());
    }

    /** Container for a comparison result */
    public static class YearComparison {
        public final int year;
        public final Double value;
        public final Integer prevYear;
        public final Double prevValue;
        public final Double percentChange; // null if not computable
        public final String note;

        public YearComparison(int year, Double value, Integer prevYear, Double prevValue, Double percentChange, String note) {
            this.year = year;
            this.value = value;
            this.prevYear = prevYear;
            this.prevValue = prevValue;
            this.percentChange = percentChange;
            this.note = note;
        }

        @Override public String toString() {
            return "YearComparison{" +
                    "year=" + year +
                    ", value=" + value +
                    ", prevYear=" + prevYear +
                    ", prevValue=" + prevValue +
                    ", percentChange=" + percentChange +
                    (note == null ? "" : ", note='" + note + '\'') +
                    '}';
        }
    }

    /**
     * 3) Compare a year's value to the latest available previous year's value.
     * If the immediate previous year is missing/null, this walks backward until it finds a non-null value.
     */
    public static YearComparison compareWithPrevious(List<GdpRecord> data, int year) {
        Map<Integer, Double> map = new HashMap<>();
        for (GdpRecord r : data) {
            if (r.getYear() != null && r.getValue() != null) {
                try { map.put(Integer.parseInt(r.getYear()), r.getValue()); }
                catch (NumberFormatException ignore) {}
            }
        }

        Double curVal = map.get(year);
        if (curVal == null) {
            return new YearComparison(year, null, null, null, null,
                    "No value available for " + year);
        }

        Integer prev = null;
        Double prevVal = null;
        for (int y = year - 1; y >= year - 50; y--) { // search back up to 50 years
            if (map.containsKey(y)) {
                prev = y;
                prevVal = map.get(y);
                break;
            }
        }
        if (prev == null || prevVal == null) {
            return new YearComparison(year, curVal, null, null, null,
                    "No previous non-null value found before " + year);
        }
        if (prevVal == 0.0) {
            return new YearComparison(year, curVal, prev, prevVal, null,
                    "Previous value is zero; percentage change undefined");
        }
        double pct = (curVal - prevVal) / prevVal * 100.0;
        return new YearComparison(year, curVal, prev, prevVal, pct, null);
    }
}
