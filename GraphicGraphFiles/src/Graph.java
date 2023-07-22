import java.util.*;

public class Graph {
    private HashMap<String, Vertex>  adjacencyList;
    private HashMap<String, Vertex> roots;
    private int size;
    private int time;

    /**
     * Initializes an empty graph
     *
     */
    public Graph() {
        this.adjacencyList = new HashMap<>();
        this.roots = new HashMap<>();
        this.size = 0;
        this.time = 0;
    }

    /**
     * Checks if there is a directed edge from {@code u} to {@code v}
     *
     * @param u a vertex
     * @param v a vertex
     * @return {@code true} if the {@code u-v} edge is in the graph
     * @throws IllegalArgumentException if a vertex does not exist
     */
    public boolean containsEdge(String u, String v) {
        if (adjacencyList.containsKey(u) && adjacencyList.containsKey(v)) {
            return adjacencyList.get(u).hasNeighbor(v);
        } else {
            throw new IllegalArgumentException("Vertex does not exist");
        }
    }

    /** GRAPH ADDITION/REMOVAL /*

    /**
     * Returns the weight of directed edge from {@code u} to {@code v} if it exists
     *
     * @param u a vertex
     * @param v a vertex
     * @return {@code true} if the {@code u-v} edge is in the graph
     * @throws IllegalArgumentException if a vertex does not exist or edge doesn't exist
     */
    public int getWeight(String u, String v) {
        if (adjacencyList.containsKey(u) && adjacencyList.containsKey(v)) {
            return adjacencyList.get(u).getWeight(v);
        } else {
            throw new IllegalArgumentException("Vertex does not exist");
        }
    }

    /**
     * Creates vertices u and v if they are not already in the graph and then creates an edge
     * from {@code u} to {@code v} if it does not already exist. If the {@code u-v} edge already
     * exists, the edge weight should not change.
     * @param u      a vertex
     * @param v      a vertex
     * @param weight the edge weight
     * @return {@code true} if the graph changed as a result of this call, false otherwise
     * @throws IllegalArgumentException u == v
     */
    public boolean addDirectedEdge(String u, String v, int weight) {
        if (u.equals(v)) {
            throw new IllegalArgumentException("Vertices can't be equal");
        }
        addVertex(u, false);
        addVertex(v, false);
        if (containsEdge(u, v)) {
            return false;
        } else {
            adjacencyList.get(u).addEdge(v, weight);
            return true;
        }
    }

    /**
     * Adds a vertex {@code u} to the graph if it is not in the graph.
     *
     * @param u a vertex
     * @param rootInTree whether this vertex is the root of a BFS, DFS, or shortest path tree
     * @return {@code true} if the graph changed as a result of this call, false otherwise
     */
    public boolean addVertex(String u, boolean rootInTree) {
        if (!adjacencyList.containsKey(u)) {
            Vertex newNode = new Vertex();
            adjacencyList.put(u, newNode);
            if (rootInTree) {
                roots.put(u, newNode);
            }
            size++;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes a vertex {@code u} and all it's incoming and outgoing edges from the graph.
     *
     * @param u a vertex
     * @return {@code true} if the graph changed as a result of this call, false otherwise
     */
    public boolean removeVertex(String u) {
        if (!adjacencyList.containsKey(u)) {
            return false;
        }
        adjacencyList.remove(u);
        for (Map.Entry<String, Vertex> e : adjacencyList.entrySet()) {
            e.getValue().removeEdge(u);
        }
        return true;
    }

    /**
     * Remove a directed edge from {@code u} to {@code v} if it exists in the graph.
     *
     * @param u a vertex
     * @param v a vertex
     * @return {@code true} if the graph changed as a result of this call, false otherwise
     */
    public boolean removeDirectedEdge(String u, String v) {
        if (u.equals(v)) {
            throw new IllegalArgumentException("Vertices can't be equal");
        }
        if (!(adjacencyList.containsKey(u) && adjacencyList.containsKey(v))) {
            throw new IllegalArgumentException("Vertex is not in graph");
        }
        if (containsEdge(u, v)) {
            adjacencyList.get(u).removeEdge(v);
            return true;
        } else {
            return false;
        }
    }

    /** GRAPH ALGORITHMS /*

     /**
     * Helper method that resets every vertex's discovered, times, and distances in the graph
     */
    private void resetNodes() {
        for (Map.Entry<String, Vertex> e : getAdjacencyList()) {
            Vertex v = e.getValue();
            v.setDiscovered(false);
            v.setStart(0);
            v.setFinish(0);
            v.setDistance(Integer.MAX_VALUE);
            v.setDistanceUpdate(null);
        }
    }

    /**
     * The Breadth-First Search algorithm.
     *
     * @param source the vertex the search will initially start at.
     * @return a graph representation of the BFS forest
     */
    public Graph bfs(String source) {
        if (!adjacencyList.containsKey(source)) {
            throw new IllegalArgumentException("Source is not in graph");
        }
        resetNodes();
        Graph bfsForest = new Graph();
        bfsForest.addVertex(source, true);
        bfsVisit(bfsForest, source);

        for (Map.Entry<String, Vertex> e : adjacencyList.entrySet()) {
            if (!e.getValue().getDiscovered()) {
                String label = e.getKey();
                bfsForest.addVertex(e.getKey(), true);
                bfsVisit(bfsForest, label);
            }
        }
        return bfsForest;
    }

    /**
     * Helper method to visit all vertices reachable from {@code u} and add them according to
     * the BFS forest
     *
     * @param forest the BFS forest
     * @param u a vertex
     */
    private void bfsVisit(Graph forest, String u) {
        Vertex curr = adjacencyList.get(u);
        LinkedList<String> queue = new LinkedList<>();
        curr.setDiscovered(true);
        queue.add(u);

        while (queue.size() != 0) {
            String visited = queue.pollFirst();
            for (String s : adjacencyList.get(visited).getNeighbors()) {
                Vertex neighbor = adjacencyList.get(s);
                if (!neighbor.getDiscovered()) {
                    neighbor.setDiscovered(true);
                    forest.addDirectedEdge(visited, s, getWeight(visited, s));
                    queue.addLast(s);
                }
            }
        }
    }

    /**
     * The Depth-First Search.
     *
     * @param source the vertex that DFS starts at
     * @return the graph representation of the DFS forest
     */
    public Graph dfs(String source) {
        if (!adjacencyList.containsKey(source)) {
            throw new IllegalArgumentException("Source is not in graph");
        }
        time = 0;
        resetNodes();
        Graph dfsForest = new Graph();
        dfsForest.addVertex(source, true);
        dfsVisit(dfsForest, source);
        for (Map.Entry<String, Vertex> e : adjacencyList.entrySet()) {
            if (!e.getValue().getDiscovered()) {
                String label = e.getKey();
                dfsForest.addVertex(label, true);
                dfsVisit(dfsForest, label);
            }
        }
        return dfsForest;
    }

    /**
     * Helper method to visit a vertex's neighbors and update start and finish times accordingly.
     *
     * @param forest the DFS forest
     * @param u a vertex
     */
    private void dfsVisit(Graph forest, String u) {
        time++;
        Vertex graphVertex = adjacencyList.get(u);
        Vertex forestVertex = forest.getVertex(u);
        forestVertex.setStart(time);
        graphVertex.setDiscovered(true);
        for (String s : graphVertex.getNeighbors()) {
            if (!adjacencyList.get(s).getDiscovered()) {
                forest.addDirectedEdge(u, s, 1);
                dfsVisit(forest, s);
            }
        }
        time++;
        forestVertex.setFinish(time);
    }

    /**
     * Dijkstra's Algorithm.
     *
     * @param source the root of the shortest path tree
     * @return the shortest path tree rooted at {@code source}
     */
    public Graph dijkstra(String source) {
        if (!adjacencyList.containsKey(source)) {
            throw new IllegalArgumentException("Source is not in graph");
        }
        resetNodes();
        adjacencyList.get(source).setDistance(0);
        PriorityQueue<String> queue = new PriorityQueue<>(size, (o1, o2) -> {
            Vertex v1 = adjacencyList.get(o1);
            Vertex v2 = adjacencyList.get(o2);
            if (v1 == null || v2 == null) {
                throw new IllegalArgumentException("Vertex is not in graph");
            }
            int d1 = v1.getDistance();
            int d2 = v2.getDistance();
            return Integer.compare(d1, d2);
        });
        adjacencyList.get(source).setDistance(0);
        for (Map.Entry<String, Vertex> entry : adjacencyList.entrySet()) {
            queue.add(entry.getKey());
        }
        Graph shortestPathTree = new Graph();
        shortestPathTree.addVertex(source, true);

        while (!queue.isEmpty()) {
            String curr = queue.poll();
            Vertex currVertex = adjacencyList.get(curr);
            int dist = currVertex.getDistance();
            if (dist == Integer.MAX_VALUE) {
                break;
            }
            shortestPathTree.addVertex(curr, false);
            currVertex.setDiscovered(true);
            for (String s : currVertex.getNeighbors()) {
                Vertex neighbor = adjacencyList.get(s);
                int edgeWeight = getWeight(curr, s);
                if (edgeWeight < 0) {
                    throw new IllegalArgumentException("A path starting from source contains " +
                            "negative edge weight");
                }
                if (!neighbor.getDiscovered()) {
                    int newDistance = currVertex.getDistance() + edgeWeight;
                    if (newDistance < neighbor.getDistance()) {
                        queue.remove(s);
                        neighbor.setDistance(newDistance);
                        neighbor.setDistanceUpdate(curr);
                        queue.add(s);
                    }
                }
            }
        }

        for (Map.Entry<String, Vertex> entry : shortestPathTree.getAdjacencyList()) {
            String label = entry.getKey();
            Vertex vertex = adjacencyList.get(label);
            String parent = vertex.getDistanceUpdate();
            if (parent != null) {
                shortestPathTree.addDirectedEdge(parent, label, getWeight(parent, label));
            }
        }

        return shortestPathTree;
    }

    /** GETTER METHODS */

    /**
     * @return the number of vertices in the graph
     */
    public int size() {
        return size;
    }

    /**
     * Gets vertex {@code u} from the graph
     *
     * @param u a vertex
     * @return the vertex corresponding to u
     */
    public Vertex getVertex(String u) {
        return adjacencyList.get(u);
    }

    /**
     * @return entry set of the HashMap of the adjacency list
     */
    public Set<Map.Entry<String, Vertex>> getAdjacencyList() {
        return adjacencyList.entrySet();
    }

    /**
     * @return entry set of the HashMap of the all the roots in the graph
     */
    public Set<Map.Entry<String, Vertex>> getRoots() {
        return roots.entrySet();
    }

}
