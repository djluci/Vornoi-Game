/**
 * Author: Duilio Lucio
 * File: VoronoiLookAheadPlayer
 * Date: November 28, 2025
 * Implements a Vornoi-based game player taht evaluates future board states by looking ahead 
 * to maximize its territorial advantage when choosing moves.
 */
import java.util.ArrayList;

public class VoronoiLookAheadPlayer extends VoronoiPlayerAlgorithm{
    
    // Stores the list of all vertices of the current graph, dont have to keep calling graph.getVertices()
    private ArrayList<Vertex> vertices;

    /**
     * Constructs that takes current vornoiGraph as an argument,
     * set graph field in the parent, and store all vertices of the graph into vertices
     * @param g
     */
    public VoronoiLookAheadPlayer(VoronoiGraph g) {
        super(g);
        this.vertices = graph.getVertices();
    }

    /**
     * Overrides the abstract method used by the game engine to ask
     * this player which vertex.
     */
    public Vertex chooseVertex(int playerIndex, int numRemainingTurns) {

        // Current tokens for each player
        ArrayList<Vertex> myTokens = new ArrayList<>();
        ArrayList<Vertex> oppTokens = new ArrayList<>();

        // Loop over all vertices and classify tokens
        for (Vertex v : vertices) {
            if (graph.hasToken(v)) {
                Integer owner = graph.getCurrentOwner(v);
                if (owner != null) {
                    if (owner == playerIndex) {
                        myTokens.add(v);
                    }
                    else {
                        oppTokens.add(v);
                    }
                }
            }
        }

        // Stores 
        Vertex bestVertex = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        // Loop through all vertices as potential moves
        for (Vertex candidate : vertices) {
            if (graph.hasToken(candidate)) {
                continue; // already has a token
            }
            // temporarily add candidate vertex into myTokens, choosing
            myTokens.add(candidate);

            double worstScore = Double.POSITIVE_INFINITY;
            boolean oppHasMove = false;

            // Opponent's possible replies, skip if same/alr has token
            for (Vertex oppCandidate : vertices) {
                if (oppCandidate == candidate) {
                    continue;
                }
                if (graph.hasToken(oppCandidate)) {
                    continue;
                }

                // Mark, and add to oppTokens by placing token at oppCandidate
                oppHasMove = true;
                oppTokens.add(oppCandidate);

                // Compute total value, after tokens been places
                double myScore = evaluateMyScore(myTokens, oppTokens);

                // update score, if outcome gives lower score than previous opp. replies
                if (myScore < worstScore) {
                    worstScore = myScore;
                }

                // Remove last element we added to oppTokens for next simulation
                oppTokens.remove(oppTokens.size() - 1);
            }
            // if opponent has no legal move(late game), only evaluate player's move
            if (!oppHasMove) {
                worstScore = evaluateMyScore(myTokens, oppTokens);
            }

            // Undo hypothetical move, reset state
            myTokens.remove(myTokens.size() - 1);

            // if candidate's worst-case score is better than current bestscore
            // update bestScore to new value and bestVertex to candidate
            if (worstScore > bestScore) {
                bestScore = worstScore;
                bestVertex = candidate;
            }
        }

        return bestVertex;
    }

    /**
     * Compute total value for a given configuration of tokens
     * myTokens = vertices w/ our tokens
     * OppTokens = vertices w/ opponents tokens
     * @param myTokens
     * @param oppTokens
     * @return
     */
    private double evaluateMyScore(ArrayList<Vertex> myTokens, ArrayList<Vertex> oppTokens) {

        // Accumulates vertices values
        double score = 0.0;

        // Loop through every vertex in the graph
        for(Vertex v : vertices) {
            // initialize distances and check if the distance isn't null and is smaller than current dMy, update dMy
            double dMy = Double.POSITIVE_INFINITY;
            for(Vertex s : myTokens) {
                Double d = graph.getDistance(s, v);
                if (d != null && d < dMy) {
                    dMy = d;
                }
            }

            // initialize distance and check if the distance isn't null and is smaller than current dOpp, update dOpp
            double dOpp = Double.POSITIVE_INFINITY;
            for (Vertex s : oppTokens) {
                Double d = graph.getDistance(s, v);
                if (d != null && d < dOpp) {
                    dOpp = d;
                }
            }

            // if both distances are infinity, skip it
            if (dMy == Double.POSITIVE_INFINITY && dOpp == Double.POSITIVE_INFINITY) {
                continue;
            }
            // Value associated w/ vertex v
            int value = graph.getValue(v);

            // if opp can't reach v or if we're closer, we own the vertex and get the full val
            if (dOpp == Double.POSITIVE_INFINITY || dMy < dOpp) {
                score += value;
            }
            // if both distances are equal then split value
            else if (dMy == dOpp) {
                score += value * 0.5;
            }
            // if opp is closer, return nothing
        }
        return score;
    }
}
