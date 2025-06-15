package com.example.computergraphics.utils;

import com.example.computergraphics.shape.Line;

public class MyComposite {
    public float [][] intersections;
    public Line [] lines;

    public MyComposite(float[][] Intersections, Line[] Lines) {
        intersections = Intersections;
        lines = Lines;
    }
}
