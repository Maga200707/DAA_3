package daa;

import java.util.*;

public class Algorithms {
    public record MST(long weight, int edges, int components) {
    }

    public static MST kruskal(Graph g, Metrics m) {
        if (g.directed) throw new IllegalArgumentException("MST needs undirected graph");
        List<Graph.Edge> sorted = new ArrayList<>(g.edges);
        sorted.sort((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.w(), b.w());
        });
        DSU d = new DSU(g.n, m);
        long weight = 0;
        int used = 0;
        for (Graph.Edge e : sorted) {
            m.edgeScans++;
            if (d.union(e.u(), e.v())) {
                weight += e.w();
                used++;
                if (used == g.n - 1) break;
            }
        }
        return new MST(weight, used, d.components());
    }

    public static MST prim(Graph g, Metrics m) {
        if (g.directed) throw new IllegalArgumentException("MST needs undirected graph");
        boolean[] seen = new boolean[g.n];
        PriorityQueue<Graph.Edge> pq = new PriorityQueue<>((a, b) -> {
            m.comparisons++;
            return Integer.compare(a.w(), b.w());
        });
        long weight = 0;
        int used = 0, components = 0;
        for (int start = 0; start < g.n; start++) {
            if (seen[start]) continue;
            components++;
            pq.add(new Graph.Edge(-1, start, 0));
            while (!pq.isEmpty()) {
                Graph.Edge e = pq.poll();
                if (seen[e.v()]) continue;
                seen[e.v()] = true;
                if (e.u() != -1) {
                    weight += e.w();
                    used++;
                }
                for (Graph.Edge next : g.adj.get(e.v())) {
                    m.edgeScans++;
                    if (!seen[next.v()]) pq.add(next);
                }
            }
        }
        return new MST(weight, used, components);
    }

    public static int kosaraju(Graph g, Metrics m) {
        int n = g.n;
        boolean[] seen = new boolean[n];
        int[] order = new int[n];
        int count = 0;
        for (int s = 0; s < n; s++) {
            if (seen[s]) continue;
            ArrayDeque<int[]> stack = new ArrayDeque<>();
            seen[s] = true;
            stack.push(new int[]{s, 0});
            m.maxDepth = Math.max(m.maxDepth, stack.size());
            while (!stack.isEmpty()) {
                int[] top = stack.peek();
                int u = top[0];
                if (top[1] < g.adj.get(u).size()) {
                    Graph.Edge e = g.adj.get(u).get(top[1]++);
                    m.edgeScans++;
                    int v = e.v();
                    if (!seen[v]) {
                        seen[v] = true;
                        stack.push(new int[]{v, 0});
                        m.maxDepth = Math.max(m.maxDepth, stack.size());
                    }
                } else {
                    order[count++] = u;
                    stack.pop();
                }
            }
        }
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < n; i++) rev.add(new ArrayList<>());
        for (Graph.Edge e : g.edges) rev.get(e.v()).add(e.u());
        Arrays.fill(seen, false);
        int comps = 0;
        for (int i = n - 1; i >= 0; i--) {
            int s = order[i];
            if (seen[s]) continue;
            comps++;
            ArrayDeque<Integer> st = new ArrayDeque<>();
            st.push(s);
            seen[s] = true;
            m.maxDepth = Math.max(m.maxDepth, st.size());
            while (!st.isEmpty()) {
                int u = st.pop();
                for (int v : rev.get(u)) {
                    m.edgeScans++;
                    if (!seen[v]) {
                        seen[v] = true;
                        st.push(v);
                        m.maxDepth = Math.max(m.maxDepth, st.size());
                    }
                }
            }
        }
        return comps;
    }

    public static int[] kahn(Graph g, Metrics m) {
        int[] indeg = new int[g.n];
        for (Graph.Edge e : g.edges) indeg[e.v()]++;
        ArrayDeque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < g.n; i++) if (indeg[i] == 0) q.add(i);
        int[] order = new int[g.n];
        int count = 0;
        while (!q.isEmpty()) {
            int u = q.remove();
            order[count++] = u;
            for (Graph.Edge e : g.adj.get(u)) {
                m.edgeScans++;
                if (--indeg[e.v()] == 0) q.add(e.v());
            }
        }
        if (count != g.n) throw new IllegalArgumentException("Graph has a cycle");
        return order;
    }

    public record Paths(long[] dist, int[] parent) {
        public List<Integer> path(int v) {
            List<Integer> result = new ArrayList<>();
            while (v != -1) {
                result.add(v);
                v = parent[v];
            }
            Collections.reverse(result);
            return result;
        }
    }

    public static Paths dagPaths(Graph g, Metrics m) {
        int[] order = kahn(g, new Metrics());
        int[] indeg = new int[g.n];
        for (Graph.Edge e : g.edges) indeg[e.v()]++;
        long[] dist = new long[g.n];
        Arrays.fill(dist, Long.MAX_VALUE / 4);
        int[] parent = new int[g.n];
        Arrays.fill(parent, -1);
        for (int i = 0; i < g.n; i++) if (indeg[i] == 0) dist[i] = 0;
        for (int u : order)
            for (Graph.Edge e : g.adj.get(u)) {
                m.edgeScans++;
                long candidate = dist[u] + e.w();
                if (candidate < dist[e.v()]) {
                    dist[e.v()] = candidate;
                    parent[e.v()] = u;
                }
            }
        return new Paths(dist, parent);
    }

    public static boolean validOrder(Graph g, int[] order) {
        if (order.length != g.n) return false;
        int[] pos = new int[g.n];
        boolean[] seen = new boolean[g.n];
        for (int i = 0; i < order.length; i++) {
            int u = order[i];
            if (u < 0 || u >= g.n || seen[u]) return false;
            seen[u] = true;
            pos[u] = i;
        }
        for (Graph.Edge e : g.edges) if (pos[e.u()] >= pos[e.v()]) return false;
        return true;
    }
}
