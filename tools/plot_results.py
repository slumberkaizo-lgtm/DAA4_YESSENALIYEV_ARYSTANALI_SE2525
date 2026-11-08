import csv
import statistics
from collections import defaultdict
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

root = Path(__file__).resolve().parents[1]
groups = defaultdict(list)
with (root / "results/results.csv").open() as file:
    for row in csv.DictReader(file):
        groups[(row["algorithm"], row["input"], int(row["n"]))].append(row)

output = root / "results/plots"
output.mkdir(parents=True, exist_ok=True)
plt.rcParams.update({"font.size": 11, "axes.grid": True, "grid.alpha": 0.25})


def series(ax, algorithm, kind, metric, label, style="-"):
    sizes = sorted(n for a, k, n in groups if a == algorithm and k == kind)
    values = [statistics.median(float(r[metric]) for r in groups[(algorithm, kind, n)]) for n in sizes]
    ax.plot(sizes, values, style, marker="o", label=label)


def save(fig, name):
    fig.tight_layout()
    fig.savefig(output / name, dpi=160)
    plt.close(fig)


fig, ax = plt.subplots(figsize=(8, 3.7))
for kind in ("random", "different"):
    series(ax, "RecursiveDistance", kind, "operations", kind)
ax.set(title="Recursive edit distance: repeated subproblems", xlabel="String length n (characters)",
       ylabel="Recursive calls per run (log scale)", yscale="log")
ax.legend()
save(fig, "recursive_calls.png")

fig, ax = plt.subplots(figsize=(8, 3.7))
series(ax, "TableDistance", "different", "operations", "Full table")
series(ax, "TwoRowDistance", "different", "operations", "Two rows (same counts)", "--")
ax.set(title="DP edit distance: work is unchanged by row reuse", xlabel="String length n (characters)",
       ylabel="Interior cell updates per run")
ax.ticklabel_format(axis="y", style="sci", scilimits=(0, 0))
ax.legend()
save(fig, "dp_updates.png")

fig, axes = plt.subplots(1, 2, figsize=(10, 3.7))
for ax, kind in zip(axes, ("random", "repeated")):
    for algorithm in ("NaiveSearch", "KmpSearch"):
        series(ax, algorithm, kind, "comparisons", algorithm)
    ax.set(title=f"Search comparisons: {kind}", xlabel="Text length n (characters)",
           ylabel="Character comparisons per run", yscale="log")
    ax.legend()
save(fig, "search_comparisons.png")

fig, axes = plt.subplots(1, 3, figsize=(12, 3.5))
panels = [(["RecursiveDistance"], "different", "Recursive, all different"),
          (["TableDistance", "TwoRowDistance"], "different", "DP, all different"),
          (["NaiveSearch", "KmpSearch"], "repeated", "Search, repeated prefixes")]
for ax, (algorithms, kind, title) in zip(axes, panels):
    for algorithm in algorithms:
        series(ax, algorithm, kind, "time_ms", algorithm)
    ax.set(title=title, xlabel="Length n (characters)", ylabel="Median of 5 runs (ms)", yscale="log")
    ax.legend(fontsize=8)
save(fig, "median_times.png")
print("Saved four PNG plots in results/plots")
