package bmv.edge_limited_paths;

import java.util.Arrays;
import java.util.Comparator;

public class EdgeLimitedPaths {

  public boolean[] distanceLimitedPathsExist(int n, int[][] edgeList, int[][] queries) {
    int[][] sortedEdges = edgeList.clone();
    Arrays.sort(sortedEdges, Comparator.comparingInt(edge -> edge[2]));

    int[][] sortedQueries = new int[queries.length][4];
    for (int i = 0; i < queries.length; i++) {
      sortedQueries[i][0] = queries[i][0];
      sortedQueries[i][1] = queries[i][1];
      sortedQueries[i][2] = queries[i][2];
      sortedQueries[i][3] = i;
    }
    Arrays.sort(sortedQueries, Comparator.comparingInt(query -> query[2]));

    boolean[] answers = new boolean[queries.length];
    DisjointSet components = new DisjointSet(n);
    int edgeIndex = 0;

    for (int[] query : sortedQueries) {
      while (edgeIndex < sortedEdges.length && sortedEdges[edgeIndex][2] < query[2]) {
        components.union(sortedEdges[edgeIndex][0], sortedEdges[edgeIndex][1]);
        edgeIndex++;
      }
      answers[query[3]] = components.connected(query[0], query[1]);
    }

    return answers;
  }

  private static final class DisjointSet {

    private final int[] parent;
    private final int[] size;

    private DisjointSet(int count) {
      parent = new int[count];
      size = new int[count];
      Arrays.setAll(parent, index -> index);
      Arrays.fill(size, 1);
    }

    private boolean connected(int first, int second) {
      return find(first) == find(second);
    }

    private void union(int first, int second) {
      int firstRoot = find(first);
      int secondRoot = find(second);
      if (firstRoot == secondRoot) {
        return;
      }

      if (size[firstRoot] < size[secondRoot]) {
        int temporary = firstRoot;
        firstRoot = secondRoot;
        secondRoot = temporary;
      }

      parent[secondRoot] = firstRoot;
      size[firstRoot] += size[secondRoot];
    }

    private int find(int node) {
      int root = node;
      while (root != parent[root]) {
        root = parent[root];
      }

      while (node != root) {
        int next = parent[node];
        parent[node] = root;
        node = next;
      }
      return root;
    }
  }
}
