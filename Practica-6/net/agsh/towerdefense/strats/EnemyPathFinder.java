package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;

public class EnemyPathFinder {

    private static float getWalkableNodeValue(MapNode n, MapNode destination) {
        ArrayList<Tower> towers = Game.getInstance().getMap().getTowers();
        int inRangeTowers = 0;
        for(Tower t : towers) {
            if(t.getPosition().distance(n.getPosition()) < 10) {
                inRangeTowers++;
            }
        }

        float distanceFactor = n.getPosition().distance(destination.getPosition());
        float damageFactor = inRangeTowers;

        return 0.5f * distanceFactor + 0.5f * inRangeTowers;
    }

    public static ArrayList<MapNode> findBestPath(MapNode position, ArrayList<MapNode> walkableNodes,
                                                  ArrayList<MapNode> endingPoints) {
        // This is just a (bad) example. Replace ALL of this with your own code.
        // This is a greedy algorithm that (badly) chooses the next node to go to as the closest one to the destination.

        Game g = Game.getInstance();
        Map m = g.getMap();

        MapNode origin = position;
        MapNode destination = endingPoints.get(0);

        // select index 1 in "node values" dropdown in the GUI to see the values of the nodes
        for(MapNode n : walkableNodes) {
            n.setValue(1, getWalkableNodeValue(n, destination));
        }

        ArrayList<MapNode> path = new ArrayList<>();
        MapNode current = origin;
        while(current != destination) {
            MapNode best = null;
            for(MapNode n : current.getNeighbors()) {
                if(walkableNodes.contains(n) && !path.contains(n)) {
                    if(best == null || getWalkableNodeValue(n, destination) < getWalkableNodeValue(best, destination)) {
                        best = n;
                    }
                }
            }

            if(best != null) { // to avoid infinite loops
                path.add(best);
                current = best;
            } else {
                return path;
            }
        }

        return path;
    }
}
