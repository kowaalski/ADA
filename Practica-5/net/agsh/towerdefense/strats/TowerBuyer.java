/*
Sustituir este comentario por una explicación de la formula o procedimiento empleado para determinar el valor de una
torreta.
*/

package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {
        // This is just a (bad) example. Replace ALL of this with your own code.
        // The ArrayList<Integer> returned is a list of the indices of the towers you want to buy.
        // For example, if you want to buy the first and third towers, return [0, 2].
        // The selected towers must be affordable, and the total cost must be less than or equal to money.
        // The indices should be given in the order that the towers are given in the original ArrayList<Tower> towers.

        // Create an ArrayList<Integer> to store the indices of the towers you want to buy.
        ArrayList<Integer> selected = new ArrayList<>();

        // Loop through the towers.
        for (int i = 0; i < towers.size(); i++) {
            // If the tower is affordable, buy it.
            if(money >= towers.get(i).getCost()) {
                // Subtract the cost of the tower from money.
                money -= towers.get(i).getCost();
                // Add the index of the tower to selected.
                selected.add(i);
            }
        }

        return selected;
    }
}
