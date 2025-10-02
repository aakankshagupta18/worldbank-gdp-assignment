
# World Bank GDP (India) — Java + Maven

Fetch GDP data from the World Bank API and provide utilities to:

* Sort by year (ascending)
* Search by “year starts with …”
* Compare a year’s value to the previous available year and compute % change

> ✅ Language: **Java 17+**
> ✅ Build: **Maven 3.8+**
> ✅ Tests: **JUnit 5**

---

## API

This project reads JSON from the World Bank API:

```
https://api.worldbank.org/v2/countries/IND/indicators/NY.GDP.MKTP.CD?per_page=5000&format=json
```

* Country: `IND` (India)
* Indicator: `NY.GDP.MKTP.CD` (GDP, current US$)

You can change both (see [Configuration](#configuration)).

---

## Features

* **Fetch**: Downloads full GDP time series in JSON and maps into `GdpRecord` objects (`year`, `value`).
* **Sort**: `DataOps.sortByYearAsc` returns data ordered from oldest to latest year.
* **Search**: `DataOps.searchByYearStartsWith("200")` → years in the 2000s.
* **Compare**: `DataOps.compareWithPrevious(year)` compares the selected year to the **nearest previous non-null** year and computes percentage change.

---

## Project Structure

```
worldbank-gdp-assignment/
├─ pom.xml
└─ src/
   └─ main/java/com/example/worldbank/
      ├─ App.java               # Demo CLI
      ├─ WorldBankClient.java   # HTTP + JSON parsing (Jackson)
      ├─ DataOps.java           # Sorting, searching, comparison utilities
      └─ GdpRecord.java         # Simple POJO
   └─ test/java/com/example/worldbank/
      └─ AppTest.java           # JUnit 5 tests
```

---

## Quickstart

### 1) Prerequisites

* Java **17** (or 11+)
* Maven **3.8+**

```bash
java -version
mvn -version
```

### 2) Build & Run

```bash
mvn clean test package

# Run with defaults
java -jar target/worldbank-gdp-1.0.0.jar
```

### 3) Command-line Arguments

```
java -jar target/worldbank-gdp-1.0.0.jar <searchPrefix> <compareYear>
```

Examples:

```bash
java -jar target/worldbank-gdp-1.0.0.jar 199 2005
java -jar target/worldbank-gdp-1.0.0.jar 202 2022
```

---

## Configuration

Change defaults in `App.java`:

```java
private static final String COUNTRY = "IND";          // e.g., "USA"
private static final String INDICATOR = "NY.GDP.MKTP.CD"; // GDP indicator
```

Or call client directly:

```java
WorldBankClient client = new WorldBankClient();
var records = client.fetchGdp("USA", "NY.GDP.MKTP.KD");
```

---

## Testing

```bash
mvn clean test
```

Tests cover:

* Sorting order
* Prefix search results
* Comparison logic with gaps & percent math

---

## Troubleshooting

* **`sun.misc.Unsafe` warnings**: harmless (from Maven's Guice on newer JDKs).
* **JUnit 3 errors**: remove legacy `AppTest.java` or use this repo's JUnit 5 setup.
* **HTTP errors**: check network/VPN, API may rate-limit.

---

## Build Details

* **Dependencies**: Jackson Databind, JUnit 5
* **Plugins**: maven-compiler-plugin, maven-shade-plugin, maven-surefire-plugin


---

## Contributing

1. Fork & branch
2. Add tests
3. `mvn clean test`
4. Open PR
