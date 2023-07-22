import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Set;

import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.Viewer;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;


public class GraphicGraphs {

    private enum graphAction {
        ADD_DIRECTED_EDGE,
        ADD_VERTEX,
        REMOVE_DIRECTED_EDGE,
        REMOVE_VERTEX
    }

    private enum graphAlgorithm {
        BFS,
        DFS,
        DIJKSTRA
    }

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private static int ySegment = 0;

    // Main screen
    private static JFrame mainFrame;

    // Screen that displays results
    private static JFrame resultFrame;

    // Text fields for inputs to adding a directed edge
    private static JTextField uTextField1;
    private static JTextField vTextField1;
    private static JTextField weightTextField;

    // Text fields for inputs to removing a directed edge
    private static JTextField uTextField2;
    private static JTextField vTextField2;

    // Text field for input to adding a vertex
    private static JTextField singleVertex1;

    // Text field for input to removing a vertex
    private static JTextField singleVertex2;

    // Text field for input to calling algorithms
    private static JTextField sourceTextField;

    // Panels for different functionalities
    private static JPanel addEdge;
    private static JPanel addVertex;
    private static JPanel removeEdge;
    private static JPanel removeVertex;
    private static JPanel source;

    // User Graph
    private static Graph userGraph;
    private static MultiGraph userGraphView;

    // Result Graph
    private static Graph algorithmGraph;
    private static MultiGraph algorithmGraphView;
    private static SpriteManager spriteManager;

    // A style sheet that specifies how each node, edge, and sprite will be drawn in the graph views
    protected static String styleSheet =
            "node {" +
                    "fill-color: #8c8c8c;" +
                    "size: 30px;" +
                    "fill-mode: dyn-plain;" +
                    "stroke-color: black;" +
                    "text-size: 25px;" +
                    "text-style: bold;" +
                    "}" +
            "edge {" +
                    "text-size: 25px;" +
                    "text-background-mode: rounded-box;" +
                    "text-background-color: white;" +
                    "}" +
            "sprite {" +
                    "fill-color: #d3d3d3;" +
                    "shape: box;" +
                    "size: 24px;" +
                    "text-size: 16px;" +
                    "}";

    // Method to run the UI
    public static void main(String[] args) throws Exception {
        Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        SCREEN_WIDTH = (int) (screenSize.width * 0.8);
        SCREEN_HEIGHT = (int) (screenSize.height * 0.8);
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        UIManager.put("Button.disabledText", Color.black);
        setUpUI();
    }

    // Method to set up UI
    private static void setUpUI() {
        // Sets up the main screen that displays the user's graph
        // Allows additional and removal of vertices and edges
        mainFrame = new JFrame("Graphic Graphs");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        // Sets up the screen that will display the resulting forests/trees from calling specific algorithms
        resultFrame = new JFrame("Algorithm Result");
        resultFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        resultFrame.setLayout(new BorderLayout());
        resultFrame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);

        // Underlying graph created by the user
        userGraph = new Graph();
        // A visual representation of the user graph using the MultiGraph class from GraphStream
        userGraphView = new MultiGraph("Graph");
        userGraphView.addAttribute("ui.stylesheet", styleSheet);
        Viewer userGraphViewer = new Viewer(userGraphView,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        userGraphViewer.enableAutoLayout();
        JPanel view = userGraphViewer.addDefaultView(false);

        // Underlying graph of the results
        algorithmGraph = new Graph();
        // A visual representation of the result graph using the MultiGraph class from GraphStream
        algorithmGraphView = new MultiGraph("Algorithm");
        algorithmGraphView.addAttribute("ui.stylesheet", styleSheet);
        spriteManager = new SpriteManager(algorithmGraphView);
        Viewer algorithmGraphViewer = new Viewer(algorithmGraphView,
                Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        algorithmGraphViewer.disableAutoLayout();
        JPanel resultView = algorithmGraphViewer.addDefaultView(false);

        // Sets up all the text fields
        uTextField1 = new JTextField(5);
        uTextField2 = new JTextField(5);
        vTextField1 = new JTextField(5);
        vTextField2 = new JTextField(5);
        singleVertex1 = new JTextField(5);
        singleVertex2 = new JTextField(5);
        weightTextField = new JTextField(5);
        sourceTextField = new JTextField(5);

        // Sets up the panel for adding an edge
        addEdge = new JPanel();
        addEdge.add(new JLabel("Vertex u: "));
        addEdge.add(uTextField1);
        addEdge.add(new JLabel("Vertex v: "));
        addEdge.add(vTextField1);
        addEdge.add(new JLabel("Weight: "));
        addEdge.add(weightTextField);

        // Sets up the panel for adding a vertex
        addVertex = new JPanel();
        addVertex.add(new JLabel("Vertex: "));
        addVertex.add(singleVertex1);

        // Sets up the panel for removing an edge
        removeEdge = new JPanel();
        removeEdge.add(new JLabel("Vertex u: "));
        removeEdge.add(uTextField2);
        removeEdge.add(new JLabel("Vertex v: "));
        removeEdge.add(vTextField2);

        // Sets up the panel for adding an edge
        removeVertex = new JPanel();
        removeVertex.add(new JLabel("Vertex: "));
        removeVertex.add(singleVertex2);

        // Sets up the panel for getting the source vertex
        source = new JPanel();
        source.add(new JLabel("Source: "));
        source.add(sourceTextField);

        // Sets up button to reset the user graph
        JButton newGraphButton = new JButton("New Graph");
        newGraphButton.addActionListener(e -> newGraph());

        // Sets up button to add a directed edge to user graph
        JButton addDirectedEdgeButton = new JButton("Add Directed Edge");
        addDirectedEdgeButton.addActionListener(e ->
                editUserGraph(graphAction.ADD_DIRECTED_EDGE));

        // Sets up button to add a vertex to the graph
        JButton addVertex = new JButton("Add Vertex");
        addVertex.addActionListener(e ->
                editUserGraph(graphAction.ADD_VERTEX));

        // Sets up button to remove a directed edge to the graph
        JButton removeDirectedEdgeButton = new JButton("Remove Directed Edge");
        removeDirectedEdgeButton.addActionListener(e ->
                editUserGraph(graphAction.REMOVE_DIRECTED_EDGE));

        // Sets up button to remove a vertex and its incoming/outgoing edges
        JButton removeVertexButton = new JButton("Remove Vertex");
        removeVertexButton.addActionListener(e -> editUserGraph(graphAction.REMOVE_VERTEX));

        // Sets up button to run BFS on current user graph
        JButton runBFSButton = new JButton("BFS");
        runBFSButton.addActionListener(e -> graphAlgorithm(graphAlgorithm.BFS));

        // Sets up button to run DFS on current user graph
        JButton runDFSButton = new JButton("DFS");
        runDFSButton.addActionListener(e -> graphAlgorithm(graphAlgorithm.DFS));

        // Sets up button to run Dijkstra's on current user graph
        JButton runDijkstraButton = new JButton("Dijkstra's");
        runDijkstraButton.addActionListener(e -> graphAlgorithm(graphAlgorithm.DIJKSTRA));

        // Sets up control panel and adds all buttons to it
        JPanel controlPanel = new JPanel();
        controlPanel.add(newGraphButton);
        controlPanel.add(addVertex);
        controlPanel.add(addDirectedEdgeButton);
        controlPanel.add(removeDirectedEdgeButton);
        controlPanel.add(removeVertexButton);
        controlPanel.add(runBFSButton);
        controlPanel.add(runDFSButton);
        controlPanel.add(runDijkstraButton);

        // Adds all components to the main frame and makes it visible
        mainFrame.add(controlPanel, BorderLayout.NORTH);
        mainFrame.add(view, BorderLayout.CENTER);
        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(true);

        // Set up result frame but do not make it visible yet
        resultFrame.add(resultView, BorderLayout.CENTER);
        resultFrame.setLocationByPlatform(true);
    }

    // Reset user graph to one with no vertices and edges
    private static void newGraph() {
        userGraph = new Graph();
        userGraphView.clear();
        userGraphView.setAttribute("ui.stylesheet", styleSheet);
    }

    // Function that edit the user graph depending on which button is pressed
    private static void editUserGraph(graphAction action) {
        // Resets all text fields
        uTextField1.setText("");
        vTextField1.setText("");
        uTextField2.setText("");
        vTextField2.setText("");
        singleVertex1.setText("");
        singleVertex2.setText("");
        weightTextField.setText("");

        // Panel for displaying the error message
        JDialog errorMessage = new JDialog(mainFrame, "Error", true);

        // Cases on the enum that is passed to it (which differs based on button)
        switch (action) {
            // Adding a vertex
            case ADD_VERTEX:
                // Shows a pop-up asking for inputs to add vertex
                int choiceAddVertex = JOptionPane.showConfirmDialog(null, addVertex,
                        "Input Value", JOptionPane.OK_CANCEL_OPTION);
                // If User clicked ok
                if (choiceAddVertex == JOptionPane.OK_OPTION) {
                    try {
                        // Gets user input
                        String label = singleVertex1.getText();
                        if (!userGraph.addVertex(label, false)) {
                            throw new IllegalArgumentException("Vertex is already in graph");
                        } else {
                            // If a vertex is successfully added into the User Graph. add it to the Graph Representation too
                            addVertexToMultiGraph(label);
                        }
                    } catch (IllegalArgumentException i) {
                        // Displays a popup that shows the error
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                    }
                }
                break;
            case ADD_DIRECTED_EDGE:
                // Shows a pop-up asking for inputs to add an edge
                int choiceAddEdge = JOptionPane.showConfirmDialog(null, addEdge, "Input Values",
                        JOptionPane.OK_CANCEL_OPTION);
                // If User clicked ok
                if (choiceAddEdge == JOptionPane.OK_OPTION) {
                    try {
                        // Gets user inputs
                        String u = uTextField1.getText();
                        String v = vTextField1.getText();
                        String w = weightTextField.getText();
                        // Checks if weight input is a proper integer input
                        int edgeWeight = Integer.parseInt(w);
                        if (!userGraph.addDirectedEdge(u, v, edgeWeight)) {
                            throw new IllegalArgumentException("Edge is already in graph");
                        } else {
                            // If an edge is added to the graph, add the corresponding vertices to Graph Representation
                            // if they are not in the graph already and then add the edge.
                            addVertexToMultiGraph(u);
                            addVertexToMultiGraph(v);
                            Edge e = userGraphView.addEdge(u + "." + v, u, v, true);
                            e.setAttribute("ui.label", w);
                        }
                    } catch (NumberFormatException n) {
                        JOptionPane.showMessageDialog(errorMessage, "Weight value is not an integer");
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                    }
                }
                break;
            case REMOVE_DIRECTED_EDGE:
                // Shows a pop-up asking for inputs to remove an edge
                int choiceRemoveEdge = JOptionPane.showConfirmDialog(null, removeEdge,
                        "Input Values", JOptionPane.OK_OPTION);
                // If User clicked ok
                if (choiceRemoveEdge == JOptionPane.OK_OPTION) {
                    try {
                        // Gets user inputs
                        String u = uTextField2.getText();
                        String v = vTextField2.getText();
                        if (!userGraph.removeDirectedEdge(u, v)) {
                            throw new IllegalArgumentException("Edge is not in graph");
                        } else {
                            // If an edge is successfully removed, remove it from the Graph Representation too.
                            userGraphView.removeEdge(u + "." + v);
                        }
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                    }
                }
                break;
            case REMOVE_VERTEX:
                // Shows a pop-up asking for inputs to remove a vertex
                int choiceRemoveVertex = JOptionPane.showConfirmDialog(null, removeVertex,
                        "Input Values",
                        JOptionPane.OK_CANCEL_OPTION);
                // If User clicked ok
                if (choiceRemoveVertex == JOptionPane.OK_OPTION) {
                    try {
                        // Gets user input
                        String label = singleVertex2.getText();
                        if (!userGraph.removeVertex(label)) {
                            throw new IllegalArgumentException("Vertex is not in graph");
                        } else {
                            // If a vertex is successfully removed, remove it from the Graph Representation too.
                            userGraphView.removeNode(label);
                        }
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                    }
                }
        }
    }

    // Helper method to add vertex to the User Graph representation
    private static void addVertexToMultiGraph(String label) {
        try {
            Node n = userGraphView.addNode(label);
            n.addAttribute("ui.label", label);
        } catch (IdAlreadyInUseException ignored) { }
    }

    // Function that calls a specific graph algorithm depending on button pressed and displays the result
    private static void graphAlgorithm(graphAlgorithm alg) {
        JDialog errorMessage = new JDialog(mainFrame, "Error", true);
        if (userGraph.size() == 0) {
            JOptionPane.showMessageDialog(errorMessage, "Graph is empty, silly!");
            return;
        }
        // Shows a pop-up asking for input
        int ok = JOptionPane.showConfirmDialog(null, source, "Input Values",
                JOptionPane.OK_CANCEL_OPTION);
        // If User clicks ok
        if (ok == JOptionPane.OK_OPTION) {
            String source = sourceTextField.getText();
            // Gets the input
            sourceTextField.setText("");
            // Cases on the enum that is passed to it
            switch (alg) {
                case BFS:
                    try {
                        // Sets the algorithm graph to the result from calling BFS
                        algorithmGraph = userGraph.bfs(source);
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                        return;
                    }
                    break;
                case DFS:
                    try {
                        // Sets the algorithm graph to the result from calling DFS
                        algorithmGraph = userGraph.dfs(source);
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                        return;
                    }
                    break;
                case DIJKSTRA:
                    try {
                        // Sets the algorithm graph to the result from calling Dijkstra
                        algorithmGraph = userGraph.dijkstra(source);
                    } catch (IllegalArgumentException i) {
                        JOptionPane.showMessageDialog(errorMessage, i.getMessage());
                        return;
                    }
            }
            // Updates the result representation
            drawForest(alg);

            // Shows the result
            resultFrame.setVisible(true);
        }
    }

    // Helper that draws the tree representation of the resulting graph
    private static void drawForest(graphAlgorithm alg) {
        // Resets the result the graph
        algorithmGraphView.clear();
        algorithmGraphView.setAttribute("ui.stylesheet", styleSheet);
        spriteManager = new SpriteManager(algorithmGraphView);
        Set<Map.Entry<String, Vertex>> roots = algorithmGraph.getRoots();
        // Splits the result screen into equal sized segments horizontally (1 for each rooted tree)
        int xSegment = SCREEN_WIDTH / roots.size();
        // Splits the result screen into equal sized segments vertically (1 for each level in the tree)
        // Divide by number of nodes of the graph since height of tree is upper bounded by that
        ySegment = SCREEN_HEIGHT / algorithmGraph.size();
        // Keeps track of which segment the algorithm is on
        int count = 0;
        for (Map.Entry<String, Vertex> r : roots) {
            drawTree(count * xSegment, (count + 1) * xSegment, 0, r.getKey(), null, alg);
            count++;
        }
    }

    // Recursive helper to draw a single tree
    private static void drawTree(int startX, int endX, int level, String currNode, String parent, graphAlgorithm alg) {
        // Adds current vertex to the middle of startX and endX (horizontal) and the specific ySegment (based on level)
        Node n = algorithmGraphView.addNode(currNode);
        n.setAttribute("ui.label", currNode);
        n.setAttribute("xy", (startX + endX) / 2, (-level * ySegment));
        Vertex curr = algorithmGraph.getVertex(currNode);
        // If this is drawing a DFS tree, display the start and finish times.
        if (alg == graphAlgorithm.DFS) {
            Sprite times = spriteManager.addSprite(currNode + "times");
            times.attachToNode(currNode);
            times.setPosition(15, 0, 45);
            times.setAttribute("ui.label", curr.getStart() + "/" + curr.getFinish());
        }
        // If the current node is not a root node, add an edge from its parent to itself.
        if (parent != null) {
            Edge e = algorithmGraphView.addEdge(parent + "." + currNode, parent, currNode, true);
            // If this is drawing the shortest path tree, display the edge weights.
            if (alg == graphAlgorithm.DIJKSTRA) {
                e.setAttribute("ui.label", algorithmGraph.getWeight(parent, currNode));
            }
        }
        Set<String> children = curr.getNeighbors();
        if (children.size() != 0) {
            // Splits the section between startX and endX into even horizontal segments (1 for each child of the current vertex)
            int xSegment = (endX - startX) / children.size();
            // Keeps track of which segment the algorithm is on
            int count = 0;
            for (String s : children) {
                // Recursively calls drawTree on this vertex's children
                drawTree(startX + count * xSegment, startX + (count + 1) * xSegment,
                        level + 1, s, currNode, alg);
                count++;
            }
        }
    }

}

