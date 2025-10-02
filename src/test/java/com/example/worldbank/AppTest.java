package com.example.worldbank;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    void sortByYearAsc_ordersCorrectly() {
        var data = List.of(
                new GdpRecord("2020", 2.0),
                new GdpRecord("1960", 1.0),
                new GdpRecord("2010", 1.5)
        );
        var sorted = DataOps.sortByYearAsc(data);
        assertEquals("1960", sorted.get(0).getYear());
        assertEquals("2010", sorted.get(1).getYear());
        assertEquals("2020", sorted.get(2).getYear());
    }

    @Test
    void searchByYearStartsWith_filtersByPrefix() {
        var data = List.of(
                new GdpRecord("1999", 1.0),
                new GdpRecord("2000", 2.0),
                new GdpRecord("2001", 3.0),
                new GdpRecord("2010", 4.0)
        );
        var res = DataOps.searchByYearStartsWith(data, "200");
         assertEquals(2, res.size()); // 2000, 2001
         assertTrue(res.stream().allMatch(r -> r.getYear().startsWith("200")));
         // sanity: check sorting
         assertEquals("2000", res.get(0).getYear());
         assertEquals("2001", res.get(1).getYear());
    }

    @Test
    void compareWithPrevious_handlesGapsAndPercent() {
        var data = List.of(
                new GdpRecord("2018", 100.0),
                new GdpRecord("2019", null),
                new GdpRecord("2020", 120.0)
        );
        var cmp = DataOps.compareWithPrevious(data, 2020);
        assertEquals(2020, cmp.year);
        assertEquals(120.0, cmp.value);
        assertEquals(2018, cmp.prevYear); // skipped 2019 null
        assertEquals(100.0, cmp.prevValue);
        assertEquals(20.0, Math.round(cmp.percentChange)); // ~20%
    }
}
