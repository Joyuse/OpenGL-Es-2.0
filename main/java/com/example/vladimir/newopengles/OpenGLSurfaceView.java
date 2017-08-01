package com.example.vladimir.newopengles;

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

    private ScaleGestureDetector mScaleDetector;
    OpenGLRenderer renderer;
    private int mLastAngle = 0;
    private float mScaleFactor = 1.0f;
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private int mode;
    private float deltaX, deltaY;
    private final float TOUCH_SCALE_FACTOR = 90.0f / 320.0f;
    int flag =0; //для проверки
    private float mLastTouchX;
    private float mLastTouchY;

    private int degrees;


    public OpenGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        renderer = new OpenGLRenderer(context);
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        this.setRenderer(renderer);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //событие JOPA
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            final float x = event.getX();   //(NEW)
            final float y = event.getY();   //(NEW)
            mLastTouchX = x;    //(NEW)
            mLastTouchY = y;    //(NEW)
            flag =0;
            return true;
        }

        switch (event.getPointerCount()) {
            case 3:
                //3 пальца
                Log.e("Event", "PointerCount = " +event.getPointerCount());
                return mScaleDetector.onTouchEvent(event);
            case 2:
                //2 пальца
                Log.e("Event", "PointerCount = " +event.getPointerCount());
                return doRotationEvent(event);
            case 1:
                //1 палец
                Log.e("Event", "PointerCount = " +event.getPointerCount());
                return doMoveEvent(event);
        }
        return true;
    }

    //DVIJUHA
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

                    //renderer += dy;
                    //renderer.angleY += dx;
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

    //POVOROTIKI PIDORMOTIKI
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
                double radians = Math.atan(deltaY / deltaX);
                //конвертим
                degrees = (int) (radians * 180 / Math.PI);
                if (flag >10) {
                    if ((degrees - mLastAngle) > 45) {
                        mode = -5;
                    } else if ((degrees - mLastAngle) < -45) {
                        mode = 5;
                    } else {
                        mode = degrees - mLastAngle;
                    }
                }
                //renderer.spinRotate -= mode;
                mLastAngle = degrees;
                break;
        }
        return true;
    }

    //KRUTILKA HUILKA
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(18.0f, Math.min(mScaleFactor, 1500.0f));
            //renderer.z = -mScaleFactor;
            flag = 0;
            return true;
        }
    }
}