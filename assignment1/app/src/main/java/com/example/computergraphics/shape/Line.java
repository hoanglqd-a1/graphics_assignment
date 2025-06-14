package com.example.computergraphics.shape;

import android.content.Context;

import com.example.computergraphics.utils.MyMatrixLib;

public class Line extends Shape {
    static float []  defaultSource = {
            -2.0f, 0.0f, -2.0f
    };
    static float [] defaultDirection = {
            0.1f, 0.0f, 0.1f
    };
    static int defaultMaxLength = 40;
    public float [] source;
    public float [] direction;
    public float [] endpoint;
    public float length;
    public Line(float [] Source, float [] Direction, float Length, Context context){
        super(context);
        source = Source;
        length = Length;
        float norm = (float) Math.sqrt(Direction[0] * Direction[0] + Direction[1] * Direction[1] + Direction[2] * Direction[2]);
        direction = new float[] {
                Direction[0] / norm,
                Direction[1] / norm,
                Direction[2] / norm,
        };
        endpoint = new float [] {
                source[0] + length * direction[0],
                source[1] + length * direction[1],
                source[2] + length * direction[2],
        };
        vertexData = new float[] {
                source[0], source[1], source[2],
                endpoint[0], endpoint[1], endpoint[2],
        };
        color = new float [] {0f, 0f, 1f, 1f};
        typ = 1;
        createBuffer();
    }
    public Line(float [] Source, float [] Direction, Context context){
        this(Source, Direction, defaultMaxLength, context);
    }
    public Line(Context context){
        this(defaultSource, defaultDirection, defaultMaxLength, context);
    }
    public Line(float [] v1, float [] v2, String i, Context context){
        this(
                v1,
                MyMatrixLib.sub(v2, v1),
                (float) Math.sqrt(
                        Math.pow(v2[0] - v1[0], 2) +
                                Math.pow(v2[1] - v1[1], 2) +
                                Math.pow(v2[2] - v1[2], 2)
                ),
                context
        );
    }
}

