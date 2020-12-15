package com.cbsd.opengl.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cbsd.opengl.R
import com.cbsd.opengl.render.ImageRender
import com.cbsd.opengl.utils.LogUtils
import com.cbsd.opengl.utils.ScreenUtils
import kotlin.math.pow

class ImageActivity : AppCompatActivity(), View.OnTouchListener {

    private var glSurfaceView: GLSurfaceView? = null
    private lateinit var renderer: ImageRender

    //画布
    private var mWidth = 0
    private var mHeight = 0

    //图片
    private var imageTopWidth = 0
    private var imageBottomWidth = 0
    private var imageLeftHeight = 0
    private var imageRightHeight = 0

    private var rightTopX = 0.5F
    private var rightTopY = 0.5F
    private var rightTopXPoint = 0F
    private var rightTopYPoint = 0F

    private var rightBottomX = 0.5F
    private var rightBottomY = -0.5F
    private var rightBottomXPoint = 0F
    private var rightBottomYPoint = 0F

    private var leftTopX = -0.5F
    private var leftTopY = 0.5F
    private var leftTopXPoint = 0F
    private var leftTopYPoint = 0F

    private var leftBottomX = -0.5F
    private var leftBottomY = -0.5F
    private var leftBottomXPoint = 0F
    private var leftBottomYPoint = 0F

    private val pointMargin = 80

    private var clickType = -1; //1 左上角  2 右上角 3 左下角  4 右下角

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        setContentView(glSurfaceView)
        glSurfaceView!!.setEGLContextClientVersion(2)
        glSurfaceView!!.setEGLConfigChooser(false)

        renderer = ImageRender(this)
        glSurfaceView!!.setRenderer(renderer)
        glSurfaceView!!.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        glSurfaceView!!.setOnTouchListener(this)

        val options = BitmapFactory.Options()
        BitmapFactory.decodeResource(resources, R.mipmap.icon_temp_avatar, options)
        imageTopWidth = options.outWidth
        imageBottomWidth = options.outWidth

        imageLeftHeight = options.outHeight
        imageRightHeight = options.outHeight
        LogUtils.d("图片宽高：$imageTopWidth, $imageRightHeight")

        glSurfaceView!!.post {
            mWidth = glSurfaceView!!.width
            mHeight = glSurfaceView!!.height + ScreenUtils.getStatusBarHeight(this)
            LogUtils.d("宽高：${glSurfaceView!!.width}, ${glSurfaceView!!.height}")

            leftTopXPoint = ((mWidth - imageTopWidth) / 2).toFloat()
            leftTopYPoint = ((mHeight - imageRightHeight) / 2).toFloat()
            LogUtils.d("左上角点：[$leftTopXPoint, $leftTopYPoint]")

            leftBottomXPoint = ((mWidth - imageTopWidth) / 2).toFloat()
            leftBottomYPoint = ((mHeight - imageRightHeight) / 2 + imageRightHeight).toFloat()
            LogUtils.d("左下角点：[$leftBottomXPoint, $leftBottomYPoint]")

            rightTopXPoint = ((mWidth - imageTopWidth) / 2 + imageTopWidth).toFloat()
            rightTopYPoint = ((mHeight - imageRightHeight) / 2).toFloat()
            LogUtils.d("右上角点：[$rightTopXPoint, $rightTopYPoint]")

            rightBottomXPoint = ((mWidth - imageTopWidth) / 2 + imageTopWidth).toFloat()
            rightBottomYPoint = ((mHeight - imageRightHeight) / 2 + imageRightHeight).toFloat()
            LogUtils.d("右下角点：[$rightBottomXPoint, $rightBottomYPoint]")
        }
    }

    /**
     * 默认左上角：( -0.5f, 0.5f) x = (mWidth - imageWidth) / 2， y = (mHeight - imageHeight) / 2
     * 默认左下角：( -0.5f, -0.5f) x = (mWidth - imageWidth) / 2， y = (mHeight - imageHeight) / 2 + imageHeight
     * 默认右上角：( 0.5f, 0.5f) x = (mWidth - imageWidth) / 2 + imageWidth, y = (mHeight - imageHeight) / 2
     * 默认右下角：( 0.5f, -0.5f) x =  (mWidth - imageWidth) / 2 + imageWidth, y = (mHeight - imageHeight) / 2 + imageHeight
     */
    private var startX = 0F
    private var startY = 0F
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        var endX = 0F
        var endY = 0F
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                LogUtils.d("按下的点：[$startX, $startY]")
                LogUtils.d("左上角点：[$leftTopXPoint, $leftTopYPoint]")
                LogUtils.d("左下角点：[$leftBottomXPoint, $leftBottomYPoint]")
                LogUtils.d("右上角点：[$rightTopXPoint, $rightTopYPoint]")
                LogUtils.d("右下角点：[$rightBottomXPoint, $rightBottomYPoint]")
                if (isRangeOfView(leftTopXPoint, leftTopYPoint, event.x, event.y)) {
                    LogUtils.d("点击的左上角")
                    clickType = 1
                }
                if (isRangeOfView(leftBottomXPoint, leftBottomYPoint, event.x, event.y)) {
                    LogUtils.d("点击的左下角")
                    clickType = 3
                }
                if (isRangeOfView(rightTopXPoint, rightTopYPoint, event.x, event.y)) {
                    LogUtils.d("点击的右上角")
                    clickType = 2
                }
                if (isRangeOfView(rightBottomXPoint, rightBottomYPoint, event.x, event.y)) {
                    LogUtils.d("点击的右下角")
                    clickType = 4
                }
            }
            MotionEvent.ACTION_MOVE -> {
                //判断移动的比例
                endX = event.x
                endY = event.y
                when (clickType) {
                    1 -> {
                        leftTopX = (endX * 2) / mWidth - 1
                        leftTopY = 2 - 4 * endY / mHeight
                        leftTopXPoint = endX
                        leftTopYPoint = endY
                    }
                    2 -> {
                        //右上角
                        rightTopX = (endX * 2) / mWidth - 1
                        rightTopY = 2 - 4 * endY / mHeight
                        rightTopXPoint = endX
                        rightTopYPoint = endY
                    }
                    3 -> {
                        //左下角
                        leftBottomX = (endX * 2) / mWidth - 1
                        leftBottomY = 2 - 4 * endY / mHeight
                        leftBottomXPoint = endX
                        leftBottomYPoint = endY
                    }
                    4 -> {
                        //右下角
                        rightBottomX = (endX * 2) / mWidth - 1
                        rightBottomY = 2 - 4 * endY / mHeight
                        rightBottomXPoint = endX
                        rightBottomYPoint = endY
                    }
                }

                val pointData = floatArrayOf(
//                -0.5f, -0.5f,
//                -0.5f, 0.5f,
//                0.5f, 0.5f,
//                0.5f, -0.5f
                    leftBottomX, leftBottomY, //左下角
                    leftTopX, leftTopY, //左上角
                    rightTopX, rightTopY, //右上角
                    rightBottomX, rightBottomY //右下角
                )
                renderer.refresh(pointData)
                glSurfaceView!!.requestRender()
            }
            MotionEvent.ACTION_UP -> {
                clickType = -1
            }
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if (glSurfaceView != null) {
            glSurfaceView!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (glSurfaceView != null) {
            glSurfaceView!!.onPause()
        }
    }

    /**
     * 触摸点是否在视图范围内
     *
     * @return 是否在视图范围内
     */
    private fun isRangeOfView(
        dx: Float,
        dy: Float,
        ev_x: Float,
        ev_y: Float
    ): Boolean {
        //dx：点的x轴  dy：点的y轴  ev_x：手势触摸到的x轴  ev_y：手势触摸到的y轴
        // 点的 x,y 轴 上下左右各增加100像素，扩大触摸范围，判断触摸手势是否在该范围内
        val distance = kotlin.math.sqrt(
            (dx.toDouble() - ev_x).pow(2.0)
                    + (dy.toDouble() - ev_y).pow(2.0)
        ).toFloat()
        LogUtils.d("距离:${distance}")
        return  distance <= pointMargin
    }
}