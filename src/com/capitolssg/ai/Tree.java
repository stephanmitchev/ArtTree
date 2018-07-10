package com.capitolssg.ai;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ColorProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Tree {

    public enum Feature {
        Wood,
        Leaf,
        Root,
        Empty
    }

    private ArrayList<Feature> original;
    ArrayList<Feature> pruned;

    private Double score = 0.0;

    Double getScore() {
        return score;
    }

    private int width, height, groundIndex;
    private final float rootFactor = 0.3f;


    Tree(int w, int h, double woodPct, double leafPct, double rootPct) {
        width = w;
        height = h;
        groundIndex = Math.round(width * (height * (1 - rootFactor) - 1));

        Random rand = new Random();

        original = new ArrayList<>(width * height);

        for (int i = 0; i < groundIndex; i++) {
            double random = rand.nextDouble();

            if (woodPct > random) {
                original.add(Feature.Wood);
            } else if (leafPct > random) {
                original.add(Feature.Leaf);
            } else original.add(Feature.Empty);
        }

        // Start with a sapling in the middle of the last line
        for (int i = 0; i < width; i++) {
            if (i < width / 2 - 1 || i > width / 2 + 1) {
                original.add(Feature.Empty);
            } else original.add(Feature.Wood);
        }
        // Start with a simple root under the sapling
        for (int i = 0; i < width; i++) {
            if (i < width / 2 - 1 || i > width / 2 + 1) {
                original.add(Feature.Empty);
            } else original.add(Feature.Root);
        }

        // Do some random roots
        for (int i = groundIndex + 2 * width; i < width * height; i++) {


            if (rootPct > rand.nextFloat()) {
                original.add(Feature.Root);
            } else original.add(Feature.Empty);
        }

        prune();
        evaluate();
    }

    private Tree prune() {

        pruned = (ArrayList<Feature>) original.clone();

        // Wood must be supported by wood under it or it falls
        for (int idx = groundIndex - 1; idx >= 0; idx--) {
            if (pruned.get(idx) == Feature.Wood && noFeatureUnder(pruned, idx, Feature.Wood)) {
                pruned.set(idx, Feature.Empty);
            }
        }

        // Leafs must have wood around them or they fall
        for (int idx = 0; idx < groundIndex; idx++) {
            if (pruned.get(idx) == Feature.Leaf && noFeatureAround(pruned, idx, Feature.Wood)) {
                pruned.set(idx, Feature.Empty);
            }
        }

        // Roots must be supported by another root above it or it dies
        for (int idx = groundIndex + 2 * width; idx < width * height; idx++) {
            if (pruned.get(idx) == Feature.Root && noFeatureAbove(pruned, idx, Feature.Root)) {
                pruned.set(idx, Feature.Empty);
            }
        }

        return this;

    }

    private void evaluate() {
        score = 0.0;

        // Data points
        int treeHeight = groundIndex / width - topMostFeature(pruned, Feature.Wood) / width;
        int rootDepth = bottomMostFeature(pruned, Feature.Root) / width - groundIndex / width;
        int countRoots = featureCount(pruned, Feature.Root);
        int countWood = featureCount(pruned, Feature.Wood);

        // Root health will become a factor after tree reaches 10 units
        double rootDepthHealth = Math.min(1, Math.max(0, Math.pow(1 / (rootFactor * treeHeight / rootDepth), 2)));
        double rootVolumeHealth = Math.min(1, Math.max(0, Math.pow(1 / (rootFactor * countWood / countRoots), 2)));

        // Raw fitness is the number of leaves
        for (Feature p : pruned) {
            if (p == Feature.Leaf)
                score = score + 1.0 / (width * height);
        }

        // Penalize unevenly long roots
        if (treeHeight > 1 && rootDepth > 1) {
            score = score * rootDepthHealth;
        }

        // Maintain root/trunk balance
        score = score * rootVolumeHealth;

    }


    static void render(List<Feature> t, String file, int w, int h) {
        ColorProcessor cpResult = new ColorProcessor(w, h);

        for (int i = 0; i < t.size(); i++) {
            cpResult.set(i, t.get(i) == Feature.Leaf ? 0x0000ff00 :
                    t.get(i) == Feature.Wood ? 0x008B4513 :
                            t.get(i) == Feature.Root ? 0x00BB8553 :
                                    0x00ffffff);
        }

        ImagePlus ipDest = new ImagePlus(file, cpResult);
        FileSaver fs = new FileSaver(ipDest);
        fs.saveAsPng(file);
    }


    Tree randomCrossover(Tree t2, double noiseFactor) {
        Tree t = new Tree(width, height, 0, 0, 0);

        Random rand = new Random();

        for (int i = 0; i < width * height; i++) {

            // Copy the ground layer
            if (i >= groundIndex && i < groundIndex + 2 * width) {
                t.original.set(i, this.original.get(i));
            }

            if (rand.nextBoolean()) {
                t.original.set(i, t2.original.get(i));
            } else {
                t.original.set(i, this.original.get(i));
            }

            if (noiseFactor > rand.nextDouble()) {
                double r = rand.nextDouble();
                if (i < t.groundIndex - t.width) {
                    Feature f = r > 0.666 ? Feature.Empty : r > 0.333 ? Feature.Leaf : Feature.Wood;
                    t.original.set(i, f);
                }
                if (i > t.groundIndex + 2 * t.width) {
                    Feature f = r > 0.5 ? Feature.Empty : Feature.Root;
                    t.original.set(i, f);
                }
            }
        }


        t.prune().evaluate();

        return t;
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

    private int topMostFeature(List<Feature> t, Feature f) {
        for (int i = 0; i < width * height; i++) {
            if (t.get(i) == f) {
                return i;
            }
        }

        return -1;
    }

    private int bottomMostFeature(List<Feature> t, Feature f) {
        for (int i = width * height - 1; i >= 0; i--) {
            if (t.get(i) == f) {
                return i;
            }
        }

        return -1;
    }

    private int featureCount(List<Feature> t, Feature f) {
        int count = 0;

        for (int i = 0; i < width * height; i++) {
            if (t.get(i) == f) {
                count++;
            }
        }

        return count;
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

    private boolean noFeatureAbove(List<Feature> t, int idx, Feature f) {
        boolean found =
                indexT(idx) != -1 && t.get(indexT(idx)) == f
                        || indexTL(idx) != -1 && t.get(indexTL(idx)) == f
                        || indexTR(idx) != -1 && t.get(indexTR(idx)) == f;
        return !found;
    }


}
