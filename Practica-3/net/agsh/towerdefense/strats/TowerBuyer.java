package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

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

    // Asigna a todas las towers un score (peso) y devuelve una lista que contiene una tupla <Tower,Score>
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

    /*** ENTRY POINT  ***/
    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        // System.out.println(" /////////////// Score: " +
        // getTowerValue(towers.get(0)));
        ArrayList<Tuple> listTowers = getAllTowersValues(towers);

        System.out.println(listTowers);

        ArrayList<Integer> selected = new ArrayList<>();
        for (int i = 0; i < towers.size(); i++) {
            if (money >= towers.get(i).getCost()) {
                money -= towers.get(i).getCost();
                selected.add(i);
            }
        }

        return selected;
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

    // public static ArrayList<Integer> initTable(ArrayList<Tower> towers, float
    // money) {

    // // Cada torreta es una fila
    // // Valores discretos desde 0 hasta el total de money
    // int files = towers.size();
    // int[][] table = new int[files][money];

    // return selected;
    // }
}
