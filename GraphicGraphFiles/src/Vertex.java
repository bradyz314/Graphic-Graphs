import java.util.HashMap;
import java.util.Set;

public class Vertex {

    // A HashMap that maps a neighbor's label to weight of the edge
    private HashMap<String, Integer> neighbors;
    // A boolean that is true if this node has been discovered during a graph traversal
    private boolean discovered;
    // An int that represents the start time of a node in a DFS traversal
    private int start;
    // An int that represents the finish time of a node in a DFS traversal
    private int finish;
    // An int that represents the distance of a vertex in Dijkstra's algorithm
    private int distance;
    // The label of the vertex that updates this vertex's distance during Dijkstra's
    // The parent in the shortest path tree
    private String distanceUpdate;

    /**
     * Creates a new vertex with the given label with degree 0.
     */
    public Vertex() {
        this.neighbors = new HashMap<>();
        this.discovered = false;
        this.start = 0;
        this.finish = 0;
        this.distance = 0;
        this.distanceUpdate = null;
    }

    /**
     * Determines whether there is a directed edge from this vertex to v.
     *
     * @param v a vertex
     * @return Whether this node has an edge to v
     */
    public boolean hasNeighbor(String v) {
        return neighbors.containsKey(v);
    }

    /**
     * Adds a directed edge from this vertex to v or updates the edge weight.
     *
     * @param v      a vertex
     * @param weight weight of new edge
     */
    public void addEdge(String v, int weight) {
        neighbors.put(v, weight);
    }

    public void removeEdge(String v) {
        neighbors.remove(v);
    }

    /** SETTERS */
    
    /**
     * Sets discovered to the input boolean
     *
     * @param b a boolean
     */
    public void setDiscovered(boolean b) {
        this.discovered = b;
    }

    public void setStart(int s) {
        this.start = s;
    }

    public void setFinish(int f) {
        this.finish = f;
    }

    public void setDistance(int dist) {
        distance = dist;
    }

    public void setDistanceUpdate(String du) {
        distanceUpdate = du;
    }

    /** GETTERS */

    /**
     * Returns the weight of the edge from this vertex to v if it exists
     *
     * @param v a vertex
     * @return Weight of u-v edge
     * @throws IllegalArgumentException if there is no edge
     */
    public int getWeight(String v) {
        if (hasNeighbor(v)) {
            return neighbors.get(v);
        } else {
            throw new IllegalArgumentException("No u-v edge");
        }
    }

    public boolean getDiscovered() {
        return discovered;
    }

    public int getStart() {
        return start;
    }

    public int getFinish() {
        return finish;
    }

    public int getDistance() {
        return distance;
    }

    public String getDistanceUpdate() {
        return distanceUpdate;
    }

    /**
     * Returns the neighbors of this node
     *
     * @return the key set of the neighbors
     */
    public Set<String> getNeighbors() {
        return neighbors.keySet();
    }
}