/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#  He probado con muchísimas heurísticas y combinaciones de ellas, pero no he 
#  conseguido mejorar el score, la heurística que mejor score me ha dado ha sido la 1
#  getWalkableNodeValue(MapNode n)
#  
#    1. Obtiene una lista de todas las torres en el juego.
#
#    2. Inicializa minDistance con un valor muy alto (Float.MAX_VALUE). 
#       Este valor se utilizará para almacenar la distancia más corta encontrada hasta ahora.
#
#    3. Recorre todas las torres en la lista. Para cada torre, calcula la distancia desde el punto 
#       dado hasta la posición de la torre.
#
#    4. Si la distancia calculada es menor que 10 y menor que la minDistance actual, entonces actualiza
#       minDistance con esta nueva distancia.
#
#    En términos de heurística para el algoritmo de búsqueda A*, este método favorece los nodos que están 
#    más lejos de las torres.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.*;
import java.util.Map;
import java.util.*;

public class EnemyPathFinder {

    //# **********************************/
    //# **** Heurísticas probadas *******/
    //# ********************************/
    
    // **** Heuristica 1
    public static float getWalkableNodeValue(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float minDistance = Float.MAX_VALUE;
        for (Tower tower : towers) {
            float distance = point.distance(tower.getPosition());
            if (distance < 10) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    // **** Heuristica 1 - v2
    public static float distanceAndDamageFromNearestTower(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float minDistanceDamage = Float.MAX_VALUE;
        for (Tower tower : towers) {
            float distance = point.distance(tower.getPosition());
            if (distance < 10) {
                float distanceDamage = distance * tower.getDamage();
                if (distanceDamage < minDistanceDamage) {
                    minDistanceDamage = distanceDamage;
                }
            }
        }
        return minDistanceDamage;
    }
// **** Heuristica extra
    public static float distanceAndCooldownFromNearestTower(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float minDistanceCooldown = Float.MAX_VALUE;
        for (Tower tower : towers) {
            float distance = point.distance(tower.getPosition());
            // if (distance < 10) {
                float distanceCooldown = distance * tower.getCooldownLeft();
                if (distanceCooldown < minDistanceCooldown) {
                    minDistanceCooldown = distanceCooldown;
                // }
            }
        }
        return minDistanceCooldown;
    }

    // **** Heuristica 2
    public static float awayPathsTowersDamages(MapNode node) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float maxScore = Float.MIN_VALUE;
        for (Tower tower : towers) {
            float distance = node.getPosition().distance(tower.getPosition());
            float score = distance * tower.getDamage();
            if (score > maxScore) {
                maxScore = score;
            }
        }
        return maxScore;

    }

    // **** Heuristica 3
    public static float minTowerDamage(MapNode node) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float totalDamage = 0;
        for (Tower tower : towers) {
            float distance = node.getPosition().distance(tower.getPosition());
            if (distance <= tower.getRange()) {
                totalDamage += tower.getDamage();
            }
        }
        return totalDamage;
    }

    // **** Heuristica 4
    public static float countTowersInRange(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        int count = 0;
        for (Tower tower : towers) {
            float range=tower.getRange();
            float distance = point.distance(tower.getPosition());
            if (distance <= range) {
                count++;
            }
        }
        return count;
    }

    // **** Heuristica 5
    public static float nearestTowerCooldown(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        float minCooldown = Float.MAX_VALUE;
        for (Tower tower : towers) {
            float distance = point.distance(tower.getPosition());
            if (distance < minCooldown) {
                minCooldown = tower.getCooldown();
            }
        }
        return minCooldown;
    }

    // **** Heuristica 6
    public static float countTowersCanShoot(Point2D point) {
        List<Tower> towers = Game.getInstance().getMap().getTowers();
        int count = 0;
        for (Tower tower : towers) {
            if (tower.getCooldownLeft() == 0 && point.distance(tower.getPosition()) <= tower.getRange()) {
                count++;
            }
        }
        return count;
    }


    public static ArrayList<MapNode> findBestPath(MapNode start, ArrayList<MapNode> walkableNodes,
            ArrayList<MapNode> endingPoints) {
        // Cola de prioridad que ordena segund sus valores de f de menor a mayor
        
        Map<MapNode, Float> fScore = new HashMap<>(); // Almacena los valores f (g + h) de los nodos.
        Map<MapNode, Float> gScore = new HashMap<>(); // Almacena los valores g (costo real desde el nodo de inicio a un
                                                      // nodo) de los nodos.
        PriorityQueue<MapNode> openSet = new PriorityQueue<>(Comparator.comparing(fScore::get)); // Una cola de
                                                                                                 // prioridad que ordena
                                                                                                 // los nodos según sus
                                                                                                 // valores f, conjuntos
                                                                                                 // de nodos que tenemos
                                                                                                 // para expandir.
        Map<MapNode, MapNode> cameFrom = new HashMap<>(); // Almacena el nodo anterior de cada nodo en el camino más
                                                          // corto, es donde iremos guardando el camino solucion

        for (MapNode node : walkableNodes) { // Inicializa los valores de g y f de todos los nodos a infinito.
            gScore.put(node, Float.MAX_VALUE);
            fScore.put(node, Float.MAX_VALUE);
        }

        //# **********************************/
        //# **** EMPIEZA EL ALGORITMO A *****/
        //# ********************************/

        MapNode bestEndPoint = bestEndPoint(start,endingPoints); // Calcula el mejor nodo final
        gScore.put(start, 0.0f);
        fScore.put(start, heuristicCostEstimate(start,bestEndPoint) );
        openSet.add(start);

        while (!openSet.isEmpty()) {
            MapNode current = openSet.poll(); // Saca el nodo (y lo elimina de la cola) con menor valor f de la cola de
                                              // prioridad --> sera el mejor nodo que deberemos usar para avanzar

            if (endingPoints.contains(current)) {
                return reconstructPath(cameFrom, current);
            }

            for (MapNode neighbor : current.getNeighbors()) {
                if (!walkableNodes.contains(neighbor))
                    continue; // Si el vecino no es transitable, lo ignoramos y pasamos a la siguiente
                              // iteracion

                float tentativeGScore = gScore.get(current) + distBetween(current, neighbor); // Calcula el valor g del
                                                                                              // vecino pasando por el
                                                                                              // nodo actual(current)

                if (tentativeGScore < gScore.get(neighbor)) { // Si el valor g del vecino es menor que el que tenia
                                                              // antes, actualizamos los valores de g y f del vecino, es
                                                              // decir hemos encontrado otro camino al vecino el cual es
                                                              // mejor por tanto nos quedamos con el nuevo, por lo que
                                                              // debemos actualizar las estructuras de datos
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    fScore.put(neighbor, gScore.get(neighbor) + heuristicCostEstimate(neighbor, endingPoints.get(0)));
                    if (!openSet.contains(neighbor)) { // Añadimos el nuevo nodo a expandir si no es que está ya añadido
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // Si no se encuentra un camino, devolvemos una lista vacia
    }

    private static MapNode bestEndPoint(MapNode a, ArrayList<MapNode> endingPoints) {
        double minCost = Double.MAX_VALUE;
        MapNode bestEndPoint = null;
        for (MapNode endPoint : endingPoints) {
            double cost = heuristicCostEstimate(a, endPoint);
            if (cost < minCost) {
                minCost = cost;
                bestEndPoint = endPoint;
            }
        }
        return bestEndPoint;
    }

    private static float distBetween(MapNode a, MapNode b) {
        return a.getPosition().distance(b.getPosition());
    }

    private static float heuristicCostEstimate(MapNode a, MapNode b) {
        // Use the distance method from your Point2D class
        // return (int) a.getPosition().distance(b.getPosition()); // 25
        // return getWalkableNodeValue(b,a); // score 25
        // return awayPathsTowersDamages(b); // score 31
        // return minTowerDamage(b); // score 31
        // return countTowersInRange(b.getPosition()); // score 31
        // return nearestTowerCooldown(b.getPosition()); // score 31
        // return countTowersCanShoot(b.getPosition()); // score 31
        // return distanceAndDamageFromNearestTower(b.getPosition()); // score 37
        return getWalkableNodeValue(b.getPosition()); // score 37
    }

    // Reconstruye el camino desde el nodo final al nodo inicial
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
