package com.example.computergraphics;

public class Material {
    public String name;
    public float [] Ka = { 1.0f, 1.0f, 1.0f };
    public float [] Kd = { 0.0f, 0.0f, 0.0f };
    public float [] Ks = { 0.0f, 0.0f, 0.0f };
    public float Ns = 0.0f;
    public int resourceId = -1;
    public int textureHandle = -1;
    public Material(String name) {
        this.name = name;
    }
}
