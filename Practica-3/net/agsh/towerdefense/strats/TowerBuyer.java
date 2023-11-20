package net.agsh.towerdefense.strats;

import net.agsh.towerdefense.Tower;

import java.util.ArrayList;

public class TowerBuyer {

    public static ArrayList<Integer> buyTowers(ArrayList<Tower> towers, float money) {

        System.out.println("// test seteo del proyecto correcto //");
        System.out.println("///////////////////////////");
        ArrayList<Integer> selected = new ArrayList<>();
        for (int i = 0; i < towers.size(); i++) {
            if(money >= towers.get(i).getCost()) {
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

    //  public static ArrayList<Integer> initTable(ArrayList<Tower> towers, float money) {

    //     // Cada torreta es una fila
    //     // Valores discretos desde 0 hasta el total de money
    //     int files = towers.size();
    //     int[][] table = new int[files][money];

    //     return selected;
    // }
}
