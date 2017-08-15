package com.example.vladimir.newopengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import javax.microedition.khronos.opengles.GL;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GLSurfaceView glSurfaceView;
    private ListView listView;
    private String [] drawerItems;

    private String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (!supportES2()) {
            Toast.makeText(this, "OpenGL ES 2.0 is not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Для вида
        setContentView(R.layout.activity_main);
        glSurfaceView = findViewById(R.id.OpenGLSurfaceViewID);
        glSurfaceView.setEGLContextClientVersion(2);

        //в случае чего нажать бэкспейс и раскоментить
        glSurfaceView.setRenderer(new OpenGLRenderer(this));

        //Кнопки + -
        Button ZoomIn = findViewById(R.id.ZoomIn);
        Button ZoomOut = findViewById(R.id.ZoomOut);
        //Обработка + -
        ZoomOut.setOnClickListener(ZoomOutListener);
        ZoomIn.setOnClickListener(ZoomInListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    //ИтемКлик для адаптера
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Toast.makeText(this,drawerItems[position]+ " was selected", Toast.LENGTH_SHORT).show();
        selectItem(position);
    }

    //Для адаптера
    public void selectItem(int position) {
        listView.setItemChecked(position,true);
        setTitle(drawerItems[position]);
    }

    //функция проверки OpenGL2
    private boolean supportES2() {
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        return (configurationInfo.reqGlEsVersion >= 0x20000);
    }

    @Override
    public void onClick(View view) {

    }

    //Функция-Обработчик нажатия на кнопки
    View.OnClickListener ZoomInListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //renderer. += 5;
            Log.e("ZoomIn", "ZoomIn");
        }
    };

    View.OnClickListener ZoomOutListener =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //renderer.z -= 5;
            Log.e("ZoomOut","ZoomOut");
        }
    };
}
