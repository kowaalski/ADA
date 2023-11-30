/*
Sustituir este comentario por una explicación de la formula o procedimiento empleado para determinar el valor de una
celda (MapNode).
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;

public class TowerPlacer {

    public static float getNodeValue(MapNode node, Map map) {
        return node.getValue(2);
    }

    public static void setValuesToNodes(Map map) {
        float maxRangeEnemy = Game.getInstance().getParam(Config.Parameter.TOWER_RANGE_MAX);
        float value = 0;
        ArrayList<MapNode> listWalkableNodes = map.getWalkableNodes();
        ArrayList<MapNode> listNodes = map.getNodesList();

        for (int i = 0; i < listNodes.size(); i++) {
            MapNode n = listNodes.get(i);
            value = 0;
            if (!n.isWalkable()) {
                for (MapNode nodeW : listWalkableNodes) {
                    if (nodeW.getPosition().distance(n.getPosition()) < maxRangeEnemy) {
                        value = value + 1;
                    }

                }
            }
            n.setValue(2, value);
        }
    }

    public static boolean collide(Point2D entity1Position, float entity1Radius,
            Point2D entity2Position, float entity2Radius) {

        boolean isCollide = false;
        try {

            float dist = entity1Position.distance(entity2Position); // Por si entity2position es null

            float totalRadio = entity1Radius + entity2Radius;

            if (totalRadio > dist) {
                isCollide = true;
            }

        } catch (Exception e) {
            return false;
        }

        return isCollide;
    }

    public static boolean isInsideMatriz(Point2D position, Map map) {
        return !(position.x < 0) && !(position.x > map.getSize().x) && !(position.y < 0)
                && !(position.y > map.getSize().y);

    }

    public static boolean collidesWalkablesNodes(Point2D position, float radius, ArrayList<MapNode> walkableNodes) {
        for (MapNode nodeW : walkableNodes) {
            if (collide(position, radius, nodeW.getPosition(), 10)) {
                // System.out.println("Colisiona con nodo caminable");
                return true;
            }
        }
        return false;

    }

    public static boolean collidesObstacles(Point2D position, float radius, ArrayList<Obstacle> obstacleList) {
        for (Obstacle obs : obstacleList) {
            if (collide(position, radius, obs.getPosition(), obs.getRadius())) {
                // System.out.println("Colisiona con obstaculo");
                return true;
            }

        }
        return false;
    }

    public static boolean collidesTowersAlreadyPositioned(Point2D position, float radius,
            ArrayList<Tower> towersAlreadyPositioned) {
        for (Tower tower : towersAlreadyPositioned) {
            if (collide(position, radius, tower.getPosition(), tower.getRadius())) {
                // System.out.println("Colisiona con torre ya posicionada");
                return true;
            }
        }
        return false;
    }

    public static ArrayList<MapNode> getOrderNodesPerValue(Map map) {
        ArrayList<MapNode> candidates = map.getNodesList();
        candidates.sort((o1, o2) -> Float.compare(o2.getValue(2), o1.getValue(2))); // Ordeno de mayor a menor
        return candidates;
    }

    public static boolean isFactible(MapNode node, Map map, ArrayList<Tower> listTowers,
            ArrayList<Tower> towersAlreadyPositioned) {
        int index = 0;

        for (int i = 0; i < listTowers.size(); i++) {
            Tower tower = listTowers.get(i);

            if (!isInsideMatriz(node.getPosition(), map)
                    || collidesTowersAlreadyPositioned(node.getPosition(), tower.getRadius(), towersAlreadyPositioned)
                    || collidesObstacles(node.getPosition(), tower.getRadius(), map.getObstacles())
                    || collidesWalkablesNodes(node.getPosition(), tower.getRadius(), map.getWalkableNodes())) {
                continue;
            }

            index = i;
            tower.setPosition(node.getPosition());
            Tower solTower = listTowers.get(index);
            Tower initialTower = listTowers.get(0);
            listTowers.set(0, solTower);
            listTowers.set(index, initialTower);
            return true;
        }

        return false;
    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        setValuesToNodes(map);
        ArrayList<MapNode> candidates = getOrderNodesPerValue(map);
        ArrayList<Tower> solution = new ArrayList<>();

        while (towers.size() != 0 && !candidates.isEmpty()) {
            MapNode c = candidates.get(0);

            if (isFactible(c, map, towers, solution)) {
                solution.add(towers.get(0));
                towers.remove(0);
            }

            candidates.remove(0);
        }

        return solution;
    }
}
