package com.cbsd.opengl.utils

import android.content.Context
import java.io.InputStream

object AssetsUtils {

    fun readAssetsTxt(context: Context, fileName: String): String? {
        val result = StringBuilder()
        try {
            val `is`: InputStream = context.resources.assets.open(fileName)
            var ch: Int
            val buffer = ByteArray(1024)
            while (-1 != `is`.read(buffer).also { ch = it }) {
                result.append(String(buffer, 0, ch))
            }
        } catch (e: Exception) {
            return null
        }
        return result.toString().replace("\\r\\n".toRegex(), "\n")
    }
}