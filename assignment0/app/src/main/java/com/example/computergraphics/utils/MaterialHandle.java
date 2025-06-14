package com.example.computergraphics.utils;

public class MaterialHandle {
    public String name;
    public float [] Ka = { 0.0f, 0.0f, 0.0f };
    public float [] Kd = { 0.0f, 0.0f, 0.0f };
    public float [] Ks = { 0.0f, 0.0f, 0.0f };
    public float Ns = 0.0f;
    public int resourceId = -1;
    public int textureHandle = -1;
    public MaterialHandle(String name) {
        this.name = name;
    }
}
