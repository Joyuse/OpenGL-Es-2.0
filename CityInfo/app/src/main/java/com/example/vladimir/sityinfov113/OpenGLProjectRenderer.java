package com.example.vladimir.sityinfov113;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Vladimir on 16.08.2017.
 */

public class OpenGLProjectRenderer implements GLSurfaceView.Renderer {
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    /*
    private final FloatBuffer mTriangle1Vertices;
    private final FloatBuffer mTriangle2Vertices;
    private final FloatBuffer mTriangle3Vertices;
    private final FloatBuffer mTriangle4Vertices;
    private final FloatBuffer mTriangle5Vertices;
    private final FloatBuffer mTriangle6Vertices;
    */


    private final FloatBuffer verticesReady;


    private int mMVPMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private final int mBytesPerFloat = 4;
    private final int mStrideBytes = 7 * mBytesPerFloat;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    private float x;
    private Context context;


    float eyeX = 0.0f;
    float eyeY = 0.0f;
    float eyeZ = 1.5f;

    float lookX = 0.0f;
    float lookY = 0.0f;
    float lookZ = -5.0f;

    float upX = 0.0f;
    float upY = 1.0f;
    float upZ = 0.0f;

    float angle = 1.0f;

    public OpenGLProjectRenderer() {

        float[] vertices = {
                //Координаты XYZ
                //ЦВЕТ RGB

                //Вершина 1
                1,0,0,
                1.0f, 0.0f, 0.0f, 0.0f,
                0,1,0,
                1.0f, 0.0f, 0.0f, 0.0f,
                0,0,0,
                1.0f, 0.0f, 0.0f, 0.0f,

                //2
                0,0,1,
                0.0f, 1.0f, 0.0f, 0.0f,
                1,0,0,
                0.0f, 1.0f, 0.0f, 0.0f,
                0,0,0,
                0.0f, 1.0f, 0.0f, 0.0f,

                //3-й
                0,1,0,
                0.0f, 0.0f, 1.0f, 0.0f,
                0,0,1,
                0.0f, 0.0f, 1.0f, 0.0f,
                0,0,0,
                0.0f, 0.0f, 1.0f, 0.0f,

                //4-й
                0,0,1,
                0.0f, 0.0f, 1.0f, 1.0f,
                1,0,0,
                0.0f, 0.0f, 1.0f, 1.0f,
                0,1,0,
                0.0f, 0.0f, 1.0f, 1.0f,
        };

        verticesReady = ByteBuffer.allocateDirect(vertices.length * mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesReady.put(vertices).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        //Вершинный шейдер
        final String vertexShader =
                "uniform mat4 u_MVPMatrix;      \n"
                        + "attribute vec4 a_Position;     \n"
                        + "attribute vec4 a_Color;        \n"
                        + "varying vec4 v_Color;          \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   v_Color = a_Color;          \n"
                        + "   gl_Position = u_MVPMatrix   \n"
                        + "               * a_Position;   \n"
                        + "}                              \n";

        //Фрагментный шейдер
        final String fragmentShader =
                "precision mediump float;       \n"
                        + "varying vec4 v_Color;          \n"
                        + "void main()                    \n"
                        + "{                              \n"
                        + "   gl_FragColor = v_Color;     \n"
                        + "}                              \n";


        //считыватель шейдера вершинного
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if (vertexShaderHandle != 0)
        {
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);

            GLES20.glCompileShader(vertexShaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }

        //Проверка на ошибку вершинного шейдера
        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        //считывание фрагментного шейдера
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if (fragmentShaderHandle != 0)
        {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
            GLES20.glCompileShader(fragmentShaderHandle);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }

        //Проверка на фрагментного шейдера
        if (fragmentShaderHandle == 0)
        {
            throw new RuntimeException("Error creating fragment shader.");
        }

        //Создание программы из шейдеров
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        //Проверка на ошибку создания программы
        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        //создание матрицы, позиции, и цвета
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

        //Говорим программе что мы рендерим сцену
        GLES20.glUseProgram(programHandle);
    }
    @Override
    // задаем ViewPort
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    // рисуем
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setIdentityM(mModelMatrix,0);
        Matrix.translateM(mModelMatrix,0,0,0,0);


        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
        drawTriangle(verticesReady);
    }

    //функция рисовки 3-ка
    private void drawTriangle(final FloatBuffer aTriangleBuffer)
    {
        // информация о позиции
        aTriangleBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // информация о цвете
        aTriangleBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
                mStrideBytes, aTriangleBuffer);

        GLES20.glEnableVertexAttribArray(mColorHandle);

        // перемножение матриц, результат в MVP матрице
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        // задаем modelView
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        //Вырисовываем 3-к
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
