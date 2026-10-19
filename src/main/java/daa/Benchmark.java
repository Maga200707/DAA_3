package daa;

import java.util.*;
import java.nio.file.*;
import java.io.*;

public class Benchmark {

    static final long[][] REFERENCE = {
            {147970, 27, 396274}, {30363, 1, 252411},
            {468911, 137, 1271911}, {97589, 1, 723157},
            {1486139, 373, 3941828}, {309550, 1, 2381342},
            {4715515, 1188, 12551891}, {968517, 1, 7376612},
            {15012094, 3923, 40096539}, {3056681, 1, 23512087}
    };
    static final int[] NS = {1000, 3162, 10000, 31623, 100000};

    static Graph generate(int n, int m, String kind) {
        Random rnd = new Random(42);
        Graph g = new Graph(n, !kind.equals("mst"));
        if (kind.equals("mst")) {
            int[] perm = new int[n];
            for (int i = 0; i < n; i++) perm[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1), t = perm[i];
                perm[i] = perm[j];
                perm[j] = t;
            }
            for (int i = 1; i < n; i++) g.add(perm[rnd.nextInt(i)], perm[i], 1 + rnd.nextInt(1000));
        }
        int[] rank = null;
        if (kind.equals("dag")) {
            rank = new int[n];
            for (int i = 0; i < n; i++) rank[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1), t = rank[i];
                rank[i] = rank[j];
                rank[j] = t;
            }
        }
        while (g.edges.size() < m) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            while (a == b) b = rnd.nextInt(n);
            if (rank != null && rank[a] > rank[b]) {
                int t = a;
                a = b;
                b = t;
            }
            g.add(a, b, 1 + rnd.nextInt(1000));
        }
        return g;
    }

    static long run(String algorithm, Graph g, Metrics met) {
        switch (algorithm) {
            case "Kruskal":
                return Algorithms.kruskal(g, met).weight();
            case "Prim":
                return Algorithms.prim(g, met).weight();
            case "Kosaraju":
                return Algorithms.kosaraju(g, met);
            case "Kahn": {
                int[] o = Algorithms.kahn(g, met);
                if (!Algorithms.validOrder(g, o)) throw new AssertionError("Invalid topological order");
                return o.length;
            }
            case "DagShortestPath": {
                long sum = 0;
                for (long x : Algorithms.dagPaths(g, met).dist()) sum += x;
                return sum;
            }
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) throws Exception {
        Path out = Path.of("results/results.csv");
        Files.createDirectories(out.getParent());
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out))) {
            w.println("algorithm,graph,n,m,time_ms,dsu_ops,edge_scans,comparisons,max_depth,result");
            int caseIndex = 0;
            int mismatches = 0;
            for (int n : NS)
                for (int factor : new int[]{4, 20}) {
                    int m = n * factor;
                    String name = n + "_" + m;
                    System.out.println("Graph " + name);
                    for (String kind : new String[]{"mst", "scc", "dag"}) {
                        Graph g = generate(n, m, kind);
                        String[] algorithms = kind.equals("mst") ? new String[]{"Kruskal", "Prim"} : kind.equals("scc") ? new String[]{"Kosaraju"} : new String[]{"Kahn", "DagShortestPath"};
                        long expected = Long.MIN_VALUE;
                        for (String algorithm : algorithms) {
                            long[] times = new long[5];
                            Metrics first = null;
                            long value = 0;
                            for (int r = 0; r < 5; r++) {
                                Metrics met = new Metrics();
                                long start = System.nanoTime();
                                long result = run(algorithm, g, met);
                                times[r] = System.nanoTime() - start;
                                if (r == 0) {
                                    first = met;
                                    value = result;
                                } else if (result != value) throw new AssertionError("Different results");
                            }
                            if (kind.equals("mst")) {
                                if (expected != Long.MIN_VALUE && expected != value)
                                    throw new AssertionError("MST mismatch: " + name);
                                expected = value;
                            }
                            Arrays.sort(times);
                            double ms = times[2] / 1e6;
                            w.printf(Locale.US, "%s,%s,%d,%d,%.3f,%d,%d,%d,%d,%d%n", algorithm, name, n, m, ms, first.dsuOps, first.edgeScans, first.comparisons, first.maxDepth, value);
                            System.out.println("  " + algorithm + ": " + value);
                            int column = kind.equals("mst") ? 0 : kind.equals("scc") ? 1 : 2;
                            long referenceValue = algorithm.equals("Kahn") ? n : REFERENCE[caseIndex][column];
                            if (value != referenceValue) {
                                mismatches++;
                                System.err.println("  REFERENCE MISMATCH " + algorithm + " at " + name + ": actual=" + value + ", expected=" + referenceValue);
                            }
                        }
                    }
                    caseIndex++;
                    System.out.println("Reference mismatches (includes both MST algorithms): " + mismatches);
                    if (mismatches > 0)
                        System.err.println("WARNING: reference values do not match. Do not submit as verified.");
                }
        }
    }
}
