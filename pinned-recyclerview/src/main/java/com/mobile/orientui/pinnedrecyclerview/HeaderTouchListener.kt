package com.mobile.orientui.pinnedrecyclerview

import android.graphics.RectF
import android.view.*
import androidx.recyclerview.widget.RecyclerView

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 13:53
 * @author zhanglei
 */
class HeaderTouchListener(private val recyclerView: RecyclerView,
                          private val decoration: StickyHeadersDecoration) : RecyclerView.SimpleOnItemTouchListener() {
    private val mTapDetector: GestureDetector by lazy { GestureDetector(recyclerView.context, SingleTapDetector()) }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val tapDetectorResponse = this.mTapDetector.onTouchEvent(e)
        if (tapDetectorResponse) {
            // Don't return false if a single tap is detected
            return true
        }
        if (e.action == MotionEvent.ACTION_DOWN) {
            val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
            return position != -1
        }
        return false
    }

    private inner class SingleTapDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
            if (position != -1) {
                val headerView = decoration.getHeaderView(recyclerView, position)
                recyclerView.playSoundEffect(SoundEffectConstants.CLICK)
                val clickView = getTouchTarget(headerView, e.x.toInt(), e.y.toInt())
                clickView.performClick()
                recyclerView.invalidate()
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return true
        }

        /**
         * 获取点击的View
         */
        private fun getTouchTarget(view: View, x: Int, y: Int): View {
            if (view !is ViewGroup) {
                return view
            }

            val childrenCount = view.childCount
            var target: View? = null
            for (childIndex in childrenCount - 1 downTo 0) {
                val child = view.getChildAt(childIndex)
                if (child.isClickable && isTouchPointInView(child, x, y)) {
                    target = if (child is ViewGroup) {
                        getTouchTarget(child, x, y)
                    } else {
                        child
                    }
                    break
                } else if (isTouchPointInView(child, x, y)) {
                    target = getTouchTarget(child, x, y)
                    break
                }
            }
            if (target == null || !target.isClickable) {
                target = view
            }

            return target
        }

        private fun isTouchPointInView(view: View, x: Int, y: Int): Boolean {
            val top = calculateViewTop(view).toFloat()
            val left = calculateViewLeft(view).toFloat()
            val rect = RectF(left,
                    top,
                    left + view.measuredWidth,
                    top + view.measuredHeight)
            return rect.contains(x.toFloat(), y.toFloat())
        }

        private fun calculateViewTop(view: View): Int {
            var top: Int = view.top
            var p: ViewParent? = view.parent
            while (p != null) {
                top += (p as View).top
                p = (p as ViewParent).parent
            }
            return top
        }

        private fun calculateViewLeft(view: View): Int {
            var top: Int = view.left
            var p: ViewParent? = view.parent
            while (p != null) {
                top += (p as View).left
                p = (p as ViewParent).parent
            }
            return top
        }
    }

}