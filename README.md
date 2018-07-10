# ArtTree
An evolutionary algorithm to build a really basic tree.

I was inspired by [this project](https://magenta.as/why-i-used-an-evolutionary-algorithm-to-create-pixel-art-ba530e71c5e9) 
developed and beautifully documented by William Anderson and I wanted to see what can I hack in a day. 
This is a VERY simple EA implementation for growing a tree.

![500 generations](img/gen500.gif)

Each tree starts as a set of random features that represent leaves, pieces of wood, or roots. A pruning function 
cleans out the features according to the following rules:
* a wood feature must be supported by another wood feature under it
* a leaf must have a wood feature as a neighbor
* a root feature must have a connecting root feature above it 

All evolutionary algorithms, after initialization, rely on two major operations:
* Create a generation of trees through some method of reproduction
* Select survivors based on some fitness function

For this project, a generation is constructed by selecting 2 random trees and select random features from each to create 
the offspring - this is far from remotely being realistic, but the implementation is rather simple. It can also be 
easily augmented with some abstract representation of a DNA.

The fitness function is also quite basic - it is linearly proportional to the number of leaves on the tree. Penalties 
are added if roots are not within about a third of the plant size by depth and by volume.

The code uses ImageJ to draw the PNGs and the animated GIF was created with [GIFMaker.me](http://gifmaker.me/)

Fork and have fun!
