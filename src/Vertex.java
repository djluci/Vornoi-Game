/**
 * Author: Duilio Lucio
 * File: Vertex
 * Date: November 20, 2025
 * Defines a graph vertex that stores and manages its incident edges and provdes access to adjacent vertices
 */

import java.util.ArrayList;

public class Vertex {
    
    // Storing incident edges
    private final ArrayList<Edge> incidentEdges;

    /**
     * Initializes a vertex
     */
    public Vertex() {
        this.incidentEdges = new ArrayList<>();
    } 

    /**
     * REturns the edge which connects this vertex and the given Vertex vertex if such an Edge exits
     * otherwise return null
     * 
     * if multiple edges are found, then return first one found
     * @param vertex
     * @return
     */
    public Edge getEdgeTo(Vertex vertex) {
        if (vertex == null) {
            return null;
        }
        for (Edge e : incidentEdges) {
            Vertex other = e.other(this);
            if (other == vertex) {
                return e;
            }
        }
        return null;
    }

    /**
     * Adds the specified Edge edge to the ArrayList of Edges incident to this Vertex
     * NOTE: this should not do anything else. Any other book-keeping will be handled in the Graph class.
     * @param edge
     */
    public void addEdge(Edge edge) {
        if (edge == null) {
            return;
        }
        // Avoids duplicates if exact edge is already stored
        if (!incidentEdges.contains(edge)) {
            incidentEdges.add(edge);
        }
    }

    /**
     * Removes this Edge from the ArrayList of Edges incident to this Vertex.
     * Returns true if this Edge was connected to this Vertex, otherwise returns false
     * 
     * NOTE: this should not do anything else. Any other book-keeping will be handled in the Graph class.
     * @param edge
     * @return
     */
    public boolean removeEdge(Edge edge) {
        if (edge == null) {
            return false;
        }  
        return incidentEdges.remove(edge);
    }

    /**
     * Returns an arrayList of all the vertices adjacent ot this vertex
     * @return
     */
    public ArrayList<Vertex> adjacentVertices() {
        ArrayList<Vertex> neighbors = new ArrayList<>();
        for (Edge e : incidentEdges) {
            Vertex other = e.other(this);
            if (other != null) {
                neighbors.add(other);
            }
        }
        return neighbors;
    }

    /**
     * Returns an arrayList of all the Edges incident to this Vertex
     * @return
     */
    public ArrayList<Edge> incidentEdges() {
        return new ArrayList<>(incidentEdges);
    }
}
