/*
Sustituir este comentario por una explicación de la formula o procedimiento empleado para determinar el valor de una
torreta.
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {

    public static float getTowerValue(Tower tower) {
        float range = tower.getRange();
        float damage = tower.getDamage();
        float cooldown = tower.getCooldown();
        float cost = tower.getCost();
        float dispersion = tower.getCost();

        // System.out.println("1. range" + range);
        // System.out.println("2. damage " + damage);
        // System.out.println("3. cooldown " + cooldown);
        // System.out.println("4. dispersion " + dispersion);
        // System.out.println("5. cost " + cost);

        /*
         * Esta fórmula tiene en cuenta los siguientes aspectos:
         * - Mayor alcance (Range) y daño (Damage) contribuyen positivamente al puntaje.
         * - Menor tiempo de recarga (Cooldown) contribuye
         * negativamente al puntaje.
         * - El costo (Costo) contribuye negativamente al puntaje, lo divido entre 1000
         * para no tener scores negativos.
         */

        float score = ((range * damage) / cooldown + dispersion) - cost / 100;
        // float score = ((range * damage) / cooldown) - cost / 1000;
        // float score = damage;

        return score;

    }

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        ArrayList<Boolean> actualSol = new ArrayList<>();

        ArrayList<Boolean> bestSol = mdr(towers, actualSol, money);
        ArrayList<Integer> selected = new ArrayList<>();

        for (int i = 0; i < bestSol.size(); i++) {
            if (bestSol.get(i)) {
                selected.add(i);
            }
        }

        // System.out.println(bestSol.toString());

        return selected;
    }

    private static ArrayList<Boolean> mdr(ArrayList<Tower> towers, ArrayList<Boolean> actualSol, float c) {
        for (int i = 0; i < towers.size(); i++) {
            actualSol.add(false);
        }
        ArrayList<Boolean> bestSol = new ArrayList<>(actualSol);
        return mdrAux(towers, c, actualSol, 0, bestSol);
    }

    private static ArrayList<Boolean> mdrAux(ArrayList<Tower> towers, float c, ArrayList<Boolean> actualSol, int k,
            ArrayList<Boolean> bestSol) {

        if (towers.get(k).getCost() + getTotalCost(actualSol, towers) <= c) {
            actualSol.set(k, true); // Incluimos el elemento en la solucion
            if (k == towers.size() - 1) { // Es hoja, por tanto es una solucion que debemos de comprobar si es mejor o
                                          // peor que la mejor que ya tenemos
                if (getTotalValue(actualSol, towers) > getTotalValue(bestSol, towers)) {
                    bestSol = new ArrayList<>(actualSol);
                    return bestSol;
                }
            } else { // Si no es hoja, seguimos explorando, el siguiente nodo
                return bestSol = mdrAux(towers, c, actualSol, k + 1, bestSol);
            }
        }

        // Cuando sale de la recursividad lo pone a false y explora la siguiente rama
        // Cuando sale de el if a false, es decir que no cabe el elemento lo que haría
        // sería podar poniendo a false
        // Sirve para estos dos casos a partir de aqui
        actualSol.set(k, false); // Excluimos el elemento de la solucion, para explorar la otra rama si no
                                 // hubiesemos
                                 // metido el elemento
        if (k == towers.size() - 1) { // Es hoja, por tanto es una solucion que debemos de comprobar si es mejor o
            if (getTotalValue(actualSol, towers) > getTotalValue(bestSol, towers)) { // Lo mismo que en el primer IF
                bestSol = new ArrayList<>(actualSol);
                return bestSol;
            }

        } else {
            return bestSol = mdrAux(towers, c, actualSol, k + 1, bestSol);
        }

        return bestSol;

    }

    public static float getTotalValue(ArrayList<Boolean> actualSol, ArrayList<Tower> towers) {
        float totalValue = 0;
        int i = 0;
        while (i < towers.size()) {
            if (actualSol.get(i)) {
                totalValue += getTowerValue(towers.get(i));
            }
            i++;
        }

        return totalValue;
    }

    // coste
    public static float getTotalCost(ArrayList<Boolean> actualSol, ArrayList<Tower> towers) {
        float totalCost = 0;
        int i = 0;
        while (i < towers.size()) {
            if (actualSol.get(i)) {
                totalCost += towers.get(i).getCost();
            }
            i++;
        }

        return totalCost;
    }

    // Busqueda en profundidad
    // public static void explorar(List<Tower> towers, int capacidad, int indice,
    // List<Tower> solucionActual,
    // List<Tower> mejorSolucion) {
    // if (indice == towers.size()) {
    // // Hemos llegado al final de la lista de elementos, es decir llegamos a una
    // hoja
    // int valorActual = calcularValor(solucionActual);
    // int pesoActual = calcularPeso(solucionActual);

    // if (valorActual > calcularValor(mejorSolucion) && pesoActual <= capacidad) {
    // // Esta solución es mejor que la mejor conocida hasta ahora
    // mejorSolucion.clear();
    // mejorSolucion.addAll(solucionActual);
    // }
    // return; // Si devuelve el return entonces se mete y borra el elemento de la
    // solucion y
    // // busca por la rama siguiente si no metiera ese elemento
    // }

    // // Incluir el elemento actual en la solución
    // solucionActual.add(towers.get(indice));
    // explorar(towers, capacidad, indice + 1, solucionActual, mejorSolucion);

    // // Excluir el elemento actual de la solución
    // solucionActual.remove(solucionActual.size() - 1);
    // explorar(towers, capacidad, indice + 1, solucionActual, mejorSolucion);
    // }

    // public static int calcularValor(List<Tower> items) {
    // int valor = 0;
    // for (Tower tower : items) {
    // valor += tower.value;
    // }
    // return valor;
    // }

    // public static int calcularPeso(List<Tower> items) { // cambiar por float
    // int peso = 0;
    // for (Tower tower : items) {
    // peso += tower.getCost();
    // }
    // return peso;
    // }

    // public static void imprimirSolucion(List<Tower> solucion) {
    // System.out.println("Mejor Solución:");
    // for (Tower tower : solucion) {
    // System.out.println("Peso: " + tower.getCost() + ", Valor: " +
    // getTowerValue(tower));
    // }
    // System.out.println("Valor total: " + calcularValor(solucion));
    // System.out.println("Peso total: " + calcularPeso(solucion));
    // }

}
