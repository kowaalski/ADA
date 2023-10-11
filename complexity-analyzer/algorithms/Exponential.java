package algorithms;

import analyzer.Algorithm;
import analyzer.Chronometer;

public class Exponential implements Algorithm {
    long sleep;

    @Override
    public String getName() {
        return "Exponential";
    }

    @Override
    public void init(long n) {
        this.sleep = n;
    }

    @Override
    public void reset() {
    }

    @Override
    public void run() {
        try {
            Thread.sleep((long) Math.pow(2, this.sleep));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getSolution() {
        return "sleep="+ this.sleep;
    }

    @Override
    public String getComplexity() {
        return "2^n";
    }

    @Override
    public long getMaxSize() {
        return 15;
    }

    public static void main(String[] args) {
        Exponential algorithm = new Exponential();
        Chronometer chronometer = new Chronometer();
        chronometer.start();
        chronometer.pause();
        for (int i = 10; i < 10000; i *= 10) {
            algorithm.init(i);
            chronometer.resume();
            algorithm.run();
            chronometer.pause();
            System.out.println("n=" + i + " in " + chronometer.getElapsedTime() + "ms");
        }
    }
}
