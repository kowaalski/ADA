/*
#---------------------------------------------------------------------------------------------------------------------
#  EXPLICACIÓN: setValuesToNodes(Map map)
#---------------------------------------------------------------------------------------------------------------------
    La estrategia seguida ha sido intentar asignar mayor valor a las celdas que 
    tienen más nodos transitables cercas es decir que tengan más caminos. 
    
    Para ello he ido cogiendo nodo por nodo (celda) y comparando con cada nodo transitable
    si la distancia entre ellos es menor que el rango máximo de la tower con mayor rango, 
    de esta manera me evito asginar valores altos a celdas que realmente no se van a poder 
    colocar una tower ya que su rango se mete dentro de una nodo transitable. Si se cumple la condición pues le 
    sumo 1 al valor de la celda, sino no le sumo nada.

*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TowerPlacer {

    public static float getNodeValue(MapNode node, Map map) {
        return node.getValue(2);
    }

    // To set the value to the nodes (cells), I set a higher value to the cells that
    // have more near walkable nodes
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

    // To get if two entities collide
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

    // To get if the tower that we want to position is inside the table and also his
    // radius
    public static boolean isInsideTable(Point2D position, Map map, float radius) {
        return !(position.x < radius) && !(position.x > map.getSize().x - radius) && !(position.y < radius)
                && !(position.y > map.getSize().y - radius);

    }

    // To get if the tower that we want to position collides with a walkable node
    public static boolean collidesWalkablesNodes(Point2D position, float radius, ArrayList<MapNode> walkableNodes) {
        for (MapNode nodeW : walkableNodes) {
            if (collide(position, radius, nodeW.getPosition(), 10)) {
                // System.out.println("Colisiona con nodo caminable");
                return true;
            }
        }
        return false;

    }

    // To get if the tower that we want to position collides with an obstacle
    public static boolean collidesObstacles(Point2D position, float radius, ArrayList<Obstacle> obstacleList) {
        for (Obstacle obs : obstacleList) {
            if (collide(position, radius, obs.getPosition(), obs.getRadius())) {
                // System.out.println("Colisiona con obstaculo");
                return true;
            }

        }
        return false;
    }

    // To get if the tower that we want to position collides with a tower that we
    // already positioned
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

    // To order de cells per value in descendent, so we have in the first position
    // the best cells
    public static ArrayList<MapNode> getOrderNodesPerValue(Map map) {
        ArrayList<MapNode> candidates = map.getNodesList();
        candidates.sort((o1, o2) -> Float.compare(o2.getValue(2), o1.getValue(2))); // Ordeno de mayor a menor
        return candidates;
    }

    // To get the most powerful tower (the tower with higher value)
    public static Tower getKeyWithMaxValue(HashMap<Tower, Float> powerfulTowers) {
        Tower maxKey = null;
        Float maxValue = null;

        for (Tower key : powerfulTowers.keySet()) {
            Float value = powerfulTowers.get(key);
            if (maxValue == null || value.compareTo(maxValue) > 0) {
                maxKey = key;
                maxValue = value;
            }
        }

        return maxKey;
    }

    // To get the tower with the value that we want (in this case I set -69f), the
    // tower with this value is the tower that we have just positioned
    public static Tower getKeyByValue(HashMap<Tower, Float> map, Float value) {
        for (Tower key : map.keySet()) {
            if (value.equals(map.get(key))) {
                return key;
            }
        }
        return null;
    }

    public static boolean isFactible(MapNode node, Map map, HashMap<Tower, Float> powerfulTowers,
            ArrayList<Tower> towersAlreadyPositioned) {

        for (int i = 0; i < powerfulTowers.size(); i++) {
            Tower tower = getKeyWithMaxValue(powerfulTowers);

            if (!isInsideTable(node.getPosition(), map, tower.getRadius())
                    || collidesTowersAlreadyPositioned(node.getPosition(), tower.getRadius(), towersAlreadyPositioned)
                    || collidesObstacles(node.getPosition(), tower.getRadius(), map.getObstacles())
                    || collidesWalkablesNodes(node.getPosition(), tower.getRadius(), map.getWalkableNodes())) {
                continue;
            }

            tower.setPosition(node.getPosition());
            // I set -69f to the value of tower(at the hashmap) that we have just
            // positioned, to have the possibility to get the tower in the
            // caller function line 189 and also could remove from the hashmap)
            powerfulTowers.put(tower, -69f);
            return true;
        }

        return false;
    }

    // To asociate a value to a tower and get the most powerful tower in order
    // descendent
    public static HashMap<Tower, Float> getPowerfulTowers(ArrayList<Tower> listTowers) {
        HashMap<Tower, Float> TowerValue = new HashMap<>();

        // The same formula that we use in the practice 3
        for (Tower tower : listTowers) {
            float range = tower.getRange();
            float damage = tower.getDamage();
            float cooldown = tower.getCooldown();
            float dispersion = tower.getDispersion();
            float cost = tower.getCost();
            float score = ((range * damage) / cooldown + dispersion) - cost / 100;
            TowerValue.put(tower, score);
        }

        return TowerValue;

    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        setValuesToNodes(map);
        HashMap<Tower, Float> powerfulTowers = getPowerfulTowers(towers);

        ArrayList<MapNode> candidates = getOrderNodesPerValue(map);
        ArrayList<Tower> solution = new ArrayList<>();

        while (powerfulTowers.size() != 0 && !candidates.isEmpty()) {
            MapNode c = candidates.get(0);

            if (isFactible(c, map, powerfulTowers, solution)) {
                // To obtain the tower that we have just positioned
                Tower tower = getKeyByValue(powerfulTowers, -69f);
                solution.add(tower);
                powerfulTowers.remove(tower);
            }

            candidates.remove(0);
        }

        return solution;
    }
}
