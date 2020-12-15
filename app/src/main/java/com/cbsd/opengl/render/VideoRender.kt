package com.cbsd.opengl.render

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.view.Surface
import com.cbsd.opengl.utils.AssetsUtils
import com.cbsd.opengl.utils.GLTools
import com.cbsd.opengl.utils.LogUtils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VideoRender(private val context: Context, private val frameAvailableListener: SurfaceTexture.OnFrameAvailableListener): GLSurfaceView.Renderer {

    private var mProgramHandle = 0
    private var vPositionLoc = 0
    private var texCoordLoc = 0
    private var textureLoc = 0

    private var textureId = 0

    private lateinit var mediaPlayer: MediaPlayer

    var vertexBuffer = GLTools.array2Buffer(
        floatArrayOf(
            -1.0f, 1.0f, 0.0f,  // top left
            -1.0f, -1.0f, 0.0f,  // bottom left
            1.0f, -1.0f, 0.0f,  // bottom right
            1.0f, 1.0f, 0.0f  // top right
        )
    )

    var texBuffer = GLTools.array2Buffer(
        floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        )
    )


    companion object {
        private val VERTEX_SHADER = """
                uniform mat4 u_Matrix;
                attribute vec4 a_Position;
                // 纹理坐标：2个分量，S和T坐标
                attribute vec2 a_TexCoord;
                varying vec2 v_TexCoord;
                void main() {
                    v_TexCoord = a_TexCoord;
                    gl_Position = u_Matrix * a_Position;
                }
                """
        private val FRAGMENT_SHADER = """
                precision mediump float;
                varying vec2 v_TexCoord;
                // sampler2D：二维纹理数据的数组
                uniform sampler2D u_TextureUnit;
                void main() {
                    gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);
                }
                """

        private val POSITION_COMPONENT_COUNT = 2


        private var POINT_DATA = floatArrayOf(
            -0.5f, -0.5f,//左下角
            -0.5f, 0.5f,//左上角
            0.5f, 0.5f,//右上角
            0.5f, -0.5f//右下角
        )

        /**
         * 纹理坐标
         */
        private val TEX_VERTEX = floatArrayOf(
            0f, 1f,
            0f, 0f,
            1f, 0f,
            1f, 1f
        )

        /**
         * 纹理坐标中每个点占的向量个数
         */
        private val TEX_VERTEX_COMPONENT_COUNT = 2
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glUseProgram(mProgramHandle)
        //设置顶点数据
        vertexBuffer.position(0)
        GLES20.glEnableVertexAttribArray(vPositionLoc)
        GLES20.glVertexAttribPointer(vPositionLoc, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        //设置纹理顶点数据
        texBuffer.position(0)
        GLES20.glEnableVertexAttribArray(texCoordLoc)
        GLES20.glVertexAttribPointer(texCoordLoc, 2, GLES20.GL_FLOAT, false, 0, texBuffer)
        //设置纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureLoc, 0)
        val index = shortArrayOf(3, 2, 0, 0, 1, 2)
        val indexBuffer = GLTools.array2Buffer(index)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            index.size,
            GLES20.GL_UNSIGNED_SHORT,
            indexBuffer
        )


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        createProgram()
        vPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        texCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate")
        textureLoc = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture")

        textureId = createOESTextureId()
        val surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture.setOnFrameAvailableListener(frameAvailableListener)

        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        val surface = Surface(surfaceTexture)
        mediaPlayer.setSurface(surface)

        startVideo()
    }

    private fun startVideo() {
        try {
            mediaPlayer.reset()
            val fd = context.assets.openFd("video/test.mp4")
            mediaPlayer.setDataSource(fd.fileDescriptor,fd.startOffset,fd.length)
            mediaPlayer.prepare()
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                mediaPlayer.start()
            }
        } catch (e: Exception) {
            LogUtils.d("播放异常：${e.message}")
        }
    }

    private fun createProgram() {
        val vertexCode =
            AssetsUtils.readAssetsTxt(
                context = context,
                fileName = "glsl/video_vs.glsl"
            )
        val fragmentCode =
            AssetsUtils.readAssetsTxt(
                context = context,
                fileName = "glsl/video_fs.glsl"
            )
        mProgramHandle = GLTools.createAndLinkProgram(vertexCode!!, fragmentCode!!)
    }

    fun createOESTextureId(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
//        GLTools.glCheck("texture generate")
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0])
//        GLTools.glCheck("texture bind")

        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        return textures[0]
    }
}