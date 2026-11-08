# DAA Assignment 4

Arystanali Yessenaliyev, SE-2525

Simple Java implementations of recursive, full-table and two-row edit distance, plus Naive and KMP string search. All search positions are zero-based and overlapping matches are included. An empty search pattern throws IllegalArgumentException.

## Requirements

- JDK 17 or later, with JAVA_HOME pointing to the JDK.
- Gradle 8.14.3 is included through the wrapper; a separate Gradle installation is not needed.
- Python 3 and matplotlib, only for regenerating the PNG plots.

## Build and test

Run from the project root:

```powershell
.\gradlew.bat clean test
```

## Run the experiment

```powershell
.\gradlew.bat run
```

The command overwrites results/results.csv with 200 measured runs. There are five timed runs after one unrecorded warm-up per case. The generator seed is 2525. Inputs, answers and counters repeat; timing depends on the computer and JVM. The recursive version deliberately uses small sizes because it has exponential worst-case cost.

## Layout

- src/main/java: algorithms, counters, input generator and experiment.
- src/test/java: JUnit 5 tests, reference values and randomized comparisons.
- results/results.csv: one row per timed run.
- results/plots: generated PNG plots.
- tools/plot_results.py: plot generation.
- REPORT.md: experiment design, results and conclusions.

The DP state, recurrence and base cases are in REPORT.md. Code comments were omitted by request, although the assignment asks for DP comments.