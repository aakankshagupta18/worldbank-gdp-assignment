package com.example.worldbank;

import java.util.List;

public class App {
    // Defaults per your ask
    private static final String COUNTRY = "IND";
    private static final String INDICATOR = "NY.GDP.MKTP.CD";

    public static void main(String[] args) throws Exception {
        WorldBankClient client = new WorldBankClient();
        List<GdpRecord> raw = client.fetchGdp(COUNTRY, INDICATOR);

        // 1) Sort ascending
        List<GdpRecord> sorted = DataOps.sortByYearAsc(raw);
        System.out.println("Total records: " + sorted.size());
        if (!sorted.isEmpty()) {
            System.out.println("Earliest: " + sorted.get(0));
            System.out.println("Latest:   " + sorted.get(sorted.size() - 1));
        }

        // 2) Search by year prefix (e.g., "20" or "199")
        String prefix = (args.length >= 1) ? args[0] : "20";
        System.out.println("\nSearch: years starting with \"" + prefix + "\"");
        List<GdpRecord> found = DataOps.searchByYearStartsWith(sorted, prefix);
        found.stream().limit(5).forEach(System.out::println);
        if (found.size() > 5) System.out.println("... (" + (found.size() - 5) + " more)");

        // 3) Compare with previous year (use exact year)
        int compareYear = (args.length >= 2) ? Integer.parseInt(args[1]) : 2019;
        System.out.println("\nCompare " + compareYear + " vs previous available year");
        DataOps.YearComparison cmp = DataOps.compareWithPrevious(sorted, compareYear);
        System.out.println(cmp);
    }
}
