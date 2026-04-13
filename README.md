# Voronoi Strategy Engine: Graph-Based Competition

An advanced Java-based simulation and AI framework exploring strategic location optimization. This project models how competing entities (like retail chains or political parties) choose optimal locations on a weighted graph to maximize their "Voronoi influence" over surrounding districts.

## 🚀 Key Technical Features

### 1. Robust Graph Framework (`/src`)
* **Custom Adjacency List Architecture:** Built a high-performance graph structure from the ground up using `Vertex`, `Edge`, and `Graph` classes.
* **Dijkstra’s Algorithm:** Implemented Dijkstra with a `PriorityQueue` for efficient single-source shortest path calculations, optimized for sparse graph topologies.
* **Floyd-Warshall Pre-computation:** Utilized in the `VoronoiGraph` subclass to pre-calculate all-pairs shortest paths, allowing $O(1)$ distance lookups during AI evaluation cycles.

### 2. Strategic AI Implementations (`/ext`)
* **Greedy Heuristics:** A baseline player that optimizes for immediate value capture ($O(V)$ complexity).
* **One-Step Lookahead (Minimax Lite):** Models an "optimal opponent" response to evaluate move quality by simulating the board state one turn into the future.
* **Monte Carlo Simulation (MCTS-inspired):** My primary extension. For every legal move, the agent performs $N$ stochastic rollouts of the game's future. It selects the move with the highest average terminal score, effectively capturing long-term territorial advantages that heuristics miss.

## 📊 Experimental Results
The simulation framework includes a headless testing suite to run large batches of games. Results from a 100-node graph with 10% edge density:

| Player Matchup | Win Rate (P1) | Win Rate (P2) | Analysis |
| :--- | :---: | :---: | :--- |
| **LookAhead** vs. Greedy | 70% | 30% | Lookahead effectively "blocks" high-value greedy expansions. |
| **Monte Carlo** vs. Greedy | 100% | 0% | Stochastic rollouts consistently identify superior long-term positions. |

## 🛠️ Technical Deep Dive

### The Voronoi Partition Logic
The game state is determined by a Voronoi partition. For every vertex $v$ in the graph $G$, the owner is defined using the following distance minimization:

$$
Owner(v) = \text{argmin}_{p \in \text{Players}} (\text{dist}(p.\text{token}, v))
$$

This requires constant re-calculation of the graph's influence map. I implemented a distance cache in the `VoronoiGraph` class to ensure the simulation remains performant even with high-frequency AI decision-making.

### Monte Carlo Rollouts
The `VoronoiMonteCarloPlayer` implements a sophisticated decision loop:
1. **Candidate Filtering:** Identifies all vertices currently without tokens.
2. **Stochastic Sampling:** For each candidate, it simulates a "random future" for a fixed number of steps ($S$).
3. **Score Approximation:** Evaluates the resulting board state using a custom `evaluateMyScore` function that calculates influence based on the shortest-path distances from all player-owned tokens.

## 💻 How to Run
1. **Compile the engine:**
   ```bash
   javac *.java
2. **Launch the Simulation:
   ```bash
   java VoronoiGame
