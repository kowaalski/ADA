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



        public static ArrayList<MapNode> findBestPath(MapNode start, ArrayList<MapNode> walkableNodes, ArrayList<MapNode> endingPoints) {
            // Cola de prioridad que ordena segund sus valores de f de menor a mayor
            Map<MapNode, Integer> fScore = new HashMap<>(); //  Almacena los valores f (g + h) de los nodos.
            Map<MapNode, Integer> gScore = new HashMap<>(); // Almacena los valores g (costo real desde el nodo de inicio a un nodo) de los nodos.
            PriorityQueue<MapNode> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));  // Una cola de prioridad que ordena los nodos según sus valores f, conjuntos de nodos que tenemos para expandir.
            Map<MapNode, MapNode> cameFrom = new HashMap<>(); //  Almacena el nodo anterior de cada nodo en el camino más corto, es donde iremos guardando el camino solucion
            // h --> distancia estimada desde el nodo hasta el nodo objetivo

            for (MapNode node : walkableNodes) { // Inicializa los valores de g y f de todos los nodos a infinito.
                gScore.put(node, Integer.MAX_VALUE);
                fScore.put(node, Integer.MAX_VALUE);
            }

            //**** EMPIEZA EL ALGORITMO ****//
            gScore.put(start, 0);
            fScore.put(start, heuristicCostEstimate(start, endingPoints.get(0)));
            openSet.add(start);


            while (!openSet.isEmpty()) {
                MapNode current = openSet.poll(); // Saca el nodo (y lo elimina de la cola) con menor valor f de la cola de prioridad --> sera el mejor nodo que deberemos usar para avanzar

                if (endingPoints.contains(current)) {
                    return reconstructPath(cameFrom, current);
                }

                for (MapNode neighbor : current.getNeighbors()) {
                    if (!walkableNodes.contains(neighbor)) continue; // Si el vecino no es transitable, lo ignoramos y pasamos a la siguiente iteracion

                    int tentativeGScore = gScore.get(current) + distBetween(current, neighbor); // Calcula el valor g del vecino pasando por el nodo actual(current)

                    if (tentativeGScore < gScore.get(neighbor)) { // Si el valor g del vecino es menor que el que tenia antes, actualizamos los valores de g y f del vecino, es decir hemos encontrado otro camino al vecino el cual es mejor por tanto nos quedamos con el nuevo, por lo que debemos actualizar las estructuras de datos
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentativeGScore);
                        fScore.put(neighbor, gScore.get(neighbor) + heuristicCostEstimate(neighbor, endingPoints.get(0)));
                        if (!openSet.contains(neighbor)) { // Añadimos el nuevo nodo a expandir si no es que está ya añadido
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
            // return (int) a.getPosition().distance(b.getPosition());
            return (int) distanceToNearestTower(b.getPosition());

        }

        public static float distanceToNearestTower(Point2D point) {
            List<Tower> towers = Game.getInstance().getMap().getTowers();
            float minDistance = Float.MAX_VALUE;
            for (Tower tower : towers) {
                float distance = point.distance(tower.getPosition());
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
            return minDistance;
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
