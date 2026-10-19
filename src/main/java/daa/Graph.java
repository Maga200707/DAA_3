package daa;

import java.util.*;
import java.nio.file.*;

public class Graph {
    public record Edge(int u, int v, int w) {
    }

    public final int n;
    public final boolean directed;
    public final List<Edge> edges = new ArrayList<>();
    public final List<List<Edge>> adj = new ArrayList<>();

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
    }

    public void add(int u, int v, int w) {
        if (u < 0 || u >= n || v < 0 || v >= n) throw new IllegalArgumentException();
        Edge e = new Edge(u, v, w);
        edges.add(e);
        adj.get(u).add(e);
        if (!directed) adj.get(v).add(new Edge(v, u, w));
    }

    public static Graph read(String file) throws Exception {
        try (Scanner s = new Scanner(Path.of(file))) {
            int n = s.nextInt(), m = s.nextInt();
            boolean directed = s.next().equals("directed");
            Graph g = new Graph(n, directed);
            for (int i = 0; i < m; i++) g.add(s.nextInt(), s.nextInt(), s.nextInt());
            return g;
        }
    }
}
