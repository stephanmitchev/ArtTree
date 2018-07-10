package com.capitolssg.ai;

public class Main {

    public static void main(String[] args) {

        TreeEvolver te = new TreeEvolver(64);
        te.evolve(1000, 8, 0.01);

        for(int i = 0; i < te.generation.size(); i++){
            Tree t = te.generation.get(i);
            Tree.render(t.pruned, i + ".png", t.width, t.height );
        }
    }
}
