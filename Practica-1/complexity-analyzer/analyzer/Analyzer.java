package analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Analyzer implements Runnable {
    Algorithm algorithm;
    long maxExecutionTime;
    String complexity = null;

    public Analyzer(Algorithm algorithm, long maxExecutionTime) {
        this.algorithm = algorithm;
        this.maxExecutionTime = maxExecutionTime;
    }

    public String getComplexity() {
        return complexity;
    }

    @Override
    public void run() {
        complexity = findComplexityOf(algorithm, maxExecutionTime);
    }

    public static String masCercano(double ratio) {

        /// RATIOS NUEVOS /////
        if (ratio < 1.0) {
            return "1";
        } else if (1.0 <= ratio && ratio <= 1.4) {
            return "log(n)";
        } else if (1.4 < ratio && ratio < 2.05) {
            return "n";
        } else if (2.05 <= ratio && ratio < 3.0) {
            return "n*log(n)";
        } else if (3 <= ratio && ratio < 6.0) {
            return "n^2";
        } else {
            return "n^3";
        }

        ////******* RATIOS OLD ******* //////
        // if (ratio < 1) {
        // // System.out.println("+1 pto --> 1"); // aprox 1.0
        // return "1";
        // }else if (1 <= ratio && ratio <= 1.4){ //LOGN esta entre 1 y N, por lo que he
        //// ido probando valores entre ese rango hasta que he dado con la clave
        // // System.out.println("+1 pto --> log(n)");
        // return "log(n)";
        // }else if (1.25 <= ratio && ratio < 2.05) { // aprox 2.0
        // // System.out.println("+1 pto --> n");
        // return "n";
        // } else if(1.4 <= ratio && ratio < 3.0){
        // // System.out.println("+1 pto --> n*log(n)");
        // return "n*log(n)";
        // }else if (3 <= ratio && ratio < 6.0) { // aprox 4.0
        // // System.out.println("+1 pto --> n^2");
        // return "n^2";
        // } else { // aprox 8.0
        // // System.out.println("+1 pto --> n^3");
        // return "n^3";
        // }
    }

    public static String masCercano(ArrayList<Double> ratios) {

        Map<String, Integer> Complejidades = new TreeMap<String, Integer>();
        Complejidades.put("1", 0);
        Complejidades.put("n", 0);
        Complejidades.put("n^2", 0);
        Complejidades.put("n^3", 0);
        Complejidades.put("log(n)", 0);
        Complejidades.put("n*log(n)", 0);
        // Complejidades.put("NF", 0);

        // System.out.println(ratios.toString());
        for (int i = 0; i < ratios.size(); i++) {
            // System.out.println(ratio);
            if (ratios.get(i) != 0.0) {
                String orden = masCercano(ratios.get(i));
                // System.out.println(orden);
                Complejidades.replace(orden, Complejidades.get(orden) + 1);
            }

        }

        int max = 0;
        String compl = "";
        for (Map.Entry<String, Integer> entry : Complejidades.entrySet()) {
            if (entry.getValue() >= max) {
                max = entry.getValue();
                compl = entry.getKey();
            }
        }

        // System.out.println(Complejidades);

        return compl;

    }

    // max -> 10 s -> 10.000 ms
    public static ArrayList<Long> sacarTiempos(Algorithm algorithm) {

        int n = 1, cont = 0;
        

        ArrayList<Long> tiempos = new ArrayList<Long>();
        Chronometer t = new Chronometer();
        while (cont < 17 && t.getElapsedTime() < 117) {
            // Pongo (0,1117 s -> 117,64 ms) ya que si no falla nada entre el for y el while
            // se ejecutaria 85 veces el algoritmo
            // 10/85 sale a 0,1117 s , es decir cada vuelta a este bucle tiene que durar
            // como máximo 0,11s ya que si fuera más y se ejecutara las 85 veces pasaríamos
            // el tiempo MAX de ejecucion por algoritmo

            algorithm.init(n);
            t.start();
            algorithm.run();
            t.stop();
            tiempos.add(t.getElapsedTime());
            n = n * 2;
            cont++;
        }



        //**************************************************************************************************** //
        //****** INTENTO DE AJUSTAR PARA LOS ALGORITMOS QUE NECESITAN ENTRADAS MÁS GRANDES ******************* //
        //**************************************************************************************************** //
        
        // int sumaTiempos=0;
        // int[] array_n = {2, 5, 13, 38, 125, 412, 1368, 4553, 15162, 27671, 50512,
        // 92172, 200000000};

        // Si tarda demasiado poco es que es una complejidad muy baja, por lo que para
        // estudiarla bien y diferenciarla de las demas
        // debemos hacerlo con 'n' mayores, los nuevos tiempos los machacamos a los
        // anteriores usando set
        // cont=0;
        // if(sumaTiempos<500){
        // while(cont<17 && (double) t.getElapsedTime()< 117 ) {
            // algorithm.init(n);
            // t.start();
            // algorithm.run();
            // t.stop();
            // tiempos.set(cont, (double) t.getElapsedTime());
            // //System.out.println(t.getElapsedTime()+" --> "+n);
            // n=n*2;
            // cont++;
            // }
            // }
        // // como max la `n` llega a 131.072
        // System.out.println(n);

        // System.out.println(tiempos.toString());

        return tiempos;
    }

    // Tabla 3 y 4
    public static double[] sacarMedia(Algorithm algorithm) {

        double[] tmedia = new double[17];
        for (int i = 0; i < 5; i++) {
            ArrayList<Long> tiempos = sacarTiempos(algorithm);
            // System.out.println(tiempos.toString());
            for (int x = 0; x < tiempos.size(); x++) {
                tmedia[x] = tmedia[x] + tiempos.get(x);
            }
        }

        for (int i = 0; i < tmedia.length; i++) {
            tmedia[i] = tmedia[i] / 5;
        }
        // System.out.println(Arrays.toString(tmedia));

        return tmedia;
    }

    public static ArrayList<Double> sacarRatios(double[] tmedia) {
        int contCeros = 0;
        ArrayList<Double> ratios = new ArrayList<Double>();

        for (int i = 0; i < tmedia.length - 1; i++) {
            double ratio = tmedia[i] != 0 ? tmedia[i + 1] / tmedia[i] : 0;
            if (Double.isFinite(ratio)) {
                ratios.add(ratio);
            }
        }

        contCeros = comprobarCeros(ratios);

        // Si hay mas de 14 ceros es decir más de la mitad de los ratios son 0 es que el
        // orden tiene que ser constante
        if (contCeros > 14) {
            throw new RuntimeException("Es de orden constante");
        }

        return ratios;
    }

    public static int comprobarCeros(ArrayList<Double> ratios) {
        int contCeros = 0;

        for (int i = 0; i < ratios.size(); i++) {
            if (ratios.get(i) == 0.0) {
                contCeros++;
            }
        }

        return contCeros;
    }

    static String findComplexityOf(Algorithm algorithm, long maxExecutionTime) {

        String complejidad;
        Chronometer t = new Chronometer();
        algorithm.init(13); // Este valor ha sido obtenido a tanteo, para que el tiempo de ejecución sea
                            // alrededor de 10 segundos para los algoritmos de complejidad 2^n
        t.start();
        algorithm.run();
        t.stop();

        if (t.getElapsedTime() > maxExecutionTime / 2) { // Si tarda más de la mitad del tiempo es que tiene que tener
                                                         // una complejidad alta como 2^n
            complejidad = "2^n";
        } else {
            double[] tmedia = sacarMedia(algorithm);
            try {
                ArrayList<Double> ratios = sacarRatios(tmedia);
                complejidad = masCercano(ratios);

            } catch (RuntimeException e) { // Si salta excepcion es que es de orden constante
                // System.out.println(e.getMessage());
                complejidad = "1";
            }
        }

        return complejidad;
    }
}
