# Assignment 3 - Graphs
A simple Java 17 Maven project for graph algorithms.

## Commands
- `mvn test` runs JUnit 5 tests.
- `mvn compile exec:java` generates `results/results.csv` (large benchmark; needs enough heap, e.g. `MAVEN_OPTS=-Xmx4g`).
- `python3 plot.py` creates three charts after the benchmark.

The benchmark runs five algorithms on 10 graph configurations, 5 times each. The source includes DSU, Kruskal, Prim, Kosaraju, Kahn and DAG shortest paths. The DFS is iterative. The generated graph data is deterministic using Java Random(42).
