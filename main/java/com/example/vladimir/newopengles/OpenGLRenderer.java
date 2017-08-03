package com.example.vladimir.newopengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glLineWidth;

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private final static int POSITION_COUNT = 3;
    private final static long TIME = 10000;

    private Context context;

    // точка положения камеры
    public float eyeX;
    public float eyeY;
    public float eyeZ = 4;

    // точка направления камеры
    public float centerX;
    public float centerY;
    public float centerZ;

    // up-вектор
    public float upX;
    public float upY = 1;
    public float upZ;

    //проверка
    public float test;

    //Буферы
    private FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private int programId;

    //Матрицы
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];




    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
        //createViewMatrix();
        prepareData();
        bindData();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        //bindMatrix();
    }

    private void prepareData() {

        float s = 0.4f;
        float d = 0.9f;
        float l = 3;

        float[] vertices = {

                // первый треугольник
                -2*s, -s, d,
                2*s, -s, d,
                0, s, d,

                // второй треугольник
                -2*s, -s, -d,
                2*s, -s, -d,
                0, s, -d,

                // третий треугольник
                d, -s, -2*s,
                d, -s, 2*s,
                d, s, 0,

                // четвертый треугольник
                -d, -s, -2*s,
                -d, -s, 2*s,
                -d, s, 0,

                // ось X
                -l, 0,0,
                l,0,0,

                // ось Y
                0,-l,0,
                0,l,0,

                // ось Z
                0,0,-l,
                0,0,l,
        };

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);
    }

    private void bindData() {
        // примитивы
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT,
                false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // цвет
        uColorLocation = glGetUniformLocation(programId, "u_Color");

        // матрица
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void createProjectionMatrix(int width, int height) {
        float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 8;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void createViewMatrix() {
        //Для теста
        float time = (float)(SystemClock.uptimeMillis() % TIME) / TIME;
        float angle = time  *  2 * 3.1415926f;

        // точка положения камеры

        //eyeX = 0;
        //eyeY = 0;
        //eyeZ = 4;

        /*
        eyeX = (float) (Math.cos(angle) * 4f);
        eyeY = 1f;
        eyeZ = (float) (Math.sin(angle) * 4f);
        */

        // точка направления камеры

        //centerX = 0;
        //centerY = 0;
        //centerZ = 0;


        // up-вектор трокать не нужно(наклоны и т.д)
        //upX = 0;
        //upY = 1;
        //upZ = 0;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        createViewMatrix();
        bindMatrix();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // треугольники
        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 3, 3);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 6, 3);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 9, 3);

        // оси
        glLineWidth(1);

        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 12, 2);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 14, 2);

        glUniform4f(uColorLocation, 1.0f, 0.5f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 16, 2);
    }
}

/*
public class OpenGLRenderer implements Renderer {

    private final static int POSITION_COUNT = 3;
    private static final int TEXTURE_COUNT = 2;
    private static final int STRIDE = (POSITION_COUNT + TEXTURE_COUNT) * 4;

    private Context context;

    private FloatBuffer vertexData;

    private int aPositionLocation;
    private int aTextureLocation;
    private int uTextureUnitLocation;
    private int uMatrixLocation;

    private int programId;

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private int texture;
    private int texture1;
    private int texture2;
    private final static long TIME = 10000L;


    public OpenGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        glClearColor(0f, 0f, 0f, 1f);
        glEnable(GL_DEPTH_TEST);

        createAndUseProgram();
        getLocations();
        prepareData();
        bindData();
        createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        glViewport(0, 0, width, height);
        createProjectionMatrix(width, height);
        bindMatrix();
    }

    private void prepareData() {
        float[] vertices = {
                //coordinates for sky
                -2, 4f, 0,   0, 0,
                -2, 0, 0,      0, 0.5f,
                2,  4f, 0,   0.5f, 0,
                2, 0, 0,       0.5f, 0.5f,

                //coordinates for sea
                -2, 0, 0,      0.5f, 0,
                -2, -1, 2,       0.5f, 0.5f,
                2, 0, 0,      1, 0,
                2,-1, 2,        1, 0.5f,

                //coordinates for dolphin
                -1, 1f, 0.5f,      0, 0.5f,
                -1, -1, 0.5f,      0, 1,
                1,  1f, 0.5f,      0.5f, 0.5f,
                1, -1, 0.5f,       0.5f, 1,
        };

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);

        texture = TextureUtils.loadTexture(context, R.drawable.texture);
        texture1 = TextureUtils.loadTexture(context, R.drawable.texture);
        texture2 = TextureUtils.loadTexture(context, R.drawable.texture);
    }

    private void createAndUseProgram() {
        int vertexShaderId = ShaderUtils.createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader);
        int fragmentShaderId = ShaderUtils.createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader);
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId);
        glUseProgram(programId);
    }

    private void getLocations() {
        aPositionLocation = glGetAttribLocation(programId, "a_Position");
        aTextureLocation = glGetAttribLocation(programId, "a_Texture");
        uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit");
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix");
    }

    private void bindData() {
        // координаты вершин
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        // координаты текстур
        vertexData.position(POSITION_COUNT);
        glVertexAttribPointer(aTextureLocation, TEXTURE_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aTextureLocation);


        // помещаем текстуру в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture);

        // помещаем текстуру1 в target 2D юнита 0
//        glActiveTexture(GL_TEXTURE0);
//        glBindTexture(GL_TEXTURE_2D, texture1);

        // помещаем текстуру2 в target 2D юнита 0

        //glActiveTexture(GL_TEXTURE0);
        //glBindTexture(GL_TEXTURE_2D, texture2);

        // юнит текстуры
        //glUniform1i(uTextureUnitLocation, 0);
    }

    private void createProjectionMatrix(int width, int height) {
        float ratio;
        float left = -0.5f;
        float right = 0.5f;
        float bottom = -0.5f;
        float top = 0.5f;
        float near = 2;
        float far = 12;
        if (width > height) {
            ratio = (float) width / height;
            left *= ratio;
            right *= ratio;
        } else {
            ratio = (float) height / width;
            bottom *= ratio;
            top *= ratio;
        }

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    private void createViewMatrix() {
        // точка полоения камеры
        float eyeX = 0;
        float eyeY = 2f;
        float eyeZ = 7;

        // точка направления камеры
        float centerX = 0;
        float centerY = 1;
        float centerZ = 0;

        // up-вектор
        float upX = 0;
        float upY = 1;
        float upZ = 0;

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    private void bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mMatrix, 0);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // сбрасываем model матрицу
        Matrix.setIdentityM(mModelMatrix, 0);
        bindMatrix();

        glBindTexture(GL_TEXTURE_2D, texture);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        glBindTexture(GL_TEXTURE_2D, texture1);
        glDrawArrays(GL_TRIANGLE_STRIP, 4, 4);

        glBindTexture(GL_TEXTURE_2D, texture2);
        Matrix.setIdentityM(mModelMatrix, 0);
        setModelMatrix();
        bindMatrix();


        glDrawArrays(GL_TRIANGLE_STRIP, 8, 4);
    }

    private void setModelMatrix() {
        Matrix.translateM(mModelMatrix, 0, 0, -0.5f, 0);
        // В переменной angle угол будет меняться  от 0 до 360 каждые 10 секунд.

        float angle = -(float)(SystemClock.uptimeMillis() % TIME) / TIME * 360;
        //void rotateM (float[] m,  int mOffset, float a,float x, float y, float z)
        //Rotates matrix m in place by angle a (in degrees) around the axis (x, y, z).
        Matrix.rotateM(mModelMatrix, 0, angle, 0, 0, 1);
        Matrix.translateM(mModelMatrix, 0, -0.8f, 0, 0);
    }

}

//        gl.glRotatef(MainActivity.x, 1.0f, 0.0f, 0.0f);
//        gl.glRotatef(MainActivity.y, 0.0f, 1.0f, 0.0f);
//        gl.glRotatef(MainActivity.z, 0.0f, 0.0f, 1.0f);

//    float pi = (float) Math.PI;
//    float rad2deg = 180/pi;
//
//// Get the pitch, yaw and roll from the sensor.
//
//    float yaw = orientation[0] * rad2deg;
//    float pitch = orientation[1] * rad2deg;
//    float roll = orientation[2] * rad2deg;
//
//// Convert pitch, yaw and roll to a vector
//
//    float x = (float)(Math.cos( yaw ) * Math.cos( pitch ));
//    float y = (float)(Math.sin( yaw ) * Math.cos( pitch ));
//    float z = (float)(Math.sin( pitch ));
//
//GLU.gluLookAt( gl, 0.0f, 0.0f, 0.0f, x, y, z, 0.0f, 1.0f, 0.0f );

*/