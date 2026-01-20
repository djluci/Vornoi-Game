/**
 * Author: Duilio Lucio
 * File: VoronoiMonteCarloPlayer
 * Date: December 5, 2025
 * Simulates random futures from each candidate move and chooses the move with the best average outcome
 */

import java.util.ArrayList;
import java.util.Random;

/**
 * Monte Carlo Player for the Voronoi Game
 * 
 * Strategy
 * For each legal move (candidate vertex), run several random simulations of the rest of the game and estimate our expected final score
 */
public class VoronoiMonteCarloPlayer extends VoronoiPlayerAlgorithm{

    private ArrayList<Vertex> vertices;
    private Random rand;
    private int simulationsPerCandidate = 8; // rollouts per move
    private int randomStepsPerSimulation = 4; // the amount of future random pics to simulate

    /**
     * Constructor
     * @param g
     */
    public VoronoiMonteCarloPlayer(VoronoiGraph g) {
        super(g);
        this.vertices = graph.getVertices();
        this.rand = new Random();
    }

    public Vertex chooseVertex(int playerIndex, int numRemainingTurns) {
        // Build lists of current tokens
        ArrayList<Vertex> myTokens = new ArrayList<>();
        ArrayList<Vertex> oppTokens = new ArrayList<>();
        ArrayList<Vertex> available = new ArrayList<>();

        for (Vertex v : vertices) {
            if (graph.hasToken(v)) {
                Integer owner = graph.getCurrentOwner(v);
                if (owner != null) {
                    if (owner == playerIndex) {
                        myTokens.add(v);
                    } else {
                        oppTokens.add(v);
                    }
                }
            } else {
                available.add(v);
            }
        }

        Vertex bestVertex = null;
        double bestAverageScore = Double.NEGATIVE_INFINITY;

        // Try each available vertex as a candidate move
        for (Vertex candidate : available) {

            double totalScore = 0.0;

            // Run several random simulations for this candidate
            for (int sim = 0; sim < simulationsPerCandidate; sim++) {
                totalScore += simulateWithCandidate(candidate, myTokens, oppTokens, available, playerIndex);
            }

            double avgScore = totalScore / simulationsPerCandidate;

            if (avgScore > bestAverageScore) {
                bestAverageScore = avgScore;
                bestVertex = candidate;
            }
        }

        return bestVertex;
    }

    /**
     * Run one random simulation after we hypothetically choose "candidate".
     * We do not modify the real graph; we work with copies of token & available lists.
     */
    private double simulateWithCandidate(Vertex candidate, ArrayList<Vertex> myTokens, ArrayList<Vertex> oppTokens, ArrayList<Vertex> available, int playerIndex) {

        // Copy current state
        ArrayList<Vertex> mySimTokens  = new ArrayList<>(myTokens);
        ArrayList<Vertex> oppSimTokens = new ArrayList<>(oppTokens);
        ArrayList<Vertex> simAvailable = new ArrayList<>(available);

        // Apply our hypothetical move: add candidate to us, remove from available
        mySimTokens.add(candidate);
        simAvailable.remove(candidate);

        // Simulate a few more random future moves
        // Alternate: opponent, us, opponent, us, ...
        boolean oppTurn = true;
        int steps = Math.min(randomStepsPerSimulation, simAvailable.size());

        for (int step = 0; step < steps; step++) {
            if (simAvailable.isEmpty()) break;

            // pick a random available vertex
            int idx = rand.nextInt(simAvailable.size());
            Vertex v = simAvailable.remove(idx);

            if (oppTurn) {
                oppSimTokens.add(v);
            } else {
                mySimTokens.add(v);
            }
            oppTurn = !oppTurn;
        }

        // Evaluate our final score after this random rollout
        return evaluateMyScore(mySimTokens, oppSimTokens);
    }

    /**
     * Compute our total value for a given configuration of tokens.
     * myTokens  = vertices with our tokens
     * oppTokens = vertices with opponent tokens
     */
    private double evaluateMyScore(ArrayList<Vertex> myTokens, ArrayList<Vertex> oppTokens) {
        double score = 0.0;

        for (Vertex v : vertices) {
            double dMy = Double.POSITIVE_INFINITY;
            for (Vertex s : myTokens) {
                Double d = graph.getDistance(s, v);
                if (d != null && d < dMy) {
                    dMy = d;
                }
            }

            double dOpp = Double.POSITIVE_INFINITY;
            for (Vertex s : oppTokens) {
                Double d = graph.getDistance(s, v);
                if (d != null && d < dOpp) {
                    dOpp = d;
                }
            }

            // If no player can reach this vertex, skip it
            if (dMy == Double.POSITIVE_INFINITY && dOpp == Double.POSITIVE_INFINITY) {
                continue;
            }

            int value = graph.getValue(v);

            if (dOpp == Double.POSITIVE_INFINITY || dMy < dOpp) {
                score += value;            // we are strictly closer
            } else if (dMy == dOpp) {
                score += value * 0.5;      // tie: split
            }
            // else opponent is closer: contributes nothing to our score
        }

        return score;
    }
}

