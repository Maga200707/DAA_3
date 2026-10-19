package daa;

public class DSU {
    private final int[] parent, size;
    private int count;
    private final Metrics metrics;

    public DSU(int n, Metrics m) {
        parent = new int[n];
        size = new int[n];
        count = n;
        metrics = m;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public static DSU makeSet(int n) {
        return new DSU(n, new Metrics());
    }

    public int find(int x) {
        metrics.dsuOps++;
        int root = x;
        while (root != parent[root]) root = parent[root];
        while (x != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    public boolean union(int a, int b) {
        int x = find(a), y = find(b);
        if (x == y) return false;
        if (size[x] < size[y]) {
            int t = x;
            x = y;
            y = t;
        }
        parent[y] = x;
        size[x] += size[y];
        count--;
        metrics.dsuOps++;
        return true;
    }

    public int components() {
        return count;
    }
}
