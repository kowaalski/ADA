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

            float dist = entity1Position.distance(entity2Position);

            float totalRadio = entity1Radius + entity2Radius;

            if (totalRadio > dist) {
                isCollide = true;
            }

        } catch (Exception e) {
            return false;
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

    public static boolean isInsideMatriz(Point2D position, Map map) {
        return !(position.x < 0) && !(position.x > map.getSize().x) && !(position.y < 0)
                && !(position.y > map.getSize().y);

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
        // for (MapNode nodeW : walkableNodes) {
        // if (!(position.distance(nodeW.getPosition()) < radius)) {
        // return true;
        // }
        // }

        for (MapNode nodeW : walkableNodes) {
            if (collide(position, radius, nodeW.getPosition(), 10)) {
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
        candidates.sort((o1, o2) -> Float.compare(o2.getValue(2), o1.getValue(2))); // Ordeno de mayor a menor
        return candidates;
    }

    public static boolean isFeasible(MapNode node, Map map, ArrayList<Tower> listTowers,
            ArrayList<Tower> towersAlreadyPositioned) {
        int index = 0;
        boolean collideTower = false;
        boolean collidesObstacles = false;

        for (int i = 0; i < listTowers.size(); i++) {
            Tower tower = listTowers.get(i);
            if (!isInsideMatriz(node.getPosition(), map)) {
                System.out.println("No se puede colocar la torre porque se sale del mapa");
                continue;
            }

            for (Tower tower2 : towersAlreadyPositioned) {
                if (collide(node.getPosition(), tower.getRadius(), tower2.getPosition(),
                        tower2.getRadius())) {
                    System.out.println("No se puede colocar la torre porque colisiona con otratorre");
                    collideTower = true;
                    break;
                }
            }

            if (collideTower) {
                collideTower = false;
                continue;
            }

            for (Obstacle obs : map.getObstacles()) {
                if (collide(node.getPosition(), tower.getRadius(), obs.getPosition(),
                        obs.getRadius())) {
                    System.out.println("No se puede colocar la torre porque colisiona con un obstaculo");
                    collidesObstacles = true;
                    continue;
                }
            }
            if (collidesObstacles) {
                collidesObstacles = false;
                continue;
            }

            if (collidesWalkablesNodes(node.getPosition(), tower.getRadius(),
                    map.getWalkableNodes())) {
                System.out.println("No se puede colocar la torre porque colisiona con un nodo transitable");
                continue;
            }
            // if (!isInsideTable(tower.getPosition(), tower.getRadius(), map)) {
            // if (oldPosition != null) {
            // tower.setPosition(oldPosition);
            // } else {
            // tower.setPosition(new Point2D());
            // }
            // System.out.println("No se puede colocar la torre porque se sale del mapa");
            // continue;
            // }

            // if (collidesObstacles(tower.getPosition(), tower.getRadius(),
            // map.getObstacles())) {
            // if (oldPosition != null) {
            // tower.setPosition(oldPosition);
            // } else {
            // tower.setPosition(new Point2D());
            // }
            // System.out.println("No se puede colocar la torre porque colisiona con un
            // obstaculo");
            // continue;
            // }

            // if (collidesTowers(tower.getPosition(), tower.getRadius(), listTowers)) {
            // if (oldPosition != null) {
            // tower.setPosition(oldPosition);
            // } else {
            // tower.setPosition(new Point2D());
            // }
            // System.out.println("No se puede colocar la torre porque colisiona con otra
            // torre");
            // continue;
            // }

            // if (collidesWalkablesNodes(tower.getPosition(),
            // tower.getRadius() +
            // Game.getInstance().getParam(Config.Parameter.ENEMY_RADIUS_MAX),
            // map.getWalkableNodes())) {
            // if (oldPosition != null) {
            // tower.setPosition(oldPosition);
            // } else {
            // tower.setPosition(new Point2D());
            // }
            // System.out.println("No se puede colocar la torre porque colisiona con un nodo
            // transitable");
            // continue;
            // }

            index = i;
            tower.setPosition(node.getPosition());
            // System.out.println("INDEX:" + index);
            Tower solTower = listTowers.get(index);
            Tower initialTower = listTowers.get(0);
            listTowers.set(0, solTower);
            listTowers.set(index, initialTower);
            System.out.println(solTower.toString());
            return true;
        }

        return false;
    }

    public static boolean isFactible(MapNode celda, Tower tower, Map map, ArrayList<Tower> towersAlreadyPositioned) {

        if (!isInsideTable(celda.getPosition(), tower.getRadius(), map)) {
            System.out.println("No se puede colocar la torre porque se sale del mapa");
            return false;
        }

        for (Tower tower2 : towersAlreadyPositioned) {
            if (collide(celda.getPosition(), tower.getRadius(), tower2.getPosition(),
                    tower2.getRadius())) {
                System.out.println("No se puede colocar la torre porque colisiona con otratorre");
                return false;
            }
        }

        for (Obstacle obs : map.getObstacles()) {
            if (collide(celda.getPosition(), tower.getRadius(), obs.getPosition(),
                    obs.getRadius())) {
                System.out.println("No se puede colocar la torre porque colisiona con un obstaculo");
                return false;
            }
        }

        if (collidesWalkablesNodes(celda.getPosition(), tower.getRadius(),
                map.getWalkableNodes())) {
            System.out.println("No se puede colocar la torre porque colisiona con un nodo transitable");
            return false;
        }

        return true;

    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        System.out.println("//////////////////////////////");
        setValuesToNodes(map);

        // boolean noPositioned = true;
        ArrayList<MapNode> candidates = getListCandidates(map);
        // ArrayList<MapNode> candidates = map.getNodesList();
        ArrayList<Tower> solution = new ArrayList<>();

        // int tamCandidates = candidates.size();
        // int tamTowers = towers.size();

        // int i = 0;
        // while (tamTowers > 0 && tamCandidates > 0) {

        // Tower tower = towers.get(i);
        // int j = 0;

        // while (tamCandidates > 0 && noPositioned) {

        // MapNode c = candidates.get(j);
        // if (isFactible(c, tower, map, solution)) {
        // tower.setPosition(c.getPosition());
        // solution.add(tower);
        // noPositioned = false;
        // }

        // candidates.remove(j);
        // tamCandidates--;
        // j++;
        // }
        // towers.remove(i);
        // tamTowers--;
        // i++;

        // }
        while (towers.size() != 0 && !candidates.isEmpty()) {
            MapNode c = candidates.get(0);

            if (isFeasible(c, map, towers, solution)) {
                solution.add(towers.get(0));
                towers.remove(0);
            }

            candidates.remove(0);
        }

        return solution;
    }
}
