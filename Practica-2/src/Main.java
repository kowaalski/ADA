import net.agsh.towerdefense.*;

import java.util.ArrayList;
/*

#---------------------------------------------------------------------------------------------------------------------
#  RESULTADOS Y EXPLICACIÓN
#---------------------------------------------------------------------------------------------------------------------

  n      NoSort  InsertionSort   MergeSort    QuickSort
 496     0,0139     0,1384        0,0291       0,0842
 946     0,0224     0,5157        0,0600       0,1859
 1540    0,0323     1,6170        0,1410       0,4104
 2278    0,0437     3,4136        0,2050       0,7965
 3160    0,0802     7,2302        0,2956       1,3259


## InsertionSort ##
Sorprendentemente rápido y eficaz tanto para valores pequeños como grandes 

## InsertionSort ## 
Aunque es eficiente para arreglos pequeños, su tiempo de ejecución 
aumenta rápidamente con tamaños más grandes, como se puede observar en los resultados proporcionados.

## MergeSort ## 
Es eficiente y estable para diferentes tamaños de matrices y tiene un tiempo 
de ejecución relativamente bajo, incluso para matrices más grandes, como se puede ver en 
los resultados proporcionados.

## QuickSort ## 
Aunque tiene un buen rendimiento en general, su tiempo de ejecución aumenta 
más rápidamente que el de MergeSort para matrices más grandes, como se puede observar 
en los resultados proporcionados.


----------- CONCLUSIÓN -----------

Dado que la matriz de datos se organiza de manera que los valores se vuelven más pequeños a medida que se acercan 
al centro y más grandes a medida que se alejan, esto tiene un impacto significativo en el rendimiento de los algoritmos de ordenación.

Dado este patrón particular en la distribución de valores, la fuerza bruta o el algoritmo NoSort es 
sorprendentemente eficaz en este caso, ya que no implican comparaciones o movimientos innecesarios. 
La distribución de los valores en la matriz, con los valores más pequeños cerca del centro y los más grandes 
en los extremos, permite que la ordenación por fuerza bruta realice comparaciones y movimientos más eficientes 
en comparación con los algoritmos clásicos que no están diseñados específicamente para aprovechar este tipo de patrón.

Para los algoritmos clásicos como MergeSort, QuickSort y InsertionSort, que no están optimizados para esta distribución 
específica de valores, pueden requerir un mayor número de comparaciones y movimientos, lo que puede llevar a 
un mayor tiempo de ejecución en comparación con la ordenación por fuerza bruta en este escenario particular.

Dicho esto, aunque la ordenación por fuerza bruta parece ser más efectiva en este caso concreto debido a la distribución particular de 
los valores en la matriz, sigue siendo esencial considerar el rendimiento y la escalabilidad de los algoritmos de ordenación para 
conjuntos de datos más grandes.

Por lo que en conclusión debido a la distribución de los datos que nos dan, el algoritmo que deberíamos usar para ordenar
las celdas debería ser el de NoSort

*/

public class Main {
    public static void main(String[] args) {
        // initialize game and map
        Game g = Game.getInstance();
        g.init(0);
        Config config = new Config();

        System.out.print("n\tNoSort\tInsertionSort\tMergeSort\tQuickSort\n");

        for (float scale = 0.5f; scale < 1.5; scale += 0.2f) {
            Map map = new Map(new Point2D(config.get(Config.Parameter.MAP_SIZE_X) * scale,
                    config.get(Config.Parameter.MAP_SIZE_Y) * scale),
                    config.get(Config.Parameter.MAP_GRID_SPACE));
            map.init();

            // assign values to nodes and print map
            boolean printMap = false;
            MapNode center = map.getNodes()[map.getNodes().length / 2][map.getNodes()[0].length / 2];
            for (int i = 0; i < map.getNodes().length; i++) {
                for (int j = 0; j < map.getNodes()[i].length; j++) {
                    if (map.getNodes()[i][j].isWalkable()) {
                        if (printMap) {
                            System.out.print("   ");
                        }
                    } else {
                        float distanceToCenter = center.getPosition().distance(map.getNodes()[i][j].getPosition());
                        map.getNodes()[i][j].setValue(0, distanceToCenter);
                        if (printMap) {
                            System.out.printf("%2.0f ", distanceToCenter / 10f);
                        }
                    }
                }
                if (printMap) {
                    System.out.println();
                }
            }

            // select best nodes for towers
            Point2D size = g.getMap().getSize();
            float separation = map.getSeparation();
            int numberOfTowers = (int) (g.getParam(Config.Parameter.TOWER_DENSITY) * size.x * size.y
                    / (separation * separation));
            ArrayList<MapNode> best = selectBestNodes(map.getNodesList(), numberOfTowers);
        }
    }

    private static ArrayList<MapNode> deepCopy(ArrayList<MapNode> nodes) {
        ArrayList<MapNode> copy = new ArrayList<>();
        for (MapNode node : nodes) {
            copy.add(new MapNode(node.getPosition(), node.isWalkable(), node.getValues()));
        }
        return copy;
    }

    private static boolean NodeListEquals(ArrayList<MapNode> a, ArrayList<MapNode> b, int valueIndex) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).getValue(valueIndex) != b.get(i).getValue(valueIndex)) {
                return false;
            }
        }
        return true;
    }

    private static ArrayList<MapNode> selectBestNodes(ArrayList<MapNode> nodesList, int count) {
        int n = nodesList.size();
        long maxTime = 1000;

        System.out.print(n + "\t");

        // ========================== No sort ==========================
        int iterations = 0;
        Chronometer c = new Chronometer();
        ArrayList<MapNode> bestNoSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestNoSort = selectBestNodesNoSort(nodesListCopy, count);

            iterations++;
        } while (c.getElapsedTime() < maxTime);
        //////////////
        // System.out.println(bestNoSort);
        /////////////
        float timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        // ========================== Insertion sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestInsertionSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestInsertionSort = selectBestNodesInsertionSort(nodesListCopy, count);
            iterations++;
        } while (c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if (!NodeListEquals(bestNoSort, bestInsertionSort, 0)) {
            System.out.println("ERROR");
        }

        // ========================== Merge sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestMergeSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestMergeSort = selectBestNodesMergeSort(nodesListCopy, count);
            iterations++;
        } while (c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if (!NodeListEquals(bestNoSort, bestMergeSort, 0)) {
            System.out.println("ERROR");
        }

        // ========================== Quick sort ==========================
        iterations = 0;
        c = new Chronometer();
        ArrayList<MapNode> bestQuickSort;
        do {
            c.pause();
            ArrayList<MapNode> nodesListCopy = deepCopy(nodesList);
            c.resume();
            bestQuickSort = selectBestNodesQuickSort(nodesListCopy, count);
            iterations++;
        } while (c.getElapsedTime() < maxTime);
        timePerIteration = (float) c.getElapsedTime() / iterations;
        System.out.printf("%.4f\t", timePerIteration);

        if (!NodeListEquals(bestNoSort, bestQuickSort, 0)) {
            System.out.println("ERROR");
        }

        System.out.println();
        return bestNoSort;
    }

    private static ArrayList<MapNode> selectBestNodesQuickSort(ArrayList<MapNode> nodesListCopy, int count) {
        // En java los arrays son los unicos tipos que se pasan por referencia,
        // por tanto si la funcion modifica el array, el array original tambien es
        // modificado
        quickSort(nodesListCopy, 0, nodesListCopy.size() - 1);

        ArrayList<MapNode> best = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            best.add(nodesListCopy.get(i));
        }
        return best;
    }

    private static ArrayList<MapNode> selectBestNodesMergeSort(ArrayList<MapNode> nodesListCopy, int count) {

        ArrayList<MapNode> sortedNodeList = mergeSort(nodesListCopy, 0, nodesListCopy.size() - 1);

        ArrayList<MapNode> best = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            best.add(sortedNodeList.get(i));
        }
        return best;

    }

    /*
     * #----------------------------------------------------------------------------
     * # QUICKSORT
     * #----------------------------------------------------------------------------
     * 
     * Implementacion basada en
     * https://www.youtube.com/watch?v=UrPJLhKF1jY&ab_channel=ChioCode
     * 
     */
    private static void quickSort(ArrayList<MapNode> nodesList, int low, int high) {
        if (low < high) { // vendría siendo mientras el num de elementos del array sea mayor de 1
            int pi = pivot(nodesList, low, high); // en realidad donde se realiza la ordenacion es en este metodo

            quickSort(nodesList, low, pi - 1);
            quickSort(nodesList, pi + 1, high);
        }
    }

    /*
     * Para reorganizar los elementos del ArrayList de manera que los elementos más
     * pequeños que el pivote se coloquen a la izquierda del pivote y
     * los elementos más grandes a la derecha.
     */
    private static int pivot(ArrayList<MapNode> nodesList, int low, int high) {
        MapNode pivot = nodesList.get(high); // Pone el pivote en la ultima pos del array
        int i = (low - 1); // Pone el puntero i fuera del array por la izq
        /*
         * El algoritmo tiene que recorrer todos los elem del array
         * 
         * SI el elemento < pivote, tenemos que cambiarlo
         * por el siguiente elemento que apunta el puntero i, y avanzamos el puntero i
         * 
         * SI el elemento > pivote no hacemos nada
         * 
         * Al salir del bucle tenemos que cambiar el pivote por el siguiente elemento
         * que apunta el puntero i
         * 
         * Asi se quedaría a la izquierda del pivote todos los elementos menores que el
         * y a la derecha mayores que el
         * Devolvemos la pos nueva del pivote
         */
        for (int j = low; j < high; j++) {
            if (nodesList.get(j).getValue(0) < pivot.getValue(0)) { // Condicion '>' --> Ordenaría de mayor a menor
                i++;

                // intercambia elemento de la pos i por j
                MapNode temp = nodesList.get(i);
                nodesList.set(i, nodesList.get(j));
                nodesList.set(j, temp);
            }
        }

        MapNode temp = nodesList.get(i + 1);
        nodesList.set(i + 1, nodesList.get(high));
        nodesList.set(high, temp);

        return i + 1; // la nueva pos del pivote
    }

    /*
     * #----------------------------------------------------------------------------
     * # MERGESORT
     * #----------------------------------------------------------------------------
     * 
     */

    private static ArrayList<MapNode> mergeSort(ArrayList<MapNode> nodesListCopy, int i, int j) {
        if (i < j) {
            int mid = (i + j) / 2;
            ArrayList<MapNode> left = mergeSort(nodesListCopy, i, mid);
            ArrayList<MapNode> right = mergeSort(nodesListCopy, mid + 1, j);
            return merge(left, right);
        } else { // lo mismo que longitud del array 1, caso base, devolvemos un array con ese
                 // mismo elemento
            ArrayList<MapNode> base = new ArrayList<>();
            base.add(nodesListCopy.get(i));
            return base;
        }
    }

    // Fusiono los dos arrays
    private static ArrayList<MapNode> merge(ArrayList<MapNode> left, ArrayList<MapNode> right) {
        ArrayList<MapNode> result = new ArrayList<>();
        int i = 0, j = 0;
        /*
         * Hay que tener en cuento que los dos arrays left y right estan ordenados.
         * Se van recorriendo las posiciones de los array y se van comparando si es
         * menor una que otra
         * si es así se avanza el respectivo indice que toca, si metemos en el array
         * resultante una posicion
         * de uno de los array pues habra que incrementar el indice del correspondiente
         * array para indicar que ya lo hemos metido esa posicion
         */
        while (i < left.size() && j < right.size()) {
            if (left.get(i).getValue(0) <= right.get(j).getValue(0)) { // Condicion '>=' --> Ordenaría de mayor a menor
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        /*
         * Tener en cuenta que aun así pueden quedar posiciones finales de los array que
         * no se han insertado
         * ya que el bucle a lo mejor ha terminado porque el tamaño por ejemplo de i ya
         * ha superado el del array left, por tanto
         * habrá que meter juntar tal cual al array resul los valores de right, y asi
         * podría pasar al contrario si j supera el tamaño de right antes que i el suyo.
         */
        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }
        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }
        return result;
    }

    /*
     * #----------------------------------------------------------------------------
     * # INSERTION SORT
     * #----------------------------------------------------------------------------
     * 
     */

     /* Coge cada elemento del array (x) y recorre a la izquierda de el comprobando cada elemento de su izq
     si el elemento de su izquierda < no hace nada
     si el elemento de su izquierda > intercambia su posicion
      */ 

    private static ArrayList<MapNode> selectBestNodesInsertionSort(ArrayList<MapNode> nodesList, int count) {
        for (int i = 1; i < nodesList.size(); i++) {
            int j = i - 1;
            MapNode x = nodesList.get(i); // Selecciona el elemento en la posición i
                                          // Mueve los elementos mayores que x una posición hacia adelante
            while (j >= 0 && nodesList.get(j).getValue(0) > x.getValue(0)) {
                nodesList.set(j + 1, nodesList.get(j));
                j--;
            }
            nodesList.set(j + 1, x);
        }

        ArrayList<MapNode> best = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            best.add(nodesList.get(i));
        }

        return best;
    }

    /*
     * #----------------------------------------------------------------------------
     * # BRUTE FORCE SORT
     * #----------------------------------------------------------------------------
     * 
     */

    private static ArrayList<MapNode> selectBestNodesNoSort(ArrayList<MapNode> nodesList, int count) {
        ArrayList<MapNode> bestNodes = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            float bestValue = Float.MAX_VALUE;
            MapNode bestNode = null;
            for (MapNode node : nodesList) {
                if (node.getValue(0) < bestValue && !bestNodes.contains(node)) {
                    bestValue = node.getValue(0);
                    bestNode = node;
                }

            }
            bestNodes.add(bestNode);
        }

        return bestNodes;
    }
}
