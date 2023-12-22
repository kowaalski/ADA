package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.Map;
import java.util.*;

public class EnemyPathFinder {

    private static float getWalkableNodeValue(MapNode n, MapNode destination) {
        ArrayList<Tower> towers = Game.getInstance().getMap().getTowers();
        int inRangeTowers = 0;
        for (Tower t : towers) {
            if (t.getPosition().distance(n.getPosition()) < 10) {
                inRangeTowers++;
            }
        }

        float distanceFactor = n.getPosition().distance(destination.getPosition());
        float damageFactor = inRangeTowers;

        return 0.5f * distanceFactor + 0.5f * inRangeTowers;
    }

    public static ArrayList<MapNode> findBestPath(MapNode start, ArrayList<MapNode> walkableNodes,
            ArrayList<MapNode> endingPoints) {
        Map<MapNode, Integer> fValues = new HashMap<>();
        PriorityQueue<MapNode> openSet = new PriorityQueue<>(Comparator.comparingInt(fValues::get));
        Map<MapNode, MapNode> cameFrom = new HashMap<>();
        Map<MapNode, Integer> gScore = new HashMap<>();
        Map<MapNode, Integer> fScore = new HashMap<>();

        for (MapNode node : walkableNodes) {
            gScore.put(node, Integer.MAX_VALUE);
            fScore.put(node, Integer.MAX_VALUE);
        }

        gScore.put(start, 0);
        fScore.put(start, heuristicCostEstimate(start, endingPoints.get(0)));
        fValues.put(start, fScore.get(start));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            MapNode current = openSet.poll();

            if (endingPoints.contains(current)) {
                return reconstructPath(cameFrom, current);
            }

            for (MapNode neighbor : current.getNeighbors()) {
                if (!walkableNodes.contains(neighbor))
                    continue;

                int tentativeGScore = gScore.get(current) + distBetween(current, neighbor);

                if (tentativeGScore < gScore.get(neighbor)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) + heuristicCostEstimate(neighbor, endingPoints.get(0)));
                    fValues.put(neighbor, fScore.get(neighbor));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // return an empty path if there is no path
    }

    private static int distBetween(MapNode a, MapNode b) {
        // Use the distance method from your Point2D class
        return (int) a.getPosition().distance(b.getPosition());
    }

    private static int heuristicCostEstimate(MapNode a, MapNode b) {
        // Use the distance method from your Point2D class
        return (int) a.getPosition().distance(b.getPosition());
    }

    private static ArrayList<MapNode> reconstructPath(Map<MapNode, MapNode> cameFrom, MapNode current) {
        ArrayList<MapNode> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current);
        }
        return totalPath;
    }
}
