package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        ArrayList<Integer> selected = new ArrayList<>();
        for (int i = 0; i < towers.size(); i++) {
            if(money >= towers.get(i).getCost()) {
                money -= towers.get(i).getCost();
                selected.add(i);
            }
        }

        return selected;
    }
}
