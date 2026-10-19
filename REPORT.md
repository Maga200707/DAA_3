# Assignment 3: Graph Algorithms

## 1. Introduction
In this assignment I implemented several graph algorithms in Java. I used adjacency lists because they are easy to understand and work well for sparse graphs. I tested the algorithms on small graphs and used counters for larger graphs. The goal was to see not only which algorithm works but also why it works.

## 2. Complexity table
| Algorithm | Time | Extra space | Reason |
|---|---|---|---|
| DSU find / union | amortized O(alpha(V)) | O(V) total | Path compression and union by size keep trees shallow. |
| Kruskal | O(E log E) | O(V+E) | Sorting the edges is the expensive step. |
| Prim (heap) | O(E log E) | O(V+E) | Every adjacency edge can enter the heap. |
| Kosaraju | O(V+E) | O(V+E) | Two DFS passes and one reversed graph. |
| Kahn | O(V+E) | O(V) | Each vertex and edge is processed once. |
| DAG shortest paths | O(V+E) | O(V) | One topological order and one relaxation pass. |

## 3. Why MST algorithms work
The cut property says that a minimum-weight edge crossing a cut can be part of a minimum spanning tree. Kruskal sorts all edges and considers them from the smallest to the largest. If an edge joins two different DSU components, it crosses a cut between these components. So choosing this edge is safe. Prim does a similar thing, but its cut is between visited and unvisited vertices. It chooses the smallest edge crossing this cut using a priority queue. Repeating safe choices builds an MST, or a forest when the graph is disconnected. The exact selected edges can differ when weights are equal, but the total minimum weight must be the same.

## 4. Why one DAG pass works
A topological ordering places every edge's start before its end. Initially, every vertex with zero in-degree has distance zero. All other vertices have a very large distance. Suppose all vertices before the current vertex already have correct shortest distances. Every incoming edge of the current vertex comes from one of those earlier vertices. Therefore, its distance has already been updated using every possible predecessor. Relaxing its outgoing edges then passes correct candidates to later vertices. By induction, one pass through the topological order gives the shortest distances. Negative edges are fine because a DAG has no cycles, so there cannot be a negative cycle that keeps reducing a distance.

## 5. Which algorithm I would choose
| Situation | Choice | Why |
|---|---|---|
| Weighted undirected graph, edge list | Kruskal | Sorting edges and DSU are convenient. |
| Weighted undirected graph, adjacency list | Prim | A heap grows the tree directly. |
| Directed graph with possible cycles | Kosaraju | Finds groups of mutually reachable vertices. |
| Job dependencies | Kahn | Finds an order and detects cycles. |
| Shortest paths in a DAG | DAG shortest paths | Linear time, supports negative weights. |
| General graph with negative edges | Bellman-Ford | DAG method cannot be used if cycles exist. |

## 6. Benchmark and counters
The benchmark uses n = 1000, 3162, 10000, 31623 and 100000, with m = 4n and m = 20n. Each case has an MST graph, a directed graph, and a DAG. Each algorithm runs five times and the median runtime is saved. The important evidence is the counters, because milliseconds depend on the computer and JVM.

For Kosaraju, `edgeScans / (V+E) = 2E/(V+E)` and it stays close to 2 for these densities. For Kahn and DAG paths, `edgeScans / (V+E) = E/(V+E)`, also constant. For Kruskal, compare `dsuOps / E`; the ratio is bounded because DSU does only a few operations per examined edge. Sorting contributes the `E log E` part. The log-log slope for the traversal edge scans should be 1.00 when E is proportional to V. The actual Kruskal total-operation slope should be measured from the generated CSV, because sorting is not linear.

After running the benchmark, use the three PNG files in `results/plots/`: operations, scans per edge, and Kruskal scanned share. Kruskal can stop early when it already has V-1 edges in a connected graph, which is why its scan fraction can be below 1. Prim still checks adjacency edges to expand the tree.

## 7. Runtime notes
The two smallest benchmarks can have noisy times due to JVM warmup and background work. I therefore use the median of five runs and focus on counted operations for the complexity argument. The measured results should be checked against the reference table in the assignment before submission.

## 8. Conclusion
Kruskal and Prim solve the same MST problem in different ways. Kosaraju is useful for cycles and strongly connected components. Kahn is a simple way to schedule dependent tasks. A DAG also allows shortest paths with negative edges in linear time. Counting operations helps explain the theoretical complexity more reliably than comparing only time.

## 9. Verification status
The benchmark ran successfully and Kruskal and Prim agreed in all 10 configurations. However, the generated numeric values do **not** exactly match the assignment's reference table. This likely means the random-generator call sequence needs to be aligned with the teacher's reference implementation. Do not claim reference-value verification until this difference is resolved.
