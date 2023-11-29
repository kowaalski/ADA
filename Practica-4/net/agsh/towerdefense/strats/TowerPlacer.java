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
        int value = 0;
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

        float dist = entity1Position.distance(entity2Position);

        float totalRadio = entity1Radius + entity2Radius;

        if (totalRadio > dist) {
            isCollide = true;
        }

        return isCollide;
    }

    public static boolean isInsideTable(Point2D position, float radius, Map map) {
        if (!(position.x < radius) && !(position.x > map.getSize().x - radius) && !(position.y < radius)
                && !(position.y > map.getSize().y - radius)) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean collidesTowers(Point2D position, float radius, ArrayList<Tower> towers) {
        for (Tower t : towers) {
            if (!(position.distance(t.getPosition()) < radius + t.getRadius())) {
                return true;
            }
        }
        return false;

    }

    public static boolean collidesObstacles(Point2D position, float radius, ArrayList<Obstacle> obstacles) {
        for (Obstacle o : obstacles) {
            if (!(position.distance(o.getPosition()) < radius + o.getRadius())) {
                return true;
            }
        }
        return false;

    }

    public static boolean collidesWalkablesNodes(Point2D position, float radius, ArrayList<MapNode> walkableNodes) {
        for (MapNode nodeW : walkableNodes) {
            if (!(position.distance(nodeW.getPosition()) < radius)) {
                return true;
            }
        }

        return false;

    }

    public static ArrayList<MapNode> getListCandidates(Map map) {
        for (MapNode n : map.getNodesList()) {
            if (!n.isWalkable()) {
                getNodeValue(n, map);
            }
        }
        ArrayList<MapNode> candidates = map.getNodesList();
        candidates.sort((o1, o2) -> Float.compare(o2.getValue(0), o1.getValue(0))); // Ordeno de mayor a menor
        return map.getNodesList();
    }

    public static boolean isFeasible(MapNode node, Map map, ArrayList<Tower> listTowers) {
        int index = 0;
        for (int i = 0; i < listTowers.size(); i++) {
            Tower tower = listTowers.get(i);
            Point2D oldPosition = tower.getPosition();
            tower.setPosition(node.getPosition());

            if (!isInsideTable(tower.getPosition(), tower.getRadius(), map)) {
                if (oldPosition != null) {
                    tower.setPosition(oldPosition);
                } else {
                    tower.setPosition(new Point2D());
                }
                System.out.println("No se puede colocar la torre porque se sale del mapa");
                continue;
            }

            if (collidesObstacles(tower.getPosition(), tower.getRadius(), map.getObstacles())) {
                if (oldPosition != null) {
                    tower.setPosition(oldPosition);
                } else {
                    tower.setPosition(new Point2D());
                }
                System.out.println("No se puede colocar la torre porque colisiona con un obstaculo");
                continue;
            }

            if (collidesTowers(tower.getPosition(), tower.getRadius(), listTowers)) {
                if (oldPosition != null) {
                    tower.setPosition(oldPosition);
                } else {
                    tower.setPosition(new Point2D());
                }
                System.out.println("No se puede colocar la torre porque colisiona con otra torre");
                continue;
            }

            if (collidesWalkablesNodes(tower.getPosition(),
                    tower.getRadius() + Game.getInstance().getParam(Config.Parameter.ENEMY_RADIUS_MAX),
                    map.getWalkableNodes())) {
                if (oldPosition != null) {
                    tower.setPosition(oldPosition);
                } else {
                    tower.setPosition(new Point2D());
                }
                System.out.println("No se puede colocar la torre porque colisiona con un nodo transitable");
                continue;
            }

            index = i;
            Tower solTower = listTowers.get(index);
            Tower initialTower = listTowers.get(0);
            listTowers.set(0, solTower);
            listTowers.set(index, initialTower);
            return true;
        }

        return false;
    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        System.out.println("//////////////////////////////");
        setValuesToNodes(map);

        ArrayList<MapNode> candidates = getListCandidates(map);
        ArrayList<Tower> solution = new ArrayList<>();

        while (towers.size() != 0 && !candidates.isEmpty()) {
            MapNode c = candidates.get(0);
            candidates.remove(0);

            if (isFeasible(c, map, towers)) {
                solution.add(towers.get(0));
                towers.remove(0);
            }
        }

        return solution;
    }
}
