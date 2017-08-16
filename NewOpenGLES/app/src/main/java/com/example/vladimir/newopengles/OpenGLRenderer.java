package com.example.vladimir.newopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private final static int POSITION_COUNT = 3;
    private final static long TIME = 10000;

    private Context context;

    // точка положения камеры
    public float eyeX = 0;
    public float eyeY = 0;
    public float eyeZ = 10;

    // точка направления камеры
    public float centerX = 0;
    public float centerY = 0;
    public float centerZ = 0;

    // up-вектор
    public float upX = 0;
    public float upY = 1;
    public float upZ = 0;

    //Угол поворота
    public float mAngle;
    public float angle = 0;

    //проверка
    public float test;
    //Буферы
    private FloatBuffer vertexData;
    private int uColorLocation;
    private int aPositionLocation;
    private int uMatrixLocation;
    private int programId;

    private float x,y,z;

    //Матрицы
    //Матрица проекции
    private float[] mProjectionMatrix = new float[16];
    //Матрица камеры(вида)
    private float[] mViewMatrix = new float[16];
    //Итоговая матрица =  mProjectionMatrix * mViewMatrix
    private float[] mMatrix = new float[16];


    //Шейдер фрагментный
    String fragmentShader = "uniform vec4 u_Color;" +
            "precision mediump float;" +
            "void main()" +
            "{" +
            "gl_FragColor = u_Color;" +
            "}";

    //Шейдер вершинный
    String vetrexShader = "attribute vec4 a_Position;" +
            "uniform mat4 u_Matrix;" +
            "void main()" +
            "gl_Position = u_Matrix * a_Position;" +
            "gl_PointSize = 5.0;" +
            "}";

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

        /*
        //Переопределили метод для загрузки шейдеров
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vertexShaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(vertexShaderHandle, vetrexShader);

            // Compile the shader.
            GLES20.glCompileShader(vertexShaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }
        if (vertexShaderHandle == 0)
        {
            throw new RuntimeException("Error creating vertex shader.");
        }

        int programHandle = GLES20.glCreateProgram();
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Color");

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        */

        if(vertexShaderId == 0) Log.e("vertextShaderId", "0");
        if (fragmentShaderId == 0) Log.e("fragmentShaderId", "0");
        if(programId == 0) Log.e("programm id"," = " + programId);


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

    private void prepareData()
    {
        float s = 0.4f;
        float d = 0.9f;
        float l = 3;

        float[] vertices = {

                /*
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
                */

                1,0,0,
                0,1,0,
                0,0,0,

                0,0,1,
                1,0,0,
                0,0,0,

                0,1,0,
                0,0,1,
                0,0,0,

                0,0,1,
                1,0,0,
                0,1,0,

        };

        vertexData = ByteBuffer
                .allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(vertices);
    }

    //Движенгие
//
//    public void Move(float updn, float lr)
//    {
//        float dirX = (centerX - eyeX);
//        Log.w("centerX", " ===== " +centerX);
//        Log.w("eyeX", " ===== " +eyeX);
//
//        float dirY = (centerY - eyeY);
//        Log.w("dirX", " ===== " +(centerX - eyeX));
//        Log.w("dirY", " ===== " +(centerY - eyeY));
//
//        Log.w("updn", " ===== " +updn);
//        Log.w("lr", " ===== " +lr);
//
//        Matrix.translateM(mViewMatrix, 0, dirX*updn, dirY*updn, 0);
//        Matrix.translateM(mViewMatrix, 0, -dirY*lr, dirX*lr, 0);
//    }
//

    //Повороты
//
//    public void Rotate(float angle)
//    {
//        Log.w("ANGLE", " =====" +angle);
//        Matrix.rotateM(mViewMatrix, 0, angle, 0, 0, 1);
//    }

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

    //Границы "дозволенного", дальность прорисовки 1500, так же для смены ориентации экрана
    private void createProjectionMatrix(int width, int height) {
        float ratio = 1;
        float left = -1;
        float right = 1;
        float bottom = -1;
        float top = 1;
        float near = 2;
        float far = 1500;
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
        //Отразили матрицу по Y
        Matrix.scaleM(mProjectionMatrix, 0, 1, -1, 1);
    }

    private void createViewMatrix() {

        /**
         * eyeX, eyeY, eyeZ – координаты точки положения камеры, т.е. где находится камера
         centerX, centerY, centerZ – координаты трочки направления камеры, т.е. куда камера смотрит
         upX, upY, upZ – координаты up-вектора, т.е. вектора, позволяющего задать поворот камеы вокруг оси «взгляда»
         */
        /**
        //Матрицы
        //Матрица проекции
        private float[] mProjectionMatrix = new float[16];
        //Матрица камеры(вида)
        private float[] mViewMatrix = new float[16];
        //Итоговая матрица =  mProjectionMatrix * mViewMatrix
        private float[] mMatrix = new float[16];
         */

        //Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, eyeZ, 0, 0, 0, 0, upY, 0);
    }

    private void setModelMatrix() {
        //Matrix.translateM(mViewMatrix, 0, 0, 0, 0);
       // Matrix.translateM(mViewMatrix,0, x, y, z);
       // Matrix.rotateM(mViewMatrix, 0, angle, 0, 0, 1);
       // Matrix.setRotateM(mViewMatrix,0,angle,0,0,1);k
//        Matrix.translateM(mViewMatrix, 0, 0, 0, 0);
    }

    private void bindMatrix() {
        //Перемножаем матрицы
        createViewMatrix();

        if(vertexData == null) Log.e("vertexData"," null");

        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        //результат в mMatrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
//        createViewMatrix();
//        setModelMatrix();

        bindMatrix();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 0.0f);
        glDrawArrays(GL_TRIANGLES, 0, 3);

        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 0.0f);
        glDrawArrays(GL_TRIANGLES, 3, 3);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 0.0f);
        glDrawArrays(GL_TRIANGLES, 6, 3);

        /*
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 0.0f);
        glDrawArrays(GL_TRIANGLES, 9, 3);
        */

        /*
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
        */
    }

    //Проверка на ошибки
    public void checkGLError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("MyApp", op + ": glError " + error);
        }
    }
}