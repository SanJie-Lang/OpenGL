package com.cbsd.opengl.utils

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object GLTools {

    fun compileShader(shaderType: Int, shaderSource: String): Int {
        //创建一个空shader
        var shaderHandle: Int = GLES20.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            //加载shader源码
            GLES20.glShaderSource(shaderHandle, shaderSource)
            //编译shader
            GLES20.glCompileShader(shaderHandle)
            val compileStatus = IntArray(1)
            //检查shader状态
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
            if (compileStatus[0] == 0) {
                //输入shader异常日志
                LogUtils.d("Error compile shader:${GLES20.glGetShaderInfoLog(shaderHandle)}")
                //删除shader
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        if (shaderHandle == 0) {
            LogUtils.d("Error create shader")
        }
        return shaderHandle
    }

    fun createAndLinkProgram(vertexCode: String, fragmentCode: String): Int {
        //创建一个空的program
        var programHandle = GLES20.glCreateProgram()
        if (programHandle != 0) {
            //编译shader
            val vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexCode)
            val fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
            //绑定shader和program
            GLES20.glAttachShader(programHandle, vertexShaderHandle)
            GLES20.glAttachShader(programHandle, fragmentShaderHandle)
            //链接program
            GLES20.glLinkProgram(programHandle)

            val linkStatus = IntArray(1)
            //检测program状态
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                LogUtils.d("Error link program:${GLES20.glGetProgramInfoLog(programHandle)}")
                //删除program
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            LogUtils.d("Error create program")
        }
        return programHandle
    }

    fun array2Buffer(array: FloatArray): FloatBuffer {
        val bb = ByteBuffer.allocateDirect(array.size * 4)
        bb.order(ByteOrder.nativeOrder())
        var buffer = bb.asFloatBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun array2Buffer(array: ShortArray): ShortBuffer {
        val bb = ByteBuffer.allocateDirect(array.size * 4)
        bb.order(ByteOrder.nativeOrder())
        var buffer = bb.asShortBuffer()
        buffer.put(array)
        buffer.position(0)
        return buffer
    }

    fun setAttributePointer(location: Int, buffers: FloatBuffer, pointSize: Int) {
        buffers.position(0)
        GLES20.glEnableVertexAttribArray(location)
        GLES20.glVertexAttribPointer(location, pointSize, GLES20.GL_FLOAT, false, 0, buffers)
    }

    fun createTextureId(): Int {
        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
//        glCheck("texture generate")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
//        glCheck("texture bind")

        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        return textures[0]
    }
}