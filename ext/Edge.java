/**
 * Author: Duilio Lucio
 * File: Edge
 * Date: November 20, 2025
 * Represents an undirected edge connecting two vertices with an associated distance and methods
 * to inspect its endpoints.
 */

public class Edge {
    
    // --------------- Fields ---------------
    private final Vertex u;
    private final Vertex v;
    private final double distance;
    

    /**
     * Constructs an Edge consisting of the two vertixes w/ a given distance
     * 
     * NOTE: all of the edges we'll be working with have distance 1, but you may want to explore edges with other distances as an extension
     * NOTE: this should not update any field for verticies v and u
     * @param u
     * @param v
     * @param distance
     */
    public Edge (Vertex u, Vertex v, double distance) {
        if (u == null || v == null) {
            throw new IllegalArgumentException("Vertices of an edge can not be null");
        }
        
        this.u = u;
        this.v = v;
        this.distance = distance;
    } 

    /**
     * REturns the distance of this edge
     * @return
     */
    public double distance() {
        return this.distance;
    }

    /**
     * If Vertex is one of the endpoints of this edge, returns the other end point
     * Otherwise returns null
     * @param vertex
     * @return
     */
    public Vertex other(Vertex vertex) {
        if (vertex == u) {
            return v;
        }
        else if (vertex == v) {
            return u;
        }
        else {
            return null;
        }
    }

    /**
     * Return an array of the two Vertices comprising this Edge. Order is arbitrary
     * @return
     */
    public Vertex[] vertices() {
        return new Vertex[]{u , v};
    }
}
