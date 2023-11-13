package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;

public class TowerPlacer {

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        MapNode[][] grid = map.getNodes();
        Random random = new Random();

        ArrayList<Tower> placedTowers = new ArrayList<>();
        for (Tower tower : towers) {
            MapNode node = grid[random.nextInt(grid.length)][random.nextInt(grid[0].length)];
            tower.setPosition(node.getPosition());
            placedTowers.add(tower);
        }

        return placedTowers;
    }
}
