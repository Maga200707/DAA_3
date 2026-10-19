package daa;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class AlgorithmsTest {
    @Test
    void dsu() {
        DSU d = DSU.makeSet(5);
        assertTrue(d.union(0, 1));
        assertTrue(d.union(1, 2));
        assertFalse(d.union(0, 2));
        assertEquals(3, d.components());
        assertEquals(d.find(0), d.find(2));
    }

    @Test
    void mstRandom() {
        Random r = new Random(5);
        for (int t = 0; t < 100; t++) {
            Graph g = new Graph(20, false);
            for (int i = 1; i < 20; i++) g.add(i, r.nextInt(i), r.nextInt(100));
            for (int j = 0; j < 80; j++) {
                int a = r.nextInt(20), b = r.nextInt(20);
                g.add(a, b, r.nextInt(100));
            }
            Algorithms.MST a = Algorithms.kruskal(g, new Metrics()), b = Algorithms.prim(g, new Metrics());
            assertEquals(a.weight(), b.weight());
            assertEquals(19, a.edges());
        }
    }

    @Test
    void disconnected() {
        Graph g = new Graph(4, false);
        g.add(0, 1, 3);
        assertEquals(3, Algorithms.prim(g, new Metrics()).components());
        assertEquals(3, Algorithms.kruskal(g, new Metrics()).components());
    }

    @Test
    void cycle() {
        Graph g = new Graph(3, true);
        g.add(0, 1, 1);
        g.add(1, 2, 1);
        g.add(2, 0, 1);
        assertEquals(1, Algorithms.kosaraju(g, new Metrics()));
        assertThrows(IllegalArgumentException.class, () -> Algorithms.kahn(g, new Metrics()));
    }

    @Test
    void paths() {
        Graph g = new Graph(4, true);
        g.add(0, 1, 2);
        g.add(0, 2, 5);
        g.add(1, 2, -4);
        g.add(2, 3, 3);
        assertTrue(Algorithms.validOrder(g, Algorithms.kahn(g, new Metrics())));
        assertEquals(1, Algorithms.dagPaths(g, new Metrics()).dist()[3]);
        assertEquals(List.of(0, 1, 2, 3), Algorithms.dagPaths(g, new Metrics()).path(3));
    }

    @Test
    void stackSafety() {
        Graph g = new Graph(100000, true);
        for (int i = 0; i < 99999; i++) g.add(i, i + 1, 1);
        assertEquals(100000, Algorithms.kosaraju(g, new Metrics()));
    }

    @Test
    void sccBruteForce() {
        Random r = new Random(9);
        for (int t = 0; t < 30; t++) {
            int n = 12;
            Graph g = new Graph(n, true);
            for (int i = 0; i < 30; i++) g.add(r.nextInt(n), r.nextInt(n), 1);
            boolean[][] reach = new boolean[n][n];
            for (int i = 0; i < n; i++) reach[i][i] = true;
            for (Graph.Edge e : g.edges) reach[e.u()][e.v()] = true;
            for (int k = 0; k < n; k++)
                for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) reach[i][j] |= reach[i][k] && reach[k][j];
            int brute = 0;
            for (int i = 0; i < n; i++) {
                boolean earlier = false;
                for (int j = 0; j < i; j++) if (reach[i][j] && reach[j][i]) earlier = true;
                if (!earlier) brute++;
            }
            assertEquals(brute, Algorithms.kosaraju(g, new Metrics()));
        }
    }

    @Test
    void dagVsBellmanFord() {
        Random r = new Random(3);
        for (int t = 0; t < 40; t++) {
            Graph g = new Graph(25, true);
            for (int j = 0; j < 70; j++) {
                int a = r.nextInt(25), b = r.nextInt(25);
                if (a < b) g.add(a, b, r.nextInt(21) - 10);
            }
            long[] d = new long[25];
            int[] indeg = new int[25];
            for (Graph.Edge e : g.edges) indeg[e.v()]++;
            Arrays.fill(d, Long.MAX_VALUE / 4);
            for (int i = 0; i < 25; i++) if (indeg[i] == 0) d[i] = 0;
            for (int k = 0; k < 24; k++) for (Graph.Edge e : g.edges) d[e.v()] = Math.min(d[e.v()], d[e.u()] + e.w());
            assertArrayEquals(d, Algorithms.dagPaths(g, new Metrics()).dist());
        }
    }
}
