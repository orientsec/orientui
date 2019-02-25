package com.mobile.orientui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * 控制右侧列表，横向和纵向的滑动冲突
 */
class RankingHorizontalScrollView(context: Context, attrs: AttributeSet) : HorizontalScrollView(context, attrs) {
    companion object {
        const val CONST_FIVE = 5
    }

    private var lastX: Int = 0
    private var lastY: Int = 0

    /**
     * 控制顶层RecyclerView和子RecyclerView的touch事件顺序
     * 顶层RecyclerView处理垂直事件
     * 子RecyclerView处理横向事件
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        val x = ev.x.toInt()
        val y = ev.y.toInt()

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
                onTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                intercept = Math.abs(x - lastX) - dp2px(context, CONST_FIVE) > Math.abs(y - lastY)
            }
            MotionEvent.ACTION_UP -> {
                intercept = false
            }
            else -> intercept = super.onInterceptTouchEvent(ev)
        }
        lastX = x
        lastY = y
        return intercept
    }
}