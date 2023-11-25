/*
Sustituir este comentario por una explicación de la formula o procedimiento empleado para determinar el valor de una
celda (MapNode).
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;

import java.util.ArrayList;
import java.util.Comparator;

public class TowerPlacer {

    // Asigno valor al nodo en función de cuantos nodos transitables tiene cerca
    public static float getNodeValue(MapNode node, Map map) {
        ArrayList<MapNode> neighboringNodes = node.getNeighbors();
        float walkableNeighborsCount = 0;

        for (MapNode neighboringNode : neighboringNodes) {
            if (neighboringNode.isWalkable()) {
                walkableNeighborsCount++;
            } else { // Por si no es un nodo transitable miro si cerca de el alguno de sus vecinos
                     // esta cerca de un nodo transitable, por tanto miro los valores de alguno de
                     // sus vecinos, si alguno tiene un valor asignado es que esta cerca de un nodo
                     // transitable por tanto cogo su valor y lo divido entre 3, así en el tablero
                     // también tengo valores en los nodos que no están inmeidatamente junto a un
                     // camino
                ArrayList<MapNode> nn = neighboringNode.getNeighbors();
                for (MapNode n : nn) {
                    if (n.getValue(0) > 0) {
                        walkableNeighborsCount = walkableNeighborsCount + n.getValue(0) / 3;
                    }
                }
            }
        }
        node.setValue(0, walkableNeighborsCount);

        return walkableNeighborsCount;
    }

    // Asigno valores a los nodos en función de si tienen caminos trnasitables cerca
    public static void setValuesToNodes(Map map) {
        for (MapNode node : map.getNodesList()) {
            if (!node.isWalkable()) {
                continue;
            }

            ArrayList<MapNode> neighboringNodes = node.getNeighbors();
            int walkableNeighborsCount = 0;

            for (MapNode neighboringNode : neighboringNodes) {
                if (neighboringNode.isWalkable()) {
                    walkableNeighborsCount++;
                }
            }
            node.setValue(0, walkableNeighborsCount);
        }
    }

    // CON ENTIDAD A QUE NOS REFERIMOS A GOBLINS,TOWERS,OBSTACULOSS
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

    public static boolean isFeasible(MapNode node, Map map) {
        boolean isFeasible = true;
        ArrayList<Obstacle> obstacles = map.getObstacles();
        ArrayList<Tower> towers = map.getTowers();
        ArrayList<MapNode> neighboringNodes = node.getNeighbors();

        for (Obstacle obstacle : obstacles) {
            if (collide(node.getPosition(), node.getRadius(), obstacle.getPosition(),
                    obstacle.getRadius())) {
                isFeasible = false;
                break;
            }
        }

        for (Tower tower : towers) {
            if (collide(node.getPosition(), node.getRadius(), tower.getPosition(),
                    tower.getRadius())) {
                isFeasible = false;
                break;
            }
        }

        for (MapNode neighboringNode : neighboringNodes) {
            if (neighboringNode.isWalkable()) {
                isFeasible = false;
                break;
            }
        }

        return isFeasible;
    }

    public static ArrayList<Tower> placeTowers(ArrayList<Tower> towers, Map map) {
        System.out.println("//////////////////////////////");

        // for (MapNode n : map.getNodesList()) {
        // if (!n.isWalkable()) {
        // getNodeValue(n, map);
        // }
        // }

        // for (MapNode n : map.getNodesList()) {
        // System.out.println(n.getValue(0));
        // }

        ArrayList<MapNode> candidates = getListCandidates(map);
        ArrayList<MapNode> solution = new ArrayList<>();
        while(!towers.size() == 0 && !candidates.isEmpty()) {
            MapNode c = candidates.get(0);
            candidates.remove(0);
            if (isFeasible(c, map)) {
                Tower t = towers.get(0);
                towers.remove(0);
                t.setPosition(c.getPosition());
                solution.add(c);
            }
        }

        // C <- Celdas del mapa (Conjunto de candidatos)
        // S <- VACIO (Conjunto solución)
        // mientras !TodasTorretasColocadas() Y C != VACIO

        // c <- mejorCelda(C) ----> Como haremos eso, asignadole valores a las celdas
        // candidatas, a mas prometedora, mas puntos tendrá. Esto lo haremos haciendo
        // uso del getNodeValue. Tedremos que explicar como hemos dedido que valor va a
        // tomar. Debemos crear una formula.
        // C <- C - {c} ---> Sacar del conjunto de candidatos para no volverlo a evaluar
        // si esFactible(c) ---> Comprobar que el radio de la torre no solapa con ningun
        // obstaculo, camino, torreta o se sale el radio del mapa
        // torreta.setPosition(c.getPosition())

        // This is just a (bad) example to show you how to use the entities in the game.
        // Replace ALL of this with your own code.

        // Get map size.
        // float mapWidth = map.getSize().x;
        // float mapHeight = map.getSize().y;

        // // Get parameters from the game.
        // float maxEnemyRadius =
        // Game.getInstance().getParam(Config.Parameter.ENEMY_RADIUS_MAX);
        // float maxRange =
        // Game.getInstance().getParam(Config.Parameter.TOWER_RANGE_MAX);

        // // Get nodes the enemies can walk on.
        // ArrayList<MapNode> walkableNodes = map.getWalkableNodes();

        // // Get obstacles.
        // ArrayList<Obstacle> obstacles = map.getObstacles();

        // // Get the grid of map nodes.
        // MapNode[][] grid = map.getNodes();

        // // Get the distance between two nodes.
        // MapNode n1 = grid[0][0];
        // MapNode n2 = grid[0][1];
        // float distance = n1.getPosition().distance(n2.getPosition());

        // // Each node has up to Config.Parameter.MAP_NODE_VALUES values. Default is 5.
        // // You can *optionally* use these values to store information about the
        // nodes.
        // // Set the second value of node n1.
        // n1.setValue(2, getNodeValue(n1, map));

        // // Get the second value of node n1.
        // float value = n1.getValue(2);

        // Random random = new Random();
        // // Loop through the towers.
        // ArrayList<Tower> placedTowers = new ArrayList<>();
        // for (Tower tower : towers) {
        // // Place the tower on a random node.
        // MapNode node =
        // grid[random.nextInt(grid.length)][random.nextInt(grid[0].length)];
        // tower.setPosition(node.getPosition());
        // placedTowers.add(tower);
        // }

        return null;
    }
}
