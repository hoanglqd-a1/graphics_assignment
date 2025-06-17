package com.example.computergraphics.shape;


import android.content.Context;

import com.example.computergraphics.utils.MyMatrixLib;

public class Hexa extends Shape {
     static float radius = 0.01f;
     public Hexa(float [] normal, float [] center, Context context){
         super(context);
         // Pick an arbitrary vector not parallel to the normal
         float[] arbitrary = (Math.abs(normal[0]) < 0.9) ? new float[]{1, 0, 0} : new float[]{0, 1, 0};

         // Use cross product to get a perpendicular vector in the plane
         float[] u = MyMatrixLib.normalize(MyMatrixLib.crossProduct(normal, arbitrary));

         // Another perpendicular vector in the plane
         float[] v = MyMatrixLib.normalize(MyMatrixLib.crossProduct(normal, u));

         float[][] points = new float[6][3];

         for (int i = 0; i < 6; i++) {
             double angle = 2 * Math.PI * i / 6;
             float cosA = (float)Math.cos(angle);
             float sinA = (float)Math.sin(angle);

             points[i][0] = center[0] + radius * (u[0]*cosA + v[0]*sinA);
             points[i][1] = center[1] + radius * (u[1]*cosA + v[1]*sinA);
             points[i][2] = center[2] + radius * (u[2]*cosA + v[2]*sinA);
         }

         float [] coordinates = new float [21];
         coordinates[0] = center[0];
         coordinates[1] = center[1];
         coordinates[2] = center[2];
         for (int i=0; i<6; i++){
             for (int j=0; j<3; j++){
                 coordinates[i * 3 + j + 3] = points[i][j];
             }
         }
         int [] drawOrder = new int [] {
             0, 1, 2,
             0, 2, 3,
             0, 3, 4,
             0, 4, 5,
             0, 5, 6,
             0, 6, 1,
         };
         float normalVertex [] = new float [21];
         for (int i=0; i<7; i++){
             normalVertex[i*3  ] = normal[0];
             normalVertex[i*3+1] = normal[1];
             normalVertex[i*3+2] = normal[2];
         }
         this.vertexData = this.createData(coordinates, drawOrder, COORDS_PER_VERTEX);
         this.normalData = this.createData(normalVertex, drawOrder, COORDS_PER_VERTEX);
         this.color = new float [] {1.0f, 0.0f, 0.0f, 1.0f};
         this.createBuffer();
     }
 }
