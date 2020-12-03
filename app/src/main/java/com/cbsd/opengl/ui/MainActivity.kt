package com.cbsd.opengl.ui

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cbsd.opengl.imge.SGLView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MainActivity : AppCompatActivity() {

    private lateinit var glView: SGLView

    private var leftIndex = 1.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glView = SGLView(this)
        setContentView(glView)

        glView.setOnClickListener {
            leftIndex -= 0.2F
            val sPos = floatArrayOf(
                    -1.0f, 1.0f,  //左上角
                    -1.0f, leftIndex,  //左下角
                    1.0F, 1.0f,  //右上角
                    1.0f, -1.0f //右下角
            )
            val mFilter = glView.render.filter
            mFilter.setsPos(sPos)
            glView.requestRender()
        }
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    inner class SimpleRenderer : GLSurfaceView.Renderer {

        lateinit var vertices: FloatBuffer
        lateinit var indices: ShortBuffer

        init {
            val byteBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)
            byteBuffer.order(ByteOrder.nativeOrder())
            vertices = byteBuffer.asFloatBuffer()
            //四个点
            vertices.put(floatArrayOf(
                    -30f, -120f, //左下角
                    80f, -190f, //
                    -80f, 220f,
                    50f, 120f))
            val indicesBuffer = ByteBuffer.allocateDirect(6 * 2)
            indicesBuffer.order(ByteOrder.nativeOrder())
            indices = indicesBuffer.asShortBuffer()
            indices.put(shortArrayOf(0, 1, 2, 1, 2, 3))
            indices.flip()
            vertices.flip()
        }

        override fun onDrawFrame(gl: GL10?) {
            //定义显示在屏幕上的什么位置(opengl 自动转换)
            gl!!.glViewport(0, 0, glView.getWidth(), glView.getHeight())
            //!! gl.glViewport(50, 50,430, 550);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            //设置视锥体的大小，一个很扁的长方体
            gl.glOrthof(-160F, 160F, -240F, 240F, 1F, -1F)
            //颜色设置为红色
            gl.glColor4f(1F, 0F, 0F, 1F)
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
            gl.glVertexPointer( 2, GL10.GL_FLOAT, 0, vertices)
            gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 6, GL10.GL_UNSIGNED_SHORT, indices)
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        }

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        }
    }
}