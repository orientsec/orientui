package com.mobile.orientui

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.pinnedrecyclerview.R

class PinnedHeaderRecyclerView : RecyclerView {
    private var mHeaderVH: ViewHolder? = null
    private val viewCache = SparseArray<ViewHolder>()
    private var mTouchTarget: View? = null

    /**
     * 是否支持点击pinned Item，收放显示列表
     */
    private var isRetractable: Boolean

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PinnedHeaderRecyclerView, defStyle, 0)
        isRetractable = a.getBoolean(R.styleable.PinnedHeaderRecyclerView_isRetractable, true)
        a.recycle()

        addOnScrollListener(ScrollListener())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeaderVH?.let { measureChild(it.itemView, widthMeasureSpec, heightMeasureSpec) }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        mHeaderVH?.let { drawChild(canvas, it.itemView, drawingTime) }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        mHeaderVH?.let {
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            //判断是否点击的pinned Item
            if (y >= it.itemView.top && y <= it.itemView.bottom) {
                if (ev.action == MotionEvent.ACTION_DOWN) {
                    mTouchTarget = getTouchTarget(it.itemView, x, y)
                } else if (ev.action == MotionEvent.ACTION_UP) {
                    val touchTarget = getTouchTarget(it.itemView, x, y)
                    if (touchTarget === mTouchTarget && mTouchTarget!!.isClickable) {
                        mTouchTarget!!.performClick()
                        mTouchTarget = null

                        //判断是否支持点击悬浮item，收放列表
                        if (isRetractable) judgeCreateHeader()
                        invalidate()
                    }
                }
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
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
        for (i in childrenCount - 1 downTo 0) {
            val childIndex = if (isChildrenDrawingOrderEnabled) getChildDrawingOrder(childrenCount, i) else i
            val child = view.getChildAt(childIndex)
            if (child.isClickable && isTouchPointInView(child, x, y)) {
                target = child
                break
            } else if (isTouchPointInView(child, x, y)) {
                target = getTouchTarget(child, x, y)
                break
            }
        }
        if (target == null) {
            target = view
        }

        return target
    }

    private fun isTouchPointInView(view: View, x: Int, y: Int): Boolean {
        return (/*view.isClickable &&*/ y >= view.top && y <= view.bottom
                && x >= view.left && x <= view.right)
    }

    private fun setupView() {
        val firstVisiblePosition = getFirstVisiblePosition()
        val headerPosition = findPinnedHeaderPosition(firstVisiblePosition)
        createHeaderView(headerPosition)
        layoutHeaderView(firstVisiblePosition, headerPosition)
    }

    /**
     * 创建Header View
     */
    private fun createHeaderView(headerPosition: Int) {
        if (headerPosition >= 0) {
            val viewType = adapter?.getItemViewType(headerPosition) ?: return
            mHeaderVH = viewCache.get(viewType)
            if (null == mHeaderVH) {
                val viewHolder = findViewHolderForAdapterPosition(headerPosition)
                if (viewHolder?.itemView == null) {
                    return
                }
                createHeaderViewHolder(viewHolder, viewType)
                        ?.let { vh ->
                            viewCache.put(viewType, vh)
                            mHeaderVH = vh
                            bindHeaderViewHolder(vh, headerPosition)
                        }
            }
            bindHeaderViewHolder(mHeaderVH!!, headerPosition)
        } else {
            if (headerPosition == -1) {
                removeHeader()
            }
        }
    }

    /**
     * 创建Header ViewHolder
     */
    private fun createHeaderViewHolder(itemVH: ViewHolder, headerType: Int): ViewHolder? {
        val vh = adapter?.onCreateViewHolder(this, headerType) ?: return null
        vh.itemView.layoutParams = RecyclerView.LayoutParams(itemVH.itemView.measuredWidth, itemVH.itemView.measuredHeight)
        measureChild(vh.itemView, measuredWidthAndState, measuredHeightAndState)
        return vh
    }

    /**
     * 绑定header viewHolder
     */
    private fun bindHeaderViewHolder(headerVH: ViewHolder, headerPosition: Int) {
        try {
            adapter?.onBindViewHolder(headerVH, headerPosition) ?: return
        } catch (e: Exception) {
            removeHeader()
            e.printStackTrace()
        }
    }

    /**
     * 绘制headerView 位置
     */
    private fun layoutHeaderView(firstVisiblePos: Int, headerPosition: Int) {
        mHeaderVH?.let {
            val adapter = adapter ?: return
            if (firstVisiblePos + 1 < adapter.itemCount
                    && isPinnedViewType(adapter.getItemViewType(firstVisiblePos + 1))) {
                val position = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
                        ?: -1
                val view = getChildAt(1 + if (isPlateViewType(position)) 2 else 0) ?: return
                if (view.measuredHeight == it.itemView.measuredHeight
                        && view.top <= it.itemView.measuredHeight - 1) {
                    val delta = it.itemView.measuredHeight - view.top
                    it.itemView.layout(0, -delta, it.itemView.measuredWidth, it.itemView.measuredHeight - delta)
                } else if (firstVisiblePos == 0 && firstVisiblePos == headerPosition) {
                    it.itemView.layout(0, 0, 0, 0)
                } else {
                    it.itemView.layout(0, 0, it.itemView.measuredWidth, it.itemView.measuredHeight)
                }
            } else {
                it.itemView.layout(0, 0, it.itemView.measuredWidth, it.itemView.measuredHeight)
            }
        }
    }

    /**
     * 获取首个可视item position
     */
    private fun getFirstVisiblePosition(): Int {
        val adapter = adapter ?: return -1
        val layoutManager = layoutManager
        val position = (layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: -1
        if (position >= adapter.itemCount) {
            return -1
        }
        return position + if (isPlateViewType(position)) 2 else 0
    }

    /**
     * 获取需要吸顶Item position
     */
    private fun findPinnedHeaderPosition(fromPosition: Int): Int {
        val adapter = adapter ?: return -1
        if (fromPosition >= adapter.itemCount) return -1

        for (position in fromPosition downTo 0) {
            val viewType = adapter.getItemViewType(position)
            if (isPinnedViewType(viewType)) {
                return position
            }
        }

        return -1
    }

    /**
     * 判断是否为需要置顶的类型
     */
    private fun isPinnedViewType(viewType: Int): Boolean {
        if (adapter is PinnedHeaderCallBack) {
            return try {
                (adapter as PinnedHeaderCallBack).isPinnedViewType(viewType)
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    private fun isPlateViewType(position: Int): Boolean {
        val adapter = adapter ?: return false
        if (position >= adapter.itemCount) return false

        val viewType = adapter.getItemViewType(position)
        if (adapter is PinnedHeaderCallBack) {
            return try {
                (adapter as PinnedHeaderCallBack).isPlateViewType(viewType)
            } catch (e: Exception) {
                false
            }
        }
        return false
    }

    /**
     * 处理悬浮item的点击事件
     */
    private fun judgeCreateHeader() {
        val position = findPinnedHeaderPosition(getFirstVisiblePosition() + 1)
        if (position >= 0) {
            (this.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(position, 0)
            if (position == getFirstVisiblePosition()) {
                setupView()
            } else {
                removeHeader()
            }
        } else {
            removeHeader()
        }
    }

    /**
     * 删除header
     */
    private fun removeHeader() {
        mHeaderVH?.itemView?.apply {
            layout(0, 0, 0, 0)
        }
        mHeaderVH = null
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewCache.clear()
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //if (dx == 0 && dy == 0) return
            if (adapter !is PinnedHeaderCallBack) return
            setupView()
        }
    }
}