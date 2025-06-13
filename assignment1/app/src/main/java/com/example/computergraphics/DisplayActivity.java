package com.example.computergraphics;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

public class DisplayActivity extends Activity {
    private MyGLSurfaceView glView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        float [] source = intent.getFloatArrayExtra(MainActivity.EXTRA_RAY_SOURCE);
        float [] direct = intent.getFloatArrayExtra(MainActivity.EXTRA_RAY_DIRECTION);
        glView = new MyGLSurfaceView(this, source, direct);
        setContentView(glView);
    }
}