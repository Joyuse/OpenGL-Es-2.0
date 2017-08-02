package com.example.vladimir.newopengles;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private GLSurfaceView glSurfaceView;
    OpenGLRenderer renderer;
    private ListView listView;
    private String [] drawerItems;
    private MyAdapter myAdapter;

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
        glSurfaceView.setRenderer(new OpenGLRenderer(this));

        //Адаптер для меню
        listView = (ListView)findViewById(R.id.drawer_list);
        myAdapter = new MyAdapter(this);
        listView.setAdapter(myAdapter);

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

    //Адаптера
    class MyAdapter extends BaseAdapter {

        Context contex;

        String[] drawerItems;

        int[] drawerImages = {
                R.drawable.ic_shortcut_location_city,
                R.drawable.ic_shortcut_message,
                R.drawable.ic_shortcut_favorite,
                R.drawable.ic_shortcut_swap_calls,
                R.drawable.ic_shortcut_nature,
                R.drawable.ic_shortcut_group};

        public MyAdapter(Context context) {
            this.contex = context;
            drawerItems = context.getResources().getStringArray(R.array.screen_array);
        }

        @Override
        public int getCount() {
            return drawerItems.length;
        }

        @Override
        public Object getItem(int position) {
            return drawerItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View row = null;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.custom_row, viewGroup, false);
            } else {
                row = view;
            }

            TextView titeltextView1 = (TextView) row.findViewById(R.id.textView1);
            ImageView titleImageView = (ImageView) row.findViewById(R.id.imageView1);

            titeltextView1.setText(drawerItems[position]);
            titleImageView.setImageResource(drawerImages[position]);

            final int p = position;
            final View.OnClickListener makeListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (p) {
                        case 0:
                            Log.e("Event", "PointerCount 0 = " + drawerItems[0]);
                            break;
                        case 1:
                            Log.e("Event", "PointerCount 1 = " + drawerItems[1]);
                            break;
                        case 2:
                            Log.e("Event", "PointerCount 2 = " + drawerItems[2]);
                            break;
                        case 3:
                            Log.e("Event", "PointerCount 3 = " + drawerItems[3]);
                            break;
                        case 4:
                            Log.e("Event", "PointerCount 4 = " + drawerItems[4]);
                            break;
                        case 5:
                            Log.e("Event", "PointerCount 5 = " + drawerItems[5]);
                            break;
                    }
                }
            };
            row.setOnClickListener(makeListener);
            return row;
        }
    }
}
