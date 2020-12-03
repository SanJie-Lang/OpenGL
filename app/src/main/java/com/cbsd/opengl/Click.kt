package com.cbsd.opengl

import android.view.View

fun View.click(click: () -> Unit){
    setOnClickListener {
        click()
    }
}