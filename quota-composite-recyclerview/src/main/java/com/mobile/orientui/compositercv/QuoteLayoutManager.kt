package com.mobile.orientui.compositercv

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.SparseIntArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/12/2 15:07
 * @author songdongqi
 */
class QuoteLayoutManager(build: Builder) : RecyclerView.LayoutManager() {
    companion object {
        private const val DEBUG = true
        private const val TAG = "QuoteLayoutManager"
    }

    private val mLayoutState by lazy { LayoutState() }

    init {
        mLayoutState.mColumnCount = build.mColumnCount
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): RecyclerView.LayoutParams {
        return LayoutParams(lp)
    }

    /**
     *  分为五个区域：
     *  头部左侧区域，固定
     *  头部右侧区域，可以横向滑动，不可以横向滑动
     *  中间左侧区域，可以上下滑动，不可以左右滑动
     *  中间右侧区域，可以上下左右滑动
     *  底部foot区域，可以上下滑动，不可以左右滑动
     */
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout || mLayoutState.mColumnCount <= 0) return
        //确定总共有多少行
        mLayoutState.mRowCount = if (itemCount % mLayoutState.mColumnCount == 0) {
            itemCount / mLayoutState.mColumnCount
        } else {
            itemCount / mLayoutState.mColumnCount + 1
        }

        //内容显示区域(可滑动的区域)
        mLayoutState.mSlideAreaRect.set(0, 0, getHorizontalActiveWidth(), getVerticalActiveHeight())

        // 先移除所有view
        detachAndScrapAttachedViews(recycler)
        calculateSpreadSize(recycler)
        fillChildren(recycler, state)
    }

    /**
     * 计算平铺出来的宽度和高度
     */
    private fun calculateSpreadSize(recycler: RecyclerView.Recycler) {
        if (itemCount == 0) return
        mLayoutState.mSpreadHeight = 0
        mLayoutState.mSpreadWidth = 0

        // 平铺高度
        for (row in 0 until mLayoutState.mRowCount) {
            val position = mLayoutState.mColumnCount * row
            val view = recycler.getViewForPosition(position)
            measureChildWithMargins(view, 0, 0)
            val height = getDecoratedMeasurementVertical(view)
            mLayoutState.mSpreadHeight += height
            mLayoutState.mEachRowHeightList.put(row, height)
        }

        // 平铺宽度
        val mColumnCount = if (itemCount == 1) itemCount else mLayoutState.mColumnCount
        for (column in 0 until mColumnCount) {
            val view = recycler.getViewForPosition(column)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasurementHorizontal(view)
            mLayoutState.mSpreadWidth += width
            mLayoutState.mEachColumnWidthList.put(column, width)
        }
    }

    private fun fillChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0 || state.isPreLayout) return

        if (DEBUG) {
            Log.d(TAG, "start fill children")
        }

        // layout algorithm:
        val itemRect = Rect()
        val rowStart = getDisplayRowStart()
        val rowEnd = getDisplayRowEnd()
        val columnStart = getDisplayColumnStart()
        val columnEnd = getDisplayColumnEnd()

        //中间右侧区域，可以上下左右滑动
        var preRowHeight = getPreRowHeight(rowStart)
        for (row in rowStart..rowEnd) {
            var preColumnWidth = getPreColumnWidth(columnStart)
            for (column in columnStart..columnEnd) {
                val position = row * mLayoutState.mColumnCount + column

                if (row == mLayoutState.mRowCount - 1 && position >= itemCount) break

                itemRect.set(preColumnWidth, preRowHeight, preColumnWidth + mLayoutState.mEachColumnWidthList.get(column),
                        preRowHeight + mLayoutState.mEachRowHeightList.get(row))

                if (row != 0 && column > 0 && Rect.intersects(mLayoutState.mSlideAreaRect, itemRect)) {
                    val view = recycler.getViewForPosition(position)
                    addView(view)
                    measureChildWithMargins(view, 0, 0)
                    //view 的真实宽度
                    layoutDecoratedWithMargins(view, itemRect.left - mLayoutState.mOffsetHorizontal,
                            itemRect.top - mLayoutState.mOffsetVertical,
                            itemRect.right - mLayoutState.mOffsetHorizontal,
                            itemRect.bottom - mLayoutState.mOffsetVertical)
                }
                preColumnWidth += mLayoutState.mEachColumnWidthList.get(column)
            }
            preRowHeight += mLayoutState.mEachRowHeightList.get(row)
        }

        //处理列固定
        preRowHeight = getPreRowHeight(rowStart)
        for (row in rowStart..rowEnd) {
            val position = row * mLayoutState.mColumnCount
            if (row == mLayoutState.mRowCount - 1 && position >= itemCount) break

            itemRect.set(0, preRowHeight, mLayoutState.mEachColumnWidthList.get(0),
                    preRowHeight + mLayoutState.mEachRowHeightList.get(row))
            if (row > 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                //view 的真实宽度
                val sizeHorizontal = getDecoratedMeasurementHorizontal(view)
                layoutDecoratedWithMargins(view, 0, itemRect.top - mLayoutState.mOffsetVertical,
                        sizeHorizontal, itemRect.bottom - mLayoutState.mOffsetVertical)
            }
            preRowHeight += mLayoutState.mEachRowHeightList.get(row)
        }

        //处理行固定，固定第一行
        var preColumnWidth = getPreColumnWidth(columnStart)
        for (column in columnStart..columnEnd) {
            if (column >= itemCount) break
            itemRect.set(preColumnWidth, 0,
                    preColumnWidth + mLayoutState.mEachColumnWidthList.get(column), mLayoutState.mEachRowHeightList.get(0))
            if (column > 0) {
                val view = recycler.getViewForPosition(column)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                layoutDecoratedWithMargins(view, itemRect.left - mLayoutState.mOffsetHorizontal, itemRect.top,
                        itemRect.right - mLayoutState.mOffsetHorizontal, itemRect.bottom)
            }
            preColumnWidth += mLayoutState.mEachColumnWidthList.get(column)
        }

        //处理头部左侧区域，固定
        itemRect.set(0, 0, 0 + mLayoutState.mEachColumnWidthList.get(0), mLayoutState.mEachRowHeightList.get(0))
        val view = recycler.getViewForPosition(0)
        addView(view)
        measureChildWithMargins(view, 0, 0)
        layoutDecoratedWithMargins(view, itemRect.left, itemRect.top, itemRect.right, itemRect.bottom)

        if (DEBUG) {
            Log.d(TAG, "end fill children")
        }
    }

    /**
     * 获取要显示行开始的位置
     */
    public fun getDisplayRowStart(): Int {
        var itemBottom = 0
        for (row in 0 until mLayoutState.mRowCount) {
            itemBottom += mLayoutState.mEachRowHeightList.get(row)
            if (itemBottom > mLayoutState.mSlideAreaRect.top) {
                return row
            }
        }
        return 0
    }

    /**
     * 获取要显示行结束的位置
     */
    public fun getDisplayRowEnd(): Int {
        var itemTop = 0
        for (row in 0 until mLayoutState.mRowCount) {
            itemTop += mLayoutState.mEachRowHeightList.get(row)
            if (itemTop >= mLayoutState.mSlideAreaRect.bottom) {
                return row
            }
        }
        return mLayoutState.mRowCount - 1
    }

    /**
     * 获取显示列开始的位置
     */
    public fun getDisplayColumnStart(): Int {
        var columnPre = 0
        for (column in 0 until mLayoutState.mEachColumnWidthList.size()) {
            val itemRight = columnPre + mLayoutState.mEachColumnWidthList.get(column)
            if (itemRight > mLayoutState.mSlideAreaRect.left) {
                return column
            }
            columnPre += mLayoutState.mEachColumnWidthList.get(column)
        }
        return 0
    }

    /**
     * 获取显示列结束的位置
     */
    public fun getDisplayColumnEnd(): Int {
        var columnPre = 0
        for (column in 0 until mLayoutState.mEachColumnWidthList.size()) {
            val itemLeft = columnPre
            if (itemLeft >= mLayoutState.mSlideAreaRect.right) {
                return column
            }
            columnPre += mLayoutState.mEachColumnWidthList.get(column)
        }
        return mLayoutState.mEachColumnWidthList.size() - 1
    }

    /**
     * 获取指定行前面的高度
     */
    private fun getPreRowHeight(row: Int): Int {
        var preRowHeight = 0
        for (index in 0 until row) {
            preRowHeight += mLayoutState.mEachRowHeightList.get(index)
        }
        return preRowHeight
    }

    /**
     * 获取指定列前面的宽度
     */
    private fun getPreColumnWidth(column: Int): Int {
        if (mLayoutState.mEachColumnWidthList.size() == 0) return 0
        var preColumnWidth = 0
        for (index in 0 until column) {
            preColumnWidth += mLayoutState.mEachColumnWidthList.get(index)
        }
        return preColumnWidth
    }

    override fun canScrollVertically(): Boolean {
        return mLayoutState.mSpreadHeight > getVerticalActiveHeight()
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var offsetY = dy
        // 先移除所有view
        detachAndScrapAttachedViews(recycler)
        if (mLayoutState.mOffsetVertical + offsetY > mLayoutState.mSpreadHeight - getVerticalActiveHeight()) {
            offsetY = mLayoutState.mSpreadHeight - getVerticalActiveHeight() - mLayoutState.mOffsetVertical
        } else if (mLayoutState.mOffsetVertical + offsetY < 0) {
            offsetY = -mLayoutState.mOffsetVertical
        }
        mLayoutState.mOffsetVertical += offsetY
        mLayoutState.mSlideAreaRect.offset(0, offsetY)
        fillChildren(recycler, state)
        return offsetY
    }

    /**
     * 平铺宽度大于内容宽度，水平可以滑动
     */
    override fun canScrollHorizontally(): Boolean {
        return mLayoutState.mSpreadWidth > getHorizontalActiveWidth()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var offsetX = dx
        // 先移除所有view
        detachAndScrapAttachedViews(recycler)
        if (mLayoutState.mOffsetHorizontal + offsetX > mLayoutState.mSpreadWidth - getHorizontalActiveWidth()) {
            offsetX = mLayoutState.mSpreadWidth - getHorizontalActiveWidth() - mLayoutState.mOffsetHorizontal
        } else if (mLayoutState.mOffsetHorizontal + offsetX < 0) {
            offsetX = -mLayoutState.mOffsetHorizontal
        }
        mLayoutState.mOffsetHorizontal += offsetX
        mLayoutState.mSlideAreaRect.offset(offsetX, 0)
        fillChildren(recycler, state)
        return offsetX
    }

    private fun getHorizontalActiveWidth(): Int {
        return width - paddingLeft - paddingRight
    }

    private fun getVerticalActiveHeight(): Int {
        return height - paddingTop - paddingBottom
    }

    /**
     * 获取 child view 横向上需要占用的空间，margin计算在内
     * @param view item view
     * @return child view 横向占用的空间
     */
    private fun getDecoratedMeasurementHorizontal(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return (getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin)
    }

    /**
     * 获取 child view 纵向上需要占用的空间，margin计算在内
     * @param view item view
     * @return child view 纵向占用的空间
     */
    private fun getDecoratedMeasurementVertical(view: View): Int {
        val params = view.layoutParams as RecyclerView.LayoutParams
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin
    }


    public fun findFirstVisibleItemPosition(): Int {
        return getDisplayRowStart() * mLayoutState.mColumnCount + getDisplayColumnStart()
    }

    public fun findLastVisibleItemPosition(): Int {
        return getDisplayRowEnd() * mLayoutState.mColumnCount + getDisplayColumnEnd()
    }

    class LayoutState {
        /**
         * 总行数
         */
        var mRowCount: Int = 0

        /**
         * 总列数
         */
        var mColumnCount: Int = 0

        /**
         * 水平偏移量
         */
        var mOffsetHorizontal: Int = 0
        /**
         * 垂直偏移量
         */
        var mOffsetVertical: Int = 0

        /**
         * 整个平铺开来，总宽度
         */
        var mSpreadWidth: Int = 0
        /**
         * 整个平铺开来，总高度
         */
        var mSpreadHeight: Int = 0
        /**
         * 每一列对应的宽度list
         */
        var mEachColumnWidthList: SparseIntArray = SparseIntArray()
        /**
         * 每一列对应的宽度list
         */
        var mEachRowHeightList: SparseIntArray = SparseIntArray()

        /**
         * 内容区域(可滑动的区域),绘制的时候只绘制内容区域内的item
         */
        var mSlideAreaRect: Rect = Rect()
    }

    class Builder {
        /**
         * 列数
         */
        var mColumnCount = 0

        fun setColumnCount(column: Int): Builder {
            mColumnCount = column
            return this
        }

        fun build(): QuoteLayoutManager {
            return QuoteLayoutManager(this)
        }
    }


    class LayoutParams : RecyclerView.LayoutParams {

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: ViewGroup.MarginLayoutParams) : super(source)

        constructor(source: ViewGroup.LayoutParams) : super(source)

        constructor(source: RecyclerView.LayoutParams) : super(source)

    }
}
