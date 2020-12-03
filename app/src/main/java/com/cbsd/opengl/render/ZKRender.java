package com.cbsd.opengl.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.cbsd.opengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ZKRender implements GLSurfaceView.Renderer {
    private static final String VERTEX_SHADER = "attribute vec4 vPosition;\n"
            + "attribute vec2 vCoordinate;\n"
            + "varying vec2 aCoordinate;\n"
            + "void main() {\n"
            + "gl_Position = vPosition;\n"
            + "aCoordinate = vCoordinate;\n"
            + "}";

    private static final String FRAGMENT_SHADER = "precision mediump float;\n"
            + "uniform sampler2D vTexture;\n"
            + "varying vec2 aCoordinate;\n"
            + "void main() {\n"
            + "gl_FragColor = texture2D(vTexture,aCoordinate);\n"
            + "}";

    private float[] vertex = {
            -1.0f, 1.0f,    //左上角
            -1.0f, -1.0f,   //左下角
            1.0f, 1.0f,     //右上角
            1.0f, -1.0f     //右下角
    };

    public void setVertex(float[] vertex) {
        this.vertex = vertex;
    }

    private final float[] sCoord = {
            0f, 0f, //左上角
            0f, 1f, //左下角
            1f, 0f, //右上角
            1f, 1f //右下角
    };

    private FloatBuffer mVertexBuffer;

    private FloatBuffer mFragmentBuffer;

    private int mProgram;

    private int mVPosition;

    private int mVCoordinate;

    private int mVTexture;

    private Context mContext;

    private int mTextureid;

    public ZKRender(Context context) {
        mContext = context;
        mVertexBuffer = ByteBuffer.allocateDirect(vertex.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(vertex);
        mVertexBuffer.position(0);

        mFragmentBuffer = ByteBuffer.allocateDirect(sCoord.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer().put(sCoord);
        mFragmentBuffer.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        int vertex_shader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragment_shader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertex_shader);
        GLES20.glAttachShader(mProgram, fragment_shader);

        GLES20.glLinkProgram(mProgram);

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] != GLES20.GL_TRUE) {
            String info = GLES20.glGetProgramInfoLog(mProgram);
            GLES20.glDeleteProgram(mProgram);
            throw new RuntimeException("Could not link program: " + info);
        }

        mVPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mVCoordinate = GLES20.glGetAttribLocation(mProgram, "vCoordinate");
        mVTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");

        mTextureid = loadTexture(R.mipmap.icon_temp_avatar);

    }

    private int loadTexture(int resId) {

        int[] textures = new int[1];
        //创建和绑定纹理
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        //激活第0个纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(mVTexture, 0);
        //设置环绕和过滤方式
        //环绕（超出纹理坐标范围）：（s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）：（缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        //设置图片
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        bitmap = null;
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textures[0];
    }

    private int loadShader(int glVertexShader, String vertexShader) {
        int glCreateShader = GLES20.glCreateShader(glVertexShader);
        GLES20.glShaderSource(glCreateShader, vertexShader);
        GLES20.glCompileShader(glCreateShader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(glCreateShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteShader(glCreateShader);
            return -1;
        }
        return glCreateShader;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, 400, 400);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 0, 0, 1f);
        //使用源程序
        GLES20.glUseProgram(mProgram);
        //绑定绘制纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureid);
        //使顶点属性数组有效
        GLES20.glEnableVertexAttribArray(mVPosition);
        //为顶点属性赋值
        GLES20.glVertexAttribPointer(mVPosition, 2, GLES20.GL_FLOAT, false, 8, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mVCoordinate);
        GLES20.glVertexAttribPointer(mVCoordinate, 2, GLES20.GL_FLOAT, false, 8, mFragmentBuffer);
        //绘制图形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

    }
}
