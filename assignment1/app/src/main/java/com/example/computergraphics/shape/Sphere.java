package com.example.computergraphics.shape;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

public class Sphere extends Shape{
    public float [] center;
    public float radius;
    public Sphere (float [] center, float radius, Context context) throws IOException {
        super(context.getAssets().open("sphere.obj"), context);
        this.center = center;
        this.radius = radius;
        for(int i=0; i<vertexData.length; i+=3){
            vertexData[i  ] = vertexData[i  ] * radius + center[0];
            vertexData[i+1] = vertexData[i+1] * radius + center[1];
            vertexData[i+2] = vertexData[i+2] * radius + center[2];
        }
        createBuffer();
    }
    public Sphere(Context context) throws IOException {
        this(new float[] {0f, 0f, 0f}, 1f, context);
    }
}
