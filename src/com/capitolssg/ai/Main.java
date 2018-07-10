package com.capitolssg.ai;


public class Main {

    public static void main(String[] args) {

        // Image dimensions
        int width = 50;
        int height = 200;

        // How many generations to evolve
        int generations = 1000;

        // Size of each generation
        int population = 50;

        // What fraction of a generation will survive
        double cutoffFactor = 0.1;

        // What is the probability of a random feature
        double noiseFactor = 0.01;




        TreeEvolver te = new TreeEvolver(population, width, height);

        for (int i = 0; i < generations; i++) {

            te.evolve(cutoffFactor, noiseFactor);

            // Render and show fitness of best individual of every 10th generation
            if(i % 10 == 0 || i == generations - 1) {
                int ord = i + 1;

                Tree best = te.selectSurvivors(0.1).get(0);
                System.out.println(ord + ", " + best.getScore());
                Tree.render(best.pruned, "rendered/best_" + ord + ".png", width, height);
            }
        }
    }
}
