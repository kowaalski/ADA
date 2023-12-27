/*
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#  He realizado dos implementaciones de backtracking para 
#  intentar conseguir mejor score:
#   
#    1. La primera implementación es la que se encuentra 
#       comentada en el código. He seguido la Versión 1 de 
#       las transparencias de clase: poda por capacidad de la mochila(I), en la que he conseguido
#       un score mínimo de 22
#     
#    2. La segunda implementación es la que se encuentra comentada en el
#       código, he seguido una mezcla de varias versiones, es con la que he 
#       conseguido el mismo score, en cambio es más larga debido al metodo getIndexFromTower que uso para rescatar
#       cada índice de las towers de la solucion, en la que he conseguido un score como mínimo de 22
#
#   En las dos implementaciones la fórmula seguida para darle valor a las towers es la misma:
#   - daño (Damage) contribuyen positivamente al puntaje.
#   - El costo contribuye negativamente al puntaje
#   - He metido mas campos en la formula pero no conseguía mejorar el score con la que menos score he sacado ha sido las más simple
#     
#     FORMULA -->  damage / cost
#
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

/*************************************************************
 * 
 * PRIMERA IMPLEMENTACIÓN
 * 
/*************************************************************/

public class TowerBuyer {

    public static float getTowerValue(Tower tower) {
        float damage = tower.getDamage();
        float cost = tower.getCost();
        float range = tower.getRange();
        float cooldown = tower.getCooldown();

        /*
         * Esta fórmula tiene en cuenta los siguientes aspectos:
         * - daño (Damage) contribuyen positivamente al puntaje.
         * - El costo contribuye negativamente al puntaje,
         * He metido mas campos en la formula pero con la que he consegudio mejor score
         * es esta más simple
         */

        float score = damage / cost;
        // float score = (cost + damage) / (range + cooldown);

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

        return selected;
    }

    private static ArrayList<Boolean> mdr(ArrayList<Tower> towers, ArrayList<Boolean> actualSol, float c) {
        for (int i = 0; i < towers.size(); i++) {
            actualSol.add(false);
        }
        ArrayList<Boolean> bestSol = new ArrayList<>(actualSol);
        return mdrAux(towers, c, actualSol, 0, bestSol);
    }

    private static ArrayList<Boolean> mdrAux(ArrayList<Tower> towers, float c, ArrayList<Boolean> actualSol, int k, ArrayList<Boolean> bestSol) {
        if (towers.get(k).getCost() + getTotalCost(actualSol, towers) <= c) {
            actualSol.set(k, true); // Incluimos el elemento en la solucion
            if (k == towers.size() - 1) { // Es hoja, por tanto es una solucion que debemos de comprobar si es mejor o peor que la mejor que ya tenemos
                if (getTotalValue(actualSol, towers) > getTotalValue(bestSol, towers)) {
                    bestSol = new ArrayList<>(actualSol);
                    // return bestSol;
                }
            } else { // Si no es hoja, seguimos explorando, el siguiente nodo
                bestSol = mdrAux(towers, c, actualSol, k + 1, bestSol);
            }
        }

        // Cuando sale de la recursividad lo pone a false y explora la siguiente rama
        // Cuando sale de el if a false, es decir que no cabe el elemento lo que
        // haría sería podar poniendo a false
        // Sirve para estos dos casos
        actualSol.set(k, false); // Excluimos el elemento de la solucion, para explorar la otra rama si no hubiesemos metido el elemento
        if (k == towers.size() - 1) { // Es hoja, por tanto es una solucion que
            if (getTotalValue(actualSol, towers) > getTotalValue(bestSol, towers)) { //
                bestSol = new ArrayList<>(actualSol);
                // return bestSol;
            }

        } else {
               bestSol = mdrAux(towers, c, actualSol, k + 1, bestSol);
        }

        return bestSol;

    }

    // Obtenemos el VALOR de la solucion que nos pasan
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

    // Obtenemos el COSTE de la solucion que nos pasan
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

}

/*************************************************************
 * 
 * SEGUNDA IMPLEMENTACIÓN
 *
 *************************************************************/

// public class TowerBuyer {
//     public static float getTowerValue(Tower tower) {
//         float damage = tower.getDamage();
//         float cost = tower.getCost();

//         /*
//          * Esta fórmula tiene en cuenta los siguientes aspectos:
//          * - daño (Damage) contribuyen positivamente al puntaje.
//          * - El costo contribuye negativamente al puntaje,
//          * He metido mas campos en la formula pero con la que he consegudio mejor score
//          * es esta más simple
//          */
//         float score = damage / cost;

//         return score;

//     }

//     public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

//         ArrayList<Tower> actualSol = new ArrayList<>();
//         ArrayList<Tower> bestSol = new ArrayList<>();

//         explorer(towers, money, 0, actualSol, bestSol);

//         ArrayList<Integer> finalSolution = getIndexFromTower(bestSol, towers);
//         return finalSolution;

//     }

//     // Para obtener los indices de las torres que hemos seleccionado
//     public static ArrayList<Integer> getIndexFromTower(ArrayList<Tower> solutionTower,
//             ArrayList<Tower> listOriginalTowers) {

//         ArrayList<Integer> finalSolution = new ArrayList<>();
//         int i = 0;
//         int j = 0;
//         boolean seguir = true;

//         while (i < solutionTower.size()) {
//             j = 0;
//             seguir = true;
//             Tower towerSolution = solutionTower.get(i);
//             while (j < listOriginalTowers.size() && seguir) {
//                 Tower towerOriginal = listOriginalTowers.get(j);
//                 if (towerSolution.getId() == towerOriginal.getId()) {
//                     finalSolution.add(j);
//                     seguir = false;
//                 } else {
//                     j = j + 1;
//                 }
//             }
//             i = i + 1;
//         }
//         return finalSolution;
//     }

//     public static void explorer(ArrayList<Tower> towers, float c, int k, ArrayList<Tower> actualSol, ArrayList<Tower> bestSol) {
//         if (k == towers.size()) {
//             // Hemos llegado al final de la lista de elementos, es decir llegamos a una hoja
//             float actualValue = getTotalValue(actualSol);
//             float actualWeight = getTotalWeight(actualSol);

//             if (actualValue > getTotalValue(bestSol) && actualWeight <= c) {
//                 // Esta solución es mejor que la mejor conocida hasta ahora
//                 bestSol.clear();
//                 bestSol.addAll(actualSol);
//             }
//             return; 
//         }

//         // Incluir el elemento actual en la solución
//         actualSol.add(towers.get(k));
//         explorer(towers, c, k + 1, actualSol, bestSol);

//         // Excluir el elemento actual de la solución
//         actualSol.remove(actualSol.size() - 1);
//         explorer(towers, c, k + 1, actualSol, bestSol);
//     }

//     public static float getTotalValue(ArrayList<Tower> items) {
//         float value = 0;
//         for (Tower tower : items) {
//             value += getTowerValue(tower);
//         }
//         return value;
//     }

//     public static float getTotalWeight(ArrayList<Tower> items) {
//         float weight = 0;
//         for (Tower tower : items) {
//             weight += tower.getCost();
//         }
//         return weight;
//     }
// }
