package com.capitolssg.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TreeEvolver {

    public List<Tree> generation;
    int generationSize;


    public TreeEvolver(int population) {
        generationSize = population;

        generation = new ArrayList<>(generationSize);
        for (int j = 0; j < generationSize; j++) {
            generation.add(new Tree(0.1, 0.3));
        }

    }

    public void evolve(int generations, int cutoff, double noiseFactor) {

        for (int i = 0; i < generations; i++) {


            generation.sort(Comparator.comparing(Tree::getScore));

            ArrayList<Tree> survivors = new ArrayList<>();

            for (int s = 0; s < cutoff; s++) {
                if (s == 0) {
                    System.out.println("Max Gen "+ i +" Score: " + generation.get(generationSize - 1).getScore());
                }
                survivors.add(generation.get(generationSize - 1 - s));
            }

            generation = new ArrayList<>(generationSize);

            Random rand = new Random();
            for (int j = 0; j < generationSize; j++) {

                Tree t1 = survivors.get(rand.nextInt(survivors.size()));
                Tree t2 = survivors.get(rand.nextInt(survivors.size()));

                Tree t = t1.randomCrossover(t2, noiseFactor);
                generation.add(t);
            }

        }


    }

}
