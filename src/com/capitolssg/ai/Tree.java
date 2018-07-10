package com.capitolssg.ai;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ColorProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tree {


    private Tree prune() {

        pruned = (ArrayList<Feature>) original.clone();

        // Wood must be supported by wood under it or it falls
        for (int idx = pruned.size() - width - 1; idx >= 0; idx--) {
            if (pruned.get(idx) == Feature.Wood && noFeatureUnder(pruned, idx, Feature.Wood)) {
                pruned.set(idx, Feature.Empty);
            }
        }

        // Leafs must have wood around them or they fall
        for (int idx = 0; idx < pruned.size(); idx++) {
            if (pruned.get(idx) == Feature.Leaf && noFeatureAround(pruned, idx, Feature.Wood)) {
                pruned.set(idx, Feature.Empty);
            }
        }

        return this;

    }

    private Tree evaluate() {
        score = 0.0;

        for (int i = 0; i < pruned.size(); i++) {
            if (pruned.get(i) == Feature.Leaf)
                score = score + 1.0 / (width * height);
        }

        return this;
    }


    static void render(List<Feature> t, String file, int w, int h) {
        ColorProcessor cpResult = new ColorProcessor(w, h);

        for (int i = 0; i < t.size(); i++) {
            cpResult.set(i, t.get(i) == Feature.Leaf ? 0x0000ff00 : t.get(i) == Feature.Wood ? 0x008B4513 : 0x00ffffff);
        }

        ImagePlus ipDest = new ImagePlus(file, cpResult);
        FileSaver fs = new FileSaver(ipDest);
        fs.saveAsPng(file);
    }


    Tree randomCrossover(Tree t2, double noiseFactor) {
        Tree t = new Tree(0, 0);

        Random rand = new Random();

        for (int i = 0; i < original.size() - width; i++) {

            if (rand.nextBoolean()) {
                t.original.set(i, t2.original.get(i));
            } else {
                t.original.set(i, this.original.get(i));
            }

            if (noiseFactor > rand.nextDouble()) {
                double r = rand.nextDouble();
                Feature f = r > 0.666 ? Feature.Empty : r > 0.333 ? Feature.Leaf : Feature.Wood;
                t.original.set(i, f);
            }
        }

        t.prune().evaluate();

        return t;
    }

    public enum Feature {
        Wood,
        Leaf,
        Empty
    }

    public ArrayList<Feature> original;
    public ArrayList<Feature> pruned;

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double score = 0.0;

    int width = 50;
    int height = 50;

    public Tree(double woodPct, double leafPct) {
        original = new ArrayList<>(width * height);

        for (int i = 0; i < width * (height - 1); i++) {
            double random = Math.random();

            if (woodPct > random) {
                original.add(Feature.Wood);
            } else if (leafPct > random) {
                original.add(Feature.Leaf);
            } else original.add(Feature.Empty);
        }

        // Start with a sapling in the middle of the last line
        for (int i = 0; i < width; i++) {

            if (i < width / 2 -1 || i > width / 2 + 1) {
                original.add(Feature.Empty);
            } else original.add(Feature.Wood);
        }

        prune();
        evaluate();
    }

    private int indexL(int indexCurrent) {
        return (indexCurrent % width == 0) ? -1 : indexCurrent - 1;
    }

    private int indexR(int indexCurrent) {
        return (indexCurrent % width == width - 1) ? -1 : indexCurrent + 1;
    }

    private int indexT(int indexCurrent) {
        return (indexCurrent / height == 0) ? -1 : indexCurrent - width;
    }

    private int indexB(int indexCurrent) {
        return (indexCurrent / height == height - 1) ? -1 : indexCurrent + width;
    }

    private int indexTL(int indexCurrent) {
        return (indexL(indexCurrent) == -1 || indexT(indexCurrent) == -1) ? -1 : indexCurrent - width - 1;
    }

    private int indexTR(int indexCurrent) {
        return (indexR(indexCurrent) == -1 || indexT(indexCurrent) == -1) ? -1 : indexCurrent - width + 1;
    }

    private int indexBL(int indexCurrent) {
        return (indexL(indexCurrent) == -1 || indexB(indexCurrent) == -1) ? -1 : indexCurrent + width - 1;
    }

    private int indexBR(int indexCurrent) {
        return (indexR(indexCurrent) == -1 || indexB(indexCurrent) == -1) ? -1 : indexCurrent + width + 1;
    }


    private boolean noFeatureAround(List<Feature> t, int idx, Feature f) {
        boolean found =
                indexT(idx) != -1 && t.get(indexT(idx)) == f
                        || indexB(idx) != -1 && t.get(indexB(idx)) == f
                        || indexL(idx) != -1 && t.get(indexL(idx)) == f
                        || indexR(idx) != -1 && t.get(indexR(idx)) == f
                        || indexTL(idx) != -1 && t.get(indexTL(idx)) == f
                        || indexTR(idx) != -1 && t.get(indexTR(idx)) == f
                        || indexBL(idx) != -1 && t.get(indexBL(idx)) == f
                        || indexBR(idx) != -1 && t.get(indexBR(idx)) == f;
        return !found;
    }


    private boolean noFeatureUnder(List<Feature> t, int idx, Feature f) {
        boolean found =
                indexB(idx) != -1 && t.get(indexB(idx)) == f
                        || indexBL(idx) != -1 && t.get(indexBL(idx)) == f
                        || indexBR(idx) != -1 && t.get(indexBR(idx)) == f;
        return !found;
    }



}
