package com.cbsd.opengl.utils

import android.annotation.SuppressLint
import android.content.Context
import java.lang.reflect.Field

object ScreenUtils {

    /**
     * 获取状态栏高度
     *
     * @return
     */
    @SuppressLint("PrivateApi")
    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c.newInstance()
            field = c.getField("status_bar_height")
            x = field[obj].toString().toInt()
            statusBarHeight = context.getResources().getDimensionPixelSize(x)
            return statusBarHeight
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight
    }
}