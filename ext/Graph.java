/**
 * Author: Duilio Lucio
 * File: Graph
 * Date: November 20, 2025
 * Implements an undirected graph structure w/ functionality to add/remove
 * vertices and edges, load graphs from files, and compute shortest paths using
 * Dijkstra's algorithm
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Random;

public class Graph {
    
    // -------------------- Fields --------------------

    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;

    // -------------------- Constructors --------------------

    /**
     * Equivalent to Graph(0)
     */
    public Graph() {
        this(0);
    }

    /**
     * Equivalent to Graph(n, 0.0)
     * @param n
     */
    public Graph(int n) {
        this(n, 0.0);
    }

    /**
     * Creates a Graph of n vertices where each pair of vertices has an edge between them of distance 1
     * w/ probability given by the supplied probability
     * @param n
     * @param probability
     */
    public Graph(int n, double probability) {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        // Creates n vertices
        for (int i = 0; i < n; i++) {
            vertices.add(new Vertex());
        }
        // Randomly add edges between each pair of vertices(undirected)
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            for(int j = i + 1; j < n; j++) {
                if (random.nextDouble() < probability) {
                    Edge e = new Edge(vertices.get(i), vertices.get(j), 1.0);
                    edges.add(e);
                    vertices.get(i).addEdge(e);
                    vertices.get(j).addEdge(e);
                }
            }
        }
    }

    /**
     * Creates a graph based on the specific vertices and edges specified in a text file
     * @param filename
     */
    public Graph( String filename ) {
        try {
            //Setup for reading the file
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            //Get the number of vertices from the file and initialize that number of vertices
            vertices = new ArrayList() ;
            Integer numVertices = Integer.valueOf( br.readLine().split( ": " )[ 1 ] ) ;
            for ( int i = 0 ; i < numVertices ; i ++ ) {
                vertices.add( new Vertex() );
            }

            //Read in the edges specified by the file and create them
            edges = new ArrayList() ; //If you used a different data structure to store Edges, you'll need to update this line
            String header = br.readLine(); //We don't use the header, but have to read it to skip to the next line
            //Read in all the lines corresponding to edges
            String line = br.readLine();
                while(line != null){
                    //Parse out the index of the start and end vertices of the edge
                    String[] arr = line.split(",");
                    Integer start = Integer.valueOf( arr[ 0 ] ) ;
                    Integer end = Integer.valueOf( arr[ 1 ] ) ;

                    //Make the edge that starts at start and ends at end with weight 1
                    Edge edge = new Edge( vertices.get( start ) , vertices.get( end ) , 1. ) ;
                    //Add the edge to the set of edges for each of the vertices
                    vertices.get( start ).addEdge( edge ) ;
                vertices.get( end ).addEdge( edge ) ;
                //Add the edge to the ArrayList of edges in the graph
                this.edges.add( edge );

                //Read the next line
                line = br.readLine();
            }
            // call the close method of the BufferedReader:
            br.close();
            }
            catch(FileNotFoundException ex) {
            System.out.println("Graph constructor:: unable to open file " + filename + ": file not found");
            }
            catch(IOException ex) {
            System.out.println("Graph constructor:: error reading file " + filename);
        }
    }
    

    // -------------------- Accessors --------------------
    
    /**
     * Returns the number of vertices
     * @return
     */
    public int size() {
        return vertices.size();
    }

    /**
     * Returns an ArrayList object that can be used to iterate over the vertices(returning underlying structure)
     * @return
     */
    public ArrayList<Vertex> getVertices() {
        return vertices;
    }

    /**
     * Returns an ArrayList object that iterates over the edges
     * @return
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Returns the Vertex at index
     * @param index
     * @return
     */
    public Vertex getVertex(int index) {
        return vertices.get(index);
    }

    // -------------------- Mutating Methods --------------------

    /**
     * Creates a new Vertex, adds it to the Graph, and returns new vertex
     * @return
     */
    public Vertex addVertex() {
        Vertex v = new Vertex();
        vertices.add(v);
        return v;
    }

    /**
     * Creates a new Edge, adds it to the GRaph (Endpoints need to be aware of new edge), and return edge
     * @param u
     * @param v
     * @param distance
     * @return
     */
    public Edge addEdge(Vertex u, Vertex v, double distance) {
        if (u == null || v == null) {
            return null;
        }
        Edge e = new Edge(u, v, distance);
        edges.add(e);
        u.addEdge(e);  
        v.addEdge(e);  
        return e;
    }

    /**
     * REturns the Edge between u and v if such an Exists otherwise returns null
     * @param u
     * @param v
     * @return
     */
    public Edge getEdge(Vertex u, Vertex v) {
        if (u == null || v == null) {
            return null;
        }
        return u.getEdgeTo(v);
    }

    /**
     * If the given Vertex vertex is in this Graph, removes it and returns True.
     * Otherwise, return false
     * @param vertex
     * @return
     */
    public boolean remove(Vertex vertex) {
        if (vertex == null || !vertices.contains(vertex)) {
            return false;
        }
        // Copy incidentEdges, so list isn't modified when interacting
        ArrayList<Edge> inc = vertex.incidentEdges();
        for (Edge e : inc) {
            remove(e); // remove(e) will clean up properly
        }
        return vertices.remove(vertex);
    }

    /**
     * If the given Edge is in the Graph, removes it and returns true
     * Otherwise returns false
     * @param edge
     * @return
     */
    public boolean remove(Edge edge) {
        if (edge == null || !edges.contains(edge)) {
            return false;
        }
        // Remove from endpoints
        Vertex[] vs = edge.vertices();
        Vertex u = vs[0];
        Vertex v = vs[1];
        if (u != null) {
            u.removeEdge(edge);
        }
        if (v != null) {
            v.removeEdge(edge);
        }
        // Remove from graph edge list
        return edges.remove(edge);
    }

     // -------------------- Dijkstra's Algorithm --------------------
    
    /**
     * Uses Dijkstra's Algorithm to compute minimal distance in this Graph from the given Vertex source
     * to all other Vertices in the graph.
     * The HashMap returned maps each Vertex to its distance from the source.
     * @param source
     * @return
     */
    public HashMap<Vertex, Double> distanceFrom(Vertex source) {
        HashMap<Vertex, Double> dist = new HashMap<>();
        HashSet<Vertex> visited = new HashSet<>();

        // Initialize distances, infinity for all and 0 for source
        for (Vertex v : vertices) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);
        // Priority queue ordered by current distance in "dist"
        PriorityQueue<Vertex> pq = new PriorityQueue<>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex a, Vertex b) {
                return Double.compare(dist.get(a), dist.get(b));
            }
        });
        pq.add(source);
        while (!pq.isEmpty()) {
            Vertex u = pq.poll();
            // If vertix already finalized then skip
            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);
            double du = dist.get(u);
            if (du == Double.POSITIVE_INFINITY) {
                break; // remaining vertices are unreachable
            }
            // Relax edges from u
            for (Edge e : u.incidentEdges()) {
                Vertex v = e.other(u);
                if (v == null || visited.contains(v)) {
                    continue;
                }
                double alt = du + e.distance();
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    pq.add(v); // re-insert v with updated priority
                }
            }
        }
        return dist;
    }
}
