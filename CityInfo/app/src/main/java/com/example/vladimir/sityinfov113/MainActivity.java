package com.example.vladimir.sityinfov113;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        // Проверяем поддерживается ли OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            //Задаем слой
            Toast.makeText(this, "Так то все збс", Toast.LENGTH_LONG).show();
        }

        else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.", Toast.LENGTH_LONG).show(); return;
        }

        setContentView(R.layout.main_activity);
        glSurfaceView = findViewById(R.id.OpenGLSurfaceViewID);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new OpenGLProjectRenderer());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glSurfaceView.onPause();
    }
}
