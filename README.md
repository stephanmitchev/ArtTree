# ArtTree
An evolutionary algoritm to build a really basic tree.

I was inspired by [this project](https://magenta.as/why-i-used-an-evolutionary-algorithm-to-create-pixel-art-ba530e71c5e9) 
developed and beautifully documented by William Anderson and I wanted to see what can I hack in a day. 
This is a VERY simple EA implementation for growing a tree.

![500 generations](img/gen500.gif)

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
