package analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Dado un algoritmo, calcula sus tiempos suponiendo cada complejidad que tenemos en "complexities", haciendo
    // la operación del switch, por lo que obtenemos un map con complejidades y tiempos para un algoritmo
    // es decir el contenido de la tabla de la foto
    static Map<String, List<Double>> getTimes(Algorithm algorithm){
        int[] array_n = {2, 5, 13, 38, 125, 412, 1368, 4553, 15162, 27671};
        String[] complexities = {
            "1",
            "log(n)",
            "n",
            "n*log(n)",
            "n^2",
            "n^3",
            "2^n"
        };

        // Map guarda la complejidad que es con su lista de tiempos
        Map<String, List<Double>> result = new HashMap<String, List<Double>>();

        for(int i=0 ; i<complexities.length ; i++){
            List<Double> times = new ArrayList<Double>();
            Chronometer chrono = new Chronometer();
            long time;
            double time_result;
            int n;

            for(int j=0; j<array_n.length ; j++){
                n=array_n[j];
                algorithm.init(n);
                chrono.start();
                algorithm.run();
                chrono.stop();
                time=chrono.getElapsedTime();
                
                switch (complexities[i]) {
                    case "1":
                        time_result = (double) time;
                        break;
                    case "log(n)":
                        time_result = time/Math.log10(n); // Habría que poner log en base 2
                        break;
                    case "n":
                        time_result = time/n;
                        break;
                    case "n*log(n)":
                        time_result = time/n*Math.log10(n);
                        break;
                    case "n^2":
                        time_result = time/Math.pow(n,2);
                        break;
                    case "n^3":
                        time_result = time/Math.pow(n,3);
                        break;
                    case "2^n":
                        time_result = time/Math.pow(2,n);
                        break;
                    default:
                        throw new RuntimeException("ERROR: getTimes() switch default");
                }

                times.add(time_result);
            }

            result.put(complexities[i], times);
        }
        
        return result;

    }

    // max -> 10 s -> 10.000 ms
    public static ArrayList<Double> sacarTiempos(Algorithm algorithm) {
		
		int n=1,cont=0;
		ArrayList<Double> tiempos = new ArrayList<Double>();
		Chronometer t = new Chronometer();
		
		while(cont<17 && (double) t.getElapsedTime()< 117 ) {  // CUIDADO AHORA ESTAMOS EN MS ADAPTAR DESDE NS
        //Pongo (0,1117 s -> 117,64 ms) ya que si no falla nada entre el for y el while se ejecutaria 85 veces el algoritmo
        //10/85 sale a 0,1117 s , es decir cada vuelta a este bucle tiene que durar como máximo 0,11s ya que si fuera más y se ejecutara las 85 veces pasaríamos el tiempo MAX de ejecucion por algoritmo			
            algorithm.init(n);
            t.start();
			algorithm.run();
			t.stop();
			tiempos.add((double) t.getElapsedTime());
			//System.out.println(t.getElapsedTime()+" --> "+n);
			
			n=n*2;
			cont++;
		}

        return tiempos;
    }
		
		

    // Tabla 3 y 4
    public static double[] sacarMedia(Algorithm algorithm) {
		
		double[] tmedia = new double[17];
		for(int i=0; i<5; i++) {
			ArrayList<Double> tiempos = sacarTiempos(algorithm);
			// System.out.println(tiempos.toString());
			for (int x = 0; x < tiempos.size(); x++) {	  
				  tmedia[x]=tmedia[x]+tiempos.get(x);	  
			}
		}
		
		for(int i=0;i<tmedia.length;i++) {
			tmedia[i]=tmedia[i]/5;
		}
		// System.out.println(Arrays.toString(tmedia));
		
		return tmedia;
	}

    static String findComplexityOf(Algorithm algorithm, long maxExecutionTime) {
        // Modify the content of this method in order to find the complexity of the given algorithm.
        // You can delete any of the following instructions if you don't need them. You can also
        // add new instructions or new methods, but you cannot modify the signature of this method
        // nor the existing methods.

        //Ir midiendo tiempos variando la n, y formamos una tabla, vemos la ultima n más grande y la que mas se acerque
        //a una CTE es esa la complejidad, si una fila es 0 pues la complejidad es del anterior

        // Map<String, List<Double>> complexity_times=getTimes(algorithm);
        double[] tmedia = sacarMedia(algorithm);
        System.out.println(Arrays.toString(tmedia));
        // System.out.println(complexity_times.toString());

        return "----THIS IS A TEST----";
    }
}
