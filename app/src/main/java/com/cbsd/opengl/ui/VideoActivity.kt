package com.cbsd.opengl.ui

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cbsd.opengl.R
import com.cbsd.opengl.render.VideoRender
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity(), SurfaceTexture.OnFrameAvailableListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        initView()
    }

    private fun initView (){
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(VideoRender(context = this, frameAvailableListener = this))
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        glSurfaceView.queueEvent {
            surfaceTexture?.updateTexImage()
            glSurfaceView.requestRender()
        }
    }
}