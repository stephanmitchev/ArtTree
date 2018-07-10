package com.capitolssg.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class TreeEvolver {

    private List<Tree> generation;
    int generationSize;


    TreeEvolver(int population, int width, int height) {
        generationSize = population;

        generation = new ArrayList<>();
        for (int j = 0; j < generationSize; j++) {
            generation.add(new Tree(width, height, 0.1, 0.3, 0.01));
        }

    }

    void evolve(double cutoffFactor, double noiseFactor) {


            ArrayList<Tree> survivors = selectSurvivors(cutoffFactor);

            generation = new ArrayList<>();

            Random rand = new Random();
            for (int j = 0; j < generationSize; j++) {

                Tree t1 = survivors.get(rand.nextInt(survivors.size()));
                Tree t2 = survivors.get(rand.nextInt(survivors.size()));

                Tree t = t1.randomCrossover(t2, noiseFactor);
                generation.add(t);
            }


    }

    ArrayList<Tree> selectSurvivors(double cutoffFactor) {

        generation.sort(Comparator.comparing(Tree::getScore));

        ArrayList<Tree> survivors = new ArrayList<>();

        for (int s = 0; s < cutoffFactor * generationSize; s++) {
            survivors.add(generation.get(generationSize - 1 - s));
        }

        return survivors;
    }

}
