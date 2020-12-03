/*
 *
 * SGLView.java
 *
 * Created by Wuwang on 2016/10/15
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.cbsd.opengl.imge;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.cbsd.opengl.R;
import com.cbsd.opengl.filter.AFilter;
import com.cbsd.opengl.imge.SGLRender;

/**
 * Description:
 */
public class SGLView extends GLSurfaceView {

    private SGLRender render;

    public SGLView(Context context) {
        this(context, null);
    }

    public SGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        render = new SGLRender(this);
        setRenderer(render);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        render.setImage(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_temp_avatar));
        requestRender();
    }

    public SGLRender getRender() {
        return render;
    }

    public void setFilter(AFilter filter) {
        render.setFilter(filter);
    }



}
