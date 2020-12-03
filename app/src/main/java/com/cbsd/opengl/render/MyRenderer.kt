package com.cbsd.opengl.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.cbsd.opengl.utils.AssetsUtils
import com.cbsd.opengl.utils.GLTools
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var mProgramHandle = 0

    var vertexBuffer = GLTools.array2Buffer(
        floatArrayOf(
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        )
    )

    /**
     * 执行OpenGL ES渲染工作，由系统以一定的频率来调用重绘View，当设置GLSurfaceView的渲染模式为GLSurfaceView.RENDERMODE_CONTINUOUSLY或不设置时，
     * 系统就会主动回调onDrawFrame()方法, 如果设置为 RENDERMODE_WHEN_DIRTY ，手动调用requestRender()，才会渲染
     */
    override fun onDrawFrame(gl: GL10?) {
        //获取Shader中参数句柄及设置顶点数据。获取Vertex Shader中vPosition句柄
        val vPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "vPosition")

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(mProgramHandle)

        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(vPositionLoc)
        GLES20.glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        /*
        OpenGL ES中绘制任何形状都是通过绘制多个三角形而组成，所以我们将这4个点分为2个三角形，分布为（V1,V2,V3）和（V1,V3,V4），因此定义三角形索引数组代码如下：
         */
        val index = shortArrayOf(3, 2, 0, 0, 1, 2)
        val indexBuffer = GLTools.array2Buffer(index)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, index.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
    }

    /**
     * 当Surface发生变化的时候回调，比如竖屏转横屏导致GLSurfaceView大小发生变化，通常情况下在此方法中设置绘制窗口及和GLSurfaceView大小有关系的参数
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        /*
        第一个参数（x）:表示窗口x坐标，屏幕左上角为原点
        第二个参数（y）:表示窗口y坐标，（0，0）表示屏幕左上角
        第三个参数（width）：表示窗口的宽
        第四个参数（height）：表示窗口的高
         */
        GLES20.glViewport(0, 0, width, height)
    }

    /**
     * GLSurfaceView创建完成，也代表OpenGL ES环境创建完成，通常情况下在此方法中创建Program及初始化参数
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        createProgram()
    }


    private fun createProgram() {
        val vertexCode = AssetsUtils.readAssetsTxt(context, "glsl/triangle_vertex.glsl")
        val fragmentCode = AssetsUtils.readAssetsTxt(context, "glsl/triangle_fragment.glsl")
        mProgramHandle = GLTools.createAndLinkProgram(vertexCode!!, fragmentCode!!)
    }
}