package com.example.computergraphics.object;

import android.content.Context;

public class Point extends GraphicObject {
    public final int typ = 0;
    public float [] color = new float [] {1f, 0f, 0f, 1f};
    public Point(float [] coordinate, Context context){
        super(context);
        vertexData = coordinate;
        createBuffer();
    }
}
