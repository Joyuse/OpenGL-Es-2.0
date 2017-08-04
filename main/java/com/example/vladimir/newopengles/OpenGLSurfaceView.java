package com.example.vladimir.newopengles;

/**
 * Created by Vladimir on 23.06.2017.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


public class OpenGLSurfaceView extends GLSurfaceView {

    private final static long TIME = 10000;


    private ScaleGestureDetector mScaleDetector;
    OpenGLRenderer renderer,r;
    private int mLastAngle = 0;
    private float mScaleFactor = 1.0f;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mode = 1;
    private float deltaX, deltaY;
    private final float TOUCH_SCALE_FACTOR = 90.0f / 320.0f;
    int flag =0; //для проверки
    private float mLastTouchX;
    private float mLastTouchY;
    private int degrees;


    //специально для углов
    float angle;
    float forX, forZ;
    float forX2,forZ2;
    //незнаю зачем, но попробую
    float speedX,speedZ,speedX2,speedZ2;


    public OpenGLSurfaceView(Context context, AttributeSet attrs) {

        super(context, attrs);

        //Почти работающий вариант
        //renderer = new OpenGLRenderer(context);

        //переопределил рендерер во вью
        //setRenderer(renderer);

        this.requestFocus();
        this.setFocusableInTouchMode(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setMyCustomRenderer(OpenGLRenderer r){
        setRenderer(r);
        this.renderer = r; // вроде должно работать для всех, но не факт, я не проверял:D
    }

    //Работкает :3
    @Override
    public void setRenderer(Renderer r){
        super.setRenderer(r);
        this.renderer = (OpenGLRenderer)r; // Если буду использовать только OpenGLRenderer, иначе -> исключения
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //событие
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            final float x = event.getX();   //(NEW)
            final float y = event.getY();   //(NEW)
            mLastTouchX = x;    //(NEW)
            //Log.w("Event", "X = " +x);
            mLastTouchY = y;    //(NEW)
            //Log.w("Event", "Y = " +y);
            flag =0;
            return true;
        }

        switch (event.getPointerCount()) {
            case 3:
                //3 пальца
                //Log.e("Event", "Пальцев = " +event.getPointerCount());
                return mScaleDetector.onTouchEvent(event);
            case 2:
                //2 пальца
                //Log.e("Event", "Пальцев = " +event.getPointerCount());
                return doRotationEvent(event);
            case 1:
                //1 палец
                //Log.e("Event", "Пальцев = " +event.getPointerCount());
                return doMoveEvent(event);
        }
        return true;
    }

    private boolean doMoveEvent(MotionEvent event)
    {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                flag ++;
                if (flag > 4) {
                    final float x = event.getX();
                    final float y = event.getY();

                    // считаем движение
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    //Log.w("DX", "DX = " +dx);
                    //Log.w("DY", "DY = " +dy);

                    //Движение по X и Y
                    //renderer.eyeX = (float) ((Math.cos(dy) * 4f) / 16);
                    //renderer.eyeY += dy;
                    //renderer.eyeX += dx;

                    //renderer.centerX += (float) ((Math.cos(dx) * 4f) / 32);
                    //renderer.centerY += (float) ((Math.cos(dy) * 4f) / 32);

                    //renderer.centerY += (float) ((Math.cos(dx) * 4f) / 32);
                    //renderer.centerY -= (float) ((Math.cos(dx) * 4f) / 32);

                    //renderer.eyeZ += (float) ((Math.sin(1) * 4f) / 32);

                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                flag = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                flag = 0;
                break;
        }
        return true;
    }

    private boolean doRotationEvent(MotionEvent event) {
        //расчитываем угол менжду двумя пальцами
        deltaX = event.getX(1) - event.getX(0);
        deltaY = event.getY(1) - event.getY(0);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                flag =0;
                break;
            case MotionEvent.ACTION_POINTER_UP: //Если поднят палец №2
                mLastAngle = degrees;
                flag =0;
                break;
            case MotionEvent.ACTION_UP: //Если поднят палец №1
                flag =0;
                break;
            case MotionEvent.ACTION_MOVE:
                flag ++;
                //Log.w("FLAG", "FLAG = "+ flag);
                double radians = Math.atan(deltaY / deltaX);
                //в градусы
                degrees = (int) (radians * 180 / Math.PI);

                if (flag >10) {
                    /*
                    Log.w("ЧО ЗА ХУНЯ", "ХУНЯ = "+ flag);
                    Log.w("dergess", "DEGRESS = "+ degrees);
                    */

                    //в зависимости от того куда крутить
                    if (degrees > 0)
                    {
                        Log.w("Указательный палце", "ВЛЕВО");
                        forX += 0.001f;
                        forZ -= 0.001f;
                        float angle = forX * 2 * 3.1415926f;
                        renderer.eyeX = (float) ((Math.cos(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.sin(angle) * 4f));

                        //renderer.eyeX += 0.1f;
                        //renderer.eyeZ -= 0.1f;
                        /*
                        Log.w("БОЛЬШЕ", "45 = ");
                        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
                        float angle = time  *  2 * 3.1415926f;
                        //Log.w("TIME", "TIME = " +(time  *  2 * 3.1415926f));
                        renderer.eyeX = (float) ((Math.cos(angle) * 4f));
                        //Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.sin(angle) * 4f));
                        */
                    }

                    else if (degrees < 0)
                    {
                        Log.w("Указательный палце", "ВПРАВО");
                        forX2 -= 0.001f;
                        forZ2 += 0.001f;
                        float angle = forZ2 * 2 * 3.1415926f;
                        renderer.eyeX = (float) ((Math.sin(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.cos(angle) * 4f));

                        /*
                        renderer.eyeX -= 0.1f;
                        renderer.eyeZ += 0.1f;
                        */
                        /*
                        Log.w("МЕНЬШЕ", "- 45 = ");
                        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
                        float angle = time  *  2 * 3.1415926f;
                        //Log.w("TIME", "TIME = " +(time  *  2 * 3.1415926f));
                        renderer.eyeX = (float) ((Math.sin(angle) * 4f));
                        //Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.cos(angle) * 4f));
                        */
                    }


                    //if ((degrees - mLastAngle) >= 1) {
                        /*
                        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
                        float angle = time  *  2 * 3.1415926f;
                        Log.w("TIME", "TIME = " +(time  *  2 * 3.1415926f));
                        renderer.eyeX = (float) ((Math.cos(angle) * 4f));
                        Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.sin(angle) * 4f));
                        Log.w("EYEX", "Angle SIN = " +(Math.cos(angle) * 4f));
                        */
                        /*
                        //Сюда запилить приколюхи
                        mode += 0.001f;
                        renderer.eyeX = (float) ((cos(mode) * 4f));
                        renderer.eyeZ = (float) ((sin(mode) * 4f));
                        */

                    //} else if ((degrees - mLastAngle) >= -1) {
                        /*
                        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
                        float angle = time  *  2 * 3.1415926f;
                        Log.w("TIME", "TIME = " +(time  *  2 * 3.1415926f));
                        renderer.eyeX = (float) ((Math.sin(angle) * 4f));
                        Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                        renderer.eyeY = 1f;
                        renderer.eyeZ = (float) ((Math.cos(angle) * 4f));
                        Log.w("EYEX", "Angle SIN = " +(Math.cos(angle) * 4f));
                        */
                        /*
                        Log.w("TIME", "TIME = Меньше чем 45 " + (degrees - mLastAngle));

                        // и сюда тоже
                        mode -= 0.001f;
                        renderer.eyeX = (float) ((cos(mode) * 4f));
                        renderer.eyeZ = (float) ((sin(mode) * 4f));
                        */

                    //} else {
                    //  mode = degrees - mLastAngle;
                    //}
                }
                //А НАХУЯ, когда я могу просто задать градус поворота и нормально будет(навреное)
                //Для теста, пробнем прикольно наверное полчится:D
                //Заменить TIME на что то другое, а именно на что то такое что не будет превышать 1 и не будет меньше 0, вот, так мы получим крутилку, по идее.
                /*
                float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
                float angle = time  *  2 * 3.1415926f;
                Log.w("TIME", "TIME = " +(time  *  2 * 3.1415926f));
                renderer.eyeX = (float) ((Math.cos(angle) * 4f));
                Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                renderer.eyeY = 1f;
                renderer.eyeZ = (float) ((Math.sin(angle) * 4f));
                Log.w("EYEX", "Angle SIN = " +(Math.cos(angle) * 4f));
                */

                /**
                // Перевод в градусы хе-хе
                float angle = degrees  *  2 * 3.1415926f;
                //Повернуть камеру по кругу на полученные градусы
                renderer.eyeX =  (float) ((Math.cos(angle) * 4f));
                Log.w("EYEX", "Angle COS = " +(Math.cos(angle) * 4f));
                renderer.eyeZ =  (float) ((Math.sin(angle) * 4f));
                Log.w("EYEZ", "Angle SIN = " +(Math.sin(angle) * 4f));
                renderer.eyeY = 1f;
                */
                mLastAngle = degrees;
                break;
        }
        return true;
    }

    //Отдаляет по Z, ура йопт
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(5.0f, Math.min(mScaleFactor, 1500.0f));
            renderer.eyeZ = mScaleFactor;
            flag = 0;
            return true;
        }
    }
}