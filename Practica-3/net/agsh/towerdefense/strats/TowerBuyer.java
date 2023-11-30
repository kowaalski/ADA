package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {

    // Creo la clase tupla, para poder tener un objeto que me asigne una torreta con
    // su score (sería el peso en la mochila)
    public static class Tuple {
        private final Tower first;
        private final int second;

        public Tuple(Tower first, float second) {
            this.first = first;
            this.second = (int) Math.floor(second);
        }

        // Tower
        public Tower getFirst() {
            return first;
        }

        // Score == Valor
        public int getSecond() {
            return second;
        }

        public String toString() {
            return "Tuple{TowerID=" + first.getId() + ", Score=" + second + ", CostMoney= " + first.getCost() + " }";
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

        // float score = ((range * damage) / cooldown + dispersion) - cost / 100;
        float score = ((range * damage) / cooldown) - cost / 1000;

        return score;

    }

    // Asigna a todas las towers un score (Valor de lo buena que es) y
    // devuelve una lista que contiene
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

    /**** ENTRY POINT ***/
    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {
        // System.out.println(towers.toString());
        // Redondea quita la parte decimal
        // System.out.println();
        // System.out.println();
        // System.out.println("-- ORIGINAL TOWER LIST --");
        // for (int i = 0; i < towers.size(); i++) {
        // Tower tower = towers.get(i);
        // System.out.println("Tower ID: " + tower.getId() + ", CostMoney: " +
        // roundMoney(tower.getCost()));
        // }
        // System.out.println();

        int moneyInt = roundMoney(money);

        ArrayList<Tuple> listTowers = getAllTowersValues(towers);
        listTowers = mergeSort(listTowers, 0, listTowers.size() - 1);

        // System.out.println("// TOWERS SELECTED //");

        ArrayList<Tower> solution = makeTable(listTowers, moneyInt);

        // int totalMoneySpend = 0;
        // for (int i = 0; i < solution.size(); i++) {
        // Tower tower = solution.get(i);
        // totalMoneySpend = totalMoneySpend + roundMoney(tower.getCost());
        // System.out.println(
        // (i + 1) + ". Tower ID: " + tower.getId() + ", CostMoney: " +
        // roundMoney(tower.getCost()));
        // }
        // System.err.println("-MONEY AVALIABLE TO SPEND: " + moneyInt);
        // System.out.println("-TOTAL COST MONEY SPEND: " + totalMoneySpend);

        ArrayList<Integer> finalSolution = getIndexFromTower(solution, towers);

        // System.out.println("/////// FINAL SOLUTION OF INDEX ");
        // int i = 1;
        // for (int num : finalSolution) {
        // System.out.println(i + ". Tower ID: " + towers.get(num).getId());
        // i++;
        // }

        // System.out.println(
        // "TAM SOLUTION TOWER: " + solution.size() + ", TAM SOLUTION FINAL INDEX: " +
        // finalSolution.size());
        return finalSolution;
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

    public static int roundMoney(float money) {
        int roundMoney;
        float decimalPart = money - (int) money;

        // Baiscamente siempre es mejor redondear hacia arriba
        if (decimalPart > 0.01) {
            roundMoney = (int) Math.ceil(money);
        } else {
            roundMoney = (int) Math.floor(money);
        }

        return roundMoney;
    }

    public static ArrayList<Tower> makeTable(ArrayList<Tuple> listTowers, int money) {

        // Cada torreta es una fila
        // Valores discretos desde 0 hasta el total de money
        int files = listTowers.size();
        int columns = money;
        int[][] table = initTable(files, columns);

        ArrayList<Integer> listCapacities = fillCapacity(money);

        /*******************************************/
        /********* ALGORITMO DE LA MOCHILA *********/
        /*******************************************/
        // La primera fila se hace sola, ya que no depende de ninguna
        for (int j = 0; j < columns; j++) {
            int cost = roundMoney(listTowers.get(1).first.getCost()); // Coste de la torreta (dinero que vale)
            int value = listTowers.get(1).getSecond();

            if (j < cost) {
                table[1][j] = 0;
            } else {
                table[1][j] = value;
            }
        }

        for (int i = 1; i < files; i++) {
            for (int j = 0; j < columns; j++) {
                int cost = roundMoney(listTowers.get(i).first.getCost());
                int value = listTowers.get(i).getSecond();

                if (j < cost) {
                    table[i][j] = table[i - 1][j];
                } else {
                    int aux = j - cost;
                    table[i][j] = Math.max(table[i - 1][j], table[i - 1][aux] + value);
                }
            }
        }

        // printMatrix(table);
        ArrayList<Tower> solution = reconstructSolution(table, listTowers, listCapacities);
        // System.out.println(solution.toString());
        // System.out.println("//////////////////////////////////");

        return solution;
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
    // decir te devuelve la lista de torretas que se deben seleccionar.
    // Tener en cuenta que al ordenar la lista de torretas, hemos perdido la
    // referencia de los indices de las torretas que seleccionemos
    // al reconstruir la solución, por lo que guardaremos la torreta entera
    // solucionada en el array solution
    //
    public static ArrayList<Tower> reconstructSolution(int[][] solvedTable, ArrayList<Tuple> listTowers,
            ArrayList<Integer> listCapacities) {
        ArrayList<Tower> solution = new ArrayList<>();
        int lastFile = solvedTable.length - 1;
        int lastColumn = solvedTable[0].length - 1;

        Tower lastTower = listTowers.get(listTowers.size() - 1).getFirst();
        int lastCapacity = roundMoney(listCapacities.get(listCapacities.size() - 1));
        boolean seguir = true;
        while (seguir) {
            int last = solvedTable[lastFile][lastColumn];
            // System.out.println("LAST:" +last);
            // System.out.println("LAST FILA ANTERIOR:" +solvedTable[lastFile -
            // 1][lastColumn]);

            if (last != solvedTable[lastFile - 1][lastColumn]) {
                // System.out.println("dentro");
                solution.add(lastTower);
                lastColumn = lastCapacity - roundMoney(lastTower.getCost());
                lastCapacity = listCapacities.get(lastColumn);
            }

            lastFile = lastFile - 1;
            lastTower = listTowers.get(lastFile).getFirst();
            last = solvedTable[lastFile][lastColumn];

            if (lastFile == 0 && solvedTable[lastFile][lastColumn] != 0) { // CASO FINAL LLEGA A LA PRIMERA FILA Y
                                                                           // TERMINA
                // System.out.println("Dentro final-1");
                solution.add(lastTower);
                seguir = false;
            } else if (lastFile == 0 && solvedTable[0][lastColumn] == 0) {
                // System.out.println("Dentro final-2");
                seguir = false;
            }
        }
        return solution;

    }

    // Asocia el ID de las torretas seleccionadas con su respectivo indice en la
    // lista original de torretas sin ordenar ni hacer tuplas
    public static ArrayList<Integer> getIndexFromTower(ArrayList<Tower> solutionTower,
            ArrayList<Tower> listOriginalTowers) {

        ArrayList<Integer> finalSolution = new ArrayList<>();
        int i = 0;
        int j = 0;
        boolean seguir = true;

        while (i < solutionTower.size()) {
            j = 0;
            seguir = true;
            Tower towerSolution = solutionTower.get(i);
            while (j < listOriginalTowers.size() && seguir) {
                Tower towerOriginal = listOriginalTowers.get(j);
                if (towerSolution.getId() == towerOriginal.getId()) {
                    finalSolution.add(j);
                    seguir = false;
                } else {
                    j = j + 1;
                }
            }
            i = i + 1;
        }
        return finalSolution;
    }

    // Ordeno por coste (sería el peso en la mochila) de la torreta
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
