package com.example.vladimir.sityinfov113;

/**
 * Created by Vladimir on 23.06.2017.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class OpenGLSurfaceView extends GLSurfaceView {

    private final static long TIME = 10000;


    private ScaleGestureDetector mScaleDetector;
    OpenGLProjectRenderer renderer,r;
    private int mLastAngle = 0;
    private float mScaleFactor = 1.0f;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mode;
    private float deltaX, deltaY;
    private final float TOUCH_SCALE_FACTOR = 90.0f / 320.0f;
    int flag =0; //для проверки
    private float mLastTouchX;
    private float mLastTouchY;
    private int degrees;

    private int DoWhat;

    //специально для углов
    float angle;
    float forX, forZ;
    float forX2,forZ2;
    //незнаю зачем, но попробую
    float speedX,speedZ,speedX2,speedZ2;

    //Для ошибки
    String pizdec = " pizdec";


    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setMyCustomRenderer(OpenGLProjectRenderer r){
        setRenderer(r);
        this.renderer = r; // вроде должно работать для всех, но не факт, я не проверял:D
    }

    //Работкает :3
    @Override
    public void setRenderer(Renderer r){
        super.setRenderer(r);
        this.renderer = (OpenGLProjectRenderer)r; // Если буду использовать только OpenGLRenderer, иначе -> исключения
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
                Log.e("Event", "Пальцев = " +event.getPointerCount());
                return doMoveEvent(event);
        }
        return true;
    }

    //Передвижение
    private boolean doMoveEvent(MotionEvent event)
    {
        final int action = event.getAction();
        //Пока идет действие
        switch (action) {
            //Если Дейсвтие - движение
            case MotionEvent.ACTION_MOVE: {
                flag ++;
                if (flag > 4) {
                    //Считываем координаты пальца
                    final float x = event.getX();
                    final float y = event.getY();
                    //Считываем разницу муежду касаниями движение
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

//                    //Направление камеры
                    renderer.lookX += -dx / 256;
                    renderer.lookY += dy / 256;
//                    //Координаты положения камеры
                    renderer.eyeX += -dx / 256;
                    renderer.eyeY += dy / 256;

                    //renderer.Move(dy / 256, dx / 256);
                    //Посление касание
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

    //Повороты
    private boolean doRotationEvent(MotionEvent event) {
        //расчитываем угол менжду двумя пальцами
        deltaX = event.getX(1) - event.getX(0);
        deltaY = event.getY(1) - event.getY(0);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                flag = 0;
                break;
            case MotionEvent.ACTION_POINTER_UP: //Если поднят палец №2
                mLastAngle = degrees;
                flag = 0;
                break;
            case MotionEvent.ACTION_UP: //Если поднят палец №1
                flag = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                flag ++;
                double radians = Math.atan(deltaY / deltaX);
                //конвертим
                degrees = (int) (radians * 180 / Math.PI);
                if (flag >10) {
                    if ((degrees - mLastAngle) > 90) {
                        angle = -1f;
                        mode = -1f;
                        Log.e("FLAG > 90 ", "degrees - mLastAngle = " +(degrees - mLastAngle));
                    }
                    else if ((degrees - mLastAngle) < -90) {
                        angle = 1f;
                        mode = 1f;
                        Log.e("FLAG > -90 ", "degrees - mLastAngle = " +(degrees - mLastAngle));
                    }
                    //Тут происходит основной поворот
                    else {
                        Log.e("ELSE", "ПРОСТО ЕЛСЕ");
                        DoWhat = 1;
                        mode = degrees - mLastAngle;
                    }
                    //Log.e("ЛУЛ", "ВЫЗОВ ЗУМА");
                    //mScaleDetector.onTouchEvent(event);
                }
                //renderer.Rotate(angle);
                //renderer.angle = mode;
                mLastAngle = degrees;
                break;
        }
        return true;
    }

    //Отдаляет по Z
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(2.0f, Math.min(mScaleFactor, 1500.0f));
            //renderer.eyeZ = -mScaleFactor;
            //renderer.centerZ = -mScaleFactor;
            flag = 0;
            return true;
        }
    }
}