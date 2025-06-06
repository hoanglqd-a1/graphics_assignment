package com.example.computergraphics;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {
    private GLSurfaceView gLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gLView = new MyGLSurfaceView(this);
        setContentView(gLView);
    }
}

