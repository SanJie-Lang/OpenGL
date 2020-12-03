package com.cbsd.opengl.ui

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cbsd.opengl.R
import com.cbsd.opengl.click
import com.cbsd.opengl.render.MyRenderer
import com.cbsd.opengl.utils.GLTools
import com.cbsd.opengl.utils.LogUtils
import kotlinx.android.synthetic.main.activity_triangle.*

class TriangleActivity : AppCompatActivity() {


    private lateinit var myRenderer: MyRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_triangle)
        LogUtils.d("是否支持ES2： ${supportsEs2()}")
        when (supportsEs2()) {
            true -> initGL()
        }
        initView()
    }

    private fun initGL() {
        //设置opengl es版
        glSurfaceView.setEGLContextClientVersion(2)
        //设置renderer
        myRenderer = MyRenderer(this)
        glSurfaceView.setRenderer(myRenderer)
        //设置渲染模式
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY //只有在调用requestRender或者onResume等方法时才渲染
        glSurfaceView.requestRender()
    }

    private var x1 = 0.5F
    private var y1 = -0.5F
    private fun initView() {
        glSurfaceView.click {
            x1 += 0.01F
            y1 -= 0.01F
            val vertexBuffer = GLTools.array2Buffer(
                floatArrayOf(
                    y1, x1, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f,
                    0.5f, 0.5f, 0.0f
                )
            )
            myRenderer.vertexBuffer = vertexBuffer
            glSurfaceView.requestRender()
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    private fun supportsEs2(): Boolean {
        val configurationInfo =
            (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo
        LogUtils.d("GLVersion:${configurationInfo.reqGlEsVersion}")
        return configurationInfo.reqGlEsVersion >= 0x20000
    }
}