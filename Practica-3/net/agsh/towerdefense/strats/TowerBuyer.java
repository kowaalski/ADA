package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.MapNode;
import net.agsh.towerdefense.Tower;
import java.util.Random;

import java.util.ArrayList;

public class TowerBuyer {

    // Creo la clase tupla, para poder tener un objeto que me asigne una torreta con
    // su score (sería el peso en la mochila)
    public static class Tuple {
        private final Tower first;
        private final float second;

        public Tuple(Tower first, float second) {
            this.first = first;
            this.second = second;
        }

        public Tower getFirst() {
            return first;
        }

        public float getSecond() {
            return second;
        }

        public String toString() {
            return "Tuple{Tower=" + first + ", Score=" + second + '}';
        }

    }

    // valor = score
    public static float getTowerValue(Tower tower) {
        float range = tower.getRange();
        float damage = tower.getDamage();
        float cooldown = tower.getCooldown();
        float dispersion = tower.getDispersion();
        float cost = tower.getCost();

        // System.out.println("1. range" + range);
        // System.out.println("2. damage " + damage);
        // System.out.println("3. cooldown " + cooldown);
        // System.out.println("4. dispersion " + dispersion);
        // System.out.println("5. cost " + cost);

        /*
         * Esta fórmula tiene en cuenta los siguientes aspectos:
         * - Mayor alcance (Range) y daño (Damage) contribuyen positivamente al puntaje.
         * - Menor tiempo de recarga (Cooldown) y dispersión (Dispersion) contribuyen
         * positivamente al puntaje.
         * - El costo (Costo) contribuye negativamente al puntaje, lo divido entre 100
         * para no tener scores negativos.
         */

        float score = ((range * damage) / cooldown + dispersion) - cost / 100;

        return score;

    }

    // Asigna a todas las towers un score (peso) y devuelve una lista que contiene
    // una tupla <Tower,Score>
    public static ArrayList<Tuple> getAllTowersValues(ArrayList<Tower> towers) {
        ArrayList<Tuple> listTowersScores = new ArrayList<>();

        for (int i = 0; i < towers.size(); i++) {
            Tower tower = towers.get(i);
            float score = getTowerValue(tower);

            Tuple tupla = new Tuple(tower, score);
            listTowersScores.add(tupla);
        }

        return listTowersScores;
    }

    /*** ENTRY POINT ***/
    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        ArrayList<Tuple> listTowers = getAllTowersValues(towers);
        listTowers = mergeSort(listTowers, 0, listTowers.size() - 1);

        // System.out.println(listTowers);

        makeTable(listTowers, (int) money);
        
        
        // getIndexFromID(solution,towers)

        return null;
    }

    /*
     * 1. Recorremos la lista de towers y les asignamos un valor a cada una según la
     * formula que nos inventemos y NO ordenamos
     * 2. Creamos la tabla entera con todos sus valores inicialiceTable
     * 3. Reconstruimos las solucion para saber la tabla dada la matriz.
     * Miramos ultima fila y ultima columna de la matriz y vemos si es distinto que
     * el vecino de arriba
     * Si es distinto entonces metemos el objeto de esa fila en nuestro
     * arraySolucionTorretas(CONTENDERA EL INDICE DE DONDE ESTA ESA TORRETA EN EL
     * ARRAY ORIGINAL), y cogemos y a la capacidad(dinero) de esas misma columna
     * cogemos y le restamos el peso (dinero de esa torreta) y ahora avanzamos a la
     * celda de la fila de justo arriba a la columna que se nos queda al
     * restar lo de antes, y repetimos el proceso justo el vecino de arriba es
     * distinto? si es igual no hacemos nada y subimos a ese vecino y hacemos lo
     * mismo con su
     * vecino de arriba si es distinto repetimos, así hasta que lleguemos a
     * seleccionar una celda de la fila 0
     * 
     * 
     * 
     * 
     */

    public static ArrayList<Integer> makeTable(ArrayList<Tuple> listTowers, int money) {

        // Cada torreta es una fila
        // Valores discretos desde 0 hasta el total de money
        int files = listTowers.size();
        int columns = money;
        int[][] table = initTable(files, columns);

        ArrayList<Integer> listCapacities = fillCapacity(money);

        // listCapacities -> P
        // listTowers -> V

        // SEGUIR TRANSPARENCIAS TAL CUAL
        for (int i = 0; i < files; i++) {
            for (int j = 0; j < columns; j++) {

            }
        }

        return null;
    }

    // Creo una matriz a modo de tabla y la inicializo con todo a 0 en todas las
    // filas en su primer columna
    public static int[][] initTable(int files, int columns) {
        int[][] table = new int[files][columns];

        for (int i = 0; i < files; i++) {
            table[i][0] = 0;
        }

        return table;
    }

    // Función para imprimir una matriz
    public static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println(); // Salto de línea al final de cada fila
        }
    }

    // Para rellenar un array desde 0,1,2,3...Money --> Asi tendría un array con las
    // "capacidades"
    public static ArrayList<Integer> fillCapacity(int money) {
        ArrayList<Integer> listCapacities = new ArrayList<>();
        for (int i = 0; i < money; i++) {
            listCapacities.add(i);

        }

        return listCapacities;
    }

    // Este método una vez obtenida la tabla rellena, reconstruye la solución, es
    // decir te devuelve la lista de torretas (indices) que se deben seleccionar
    // Tener en cuenta que al ordenar la lista de torretas, hemos perdido la
    // referencia de los indices de las torretas que seleccionemos
    // al reconstruir la solución, por lo que guardaremos el ID de la torreta
    // solucionada en el array solution, y despues
    //
    public static ArrayList<Integer> reconstructSolution(int[][] solvedTable) {
        ArrayList<Integer> solution = new ArrayList<>();
        // TO DO

       
        return null;
    }

    // Asocia el ID de las torretas seleccionadas con su respectivo indice en la
    // lista original de torretas sin ordenar ni hacer tuplas
    public static ArrayList<Integer> getIndexFromID(ArrayList<Integer> solutionIndex, ArrayList<Tower> listOriginalTowers) {
        ArrayList<Integer> FinalSolution = new ArrayList<>();
        // TO DO

        return null;
    }

    // Ordeno por coste de la torreta
    private static ArrayList<Tuple> mergeSort(ArrayList<Tuple> listTowers, int i, int j) {
        if (i < j) {
            int mid = (i + j) / 2;
            ArrayList<Tuple> left = mergeSort(listTowers, i, mid);
            ArrayList<Tuple> right = mergeSort(listTowers, mid + 1, j);
            return merge(left, right);
        } else { // lo mismo que longitud del array 1, caso base, devolvemos un array con ese
                 // mismo elemento
            ArrayList<Tuple> base = new ArrayList<>();
            base.add(listTowers.get(i));
            return base;
        }
    }

    // Fusiono los dos arrays
    private static ArrayList<Tuple> merge(ArrayList<Tuple> left, ArrayList<Tuple> right) {
        ArrayList<Tuple> result = new ArrayList<>();
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
            if (left.get(i).first.getCost() <= right.get(j).first.getCost()) { // Condicion '>=' --> Ordenaría de mayor
                                                                               // a menor
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
}