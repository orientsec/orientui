package com.mobile.orientui.pinnedrecyclerview.widgets

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.pinnedrecyclerview.StickyHeadersAdapter
import kotlin.math.max

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview.impls
 * @date 2019/7/1 13:24
 * @author zhanglei
 */
internal class HeaderPositionCalculator(private val mAdapter: StickyHeadersAdapter<out RecyclerView.ViewHolder>) {
    /**
     * The following fields are used as buffers for internal calculations. Their sole purpose is to avoid
     * allocating new Rect every time we need one.
     */
    private val mTempRect1 = Rect()
    private val mTempRect2 = Rect()

    private val headerPositions = mutableListOf<Int>()

    /**
     * 判断是否在显示header范围内
     */
    fun isInHeaderRegion(position: Int): Boolean {
        val headerPosition = getHeaderPosition(position)
        return headerPosition != RecyclerView.NO_POSITION
    }

    /**
     * 初始化header rect
     * @param bounds 返回header的rect
     */
    fun initHeaderBounds(bounds: Rect,
                         recyclerView: RecyclerView,
                         header: View) {
        initDefaultHeaderOffset(bounds, recyclerView, header, recyclerView.getChildAt(0))

        if (pushOffscreen(recyclerView, header)) {
            val firstChildPos = recyclerView.getChildAdapterPosition(recyclerView.getChildAt(0))
            val nextHeaderPos = getNextHeaderPosition(firstChildPos)
            val nextHeaderView = recyclerView.getChildAt(nextHeaderPos - firstChildPos)
            translateHeaderWithNextHeader(recyclerView,
                    bounds,
                    header,
                    nextHeaderView)
        }
    }

    /**
     * 初始化header rect
     */
    private fun initDefaultHeaderOffset(headerMargins: Rect,
                                        recyclerView: RecyclerView,
                                        header: View,
                                        firstView: View) {
        val translationX: Int
        val translationY: Int
        initMargins(mTempRect1, header)

        val layoutParams = firstView.layoutParams
        var leftMargin = 0
        var topMargin = 0
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            leftMargin = layoutParams.leftMargin
            topMargin = layoutParams.topMargin
        }

        if (recyclerView.orientation == LinearLayoutManager.VERTICAL) {
            translationX = firstView.left - leftMargin + mTempRect1.left
            translationY = max(firstView.top - topMargin - header.height - mTempRect1.bottom, getListTop(recyclerView) + mTempRect1.top)
        } else {
            translationY = firstView.top - topMargin + mTempRect1.top
            translationX = max(firstView.left - leftMargin - header.width - mTempRect1.right, getListLeft(recyclerView) + mTempRect1.left)
        }

        headerMargins.set(translationX,
                translationY,
                translationX + header.width,
                translationY + header.height)
    }

    /**
     * 判断是否将header滑出screen
     */
    private fun pushOffscreen(parent: RecyclerView, header: View): Boolean {
        val firstChildPos = parent.getChildAdapterPosition(parent.getChildAt(0))
        if (!isInHeaderRegion(firstChildPos)) {
            return false
        }
        val lastChildPos = parent.getChildAdapterPosition(parent.getChildAt(parent.childCount - 1))
        val firstHeaderPos = getHeaderPosition(firstChildPos)
        if (firstChildPos == RecyclerView.NO_POSITION) {
            return false
        }
        val lastHeaderPos = getHeaderPosition(lastChildPos)
        if (firstHeaderPos != lastHeaderPos) {
            val nextHeaderPos = getNextHeaderPosition(firstChildPos)
            val nextHeaderView = parent.getChildAt(nextHeaderPos - firstChildPos)
            return itemIsObscuredByHeader(parent, nextHeaderView, header)
        }
        return false
    }

    private fun translateHeaderWithNextHeader(recyclerView: RecyclerView, translation: Rect, currentHeader: View, nextHeader: View) {
        initMargins(mTempRect1, nextHeader)
        initMargins(mTempRect2, currentHeader)
        if (recyclerView.orientation == LinearLayoutManager.VERTICAL) {
            val topOfStickyHeader = getListTop(recyclerView) + mTempRect2.top + mTempRect2.bottom
            translation.top = nextHeader.top - mTempRect1.bottom - mTempRect1.top - currentHeader.height - topOfStickyHeader
            translation.bottom = nextHeader.top
        } else {
            val leftOfStickyHeader = getListLeft(recyclerView) + mTempRect2.left + mTempRect2.right
            translation.left = nextHeader.left - mTempRect1.left - mTempRect1.right - currentHeader.width - leftOfStickyHeader
            translation.right = nextHeader.left
        }
    }

    /**
     * Determines if an item is obscured by a header
     *
     *
     * @param parent
     * @param item        to determine if obscured by header
     * @param header      that might be obscuring the item
     * @return true if the item view is obscured by the header view
     */
    private fun itemIsObscuredByHeader(parent: RecyclerView, item: View, header: View): Boolean {
        val layoutParams = item.layoutParams as RecyclerView.LayoutParams
        initMargins(mTempRect1, header)

        val adapterPosition = parent.getChildAdapterPosition(item)
        if (adapterPosition == RecyclerView.NO_POSITION) {
            // Resolves https://github.com/timehop/sticky-headers-recyclerview/issues/36
            // Handles an edge case where a trailing header is smaller than the current sticky header.
            return false
        }

        if (parent.orientation == LinearLayoutManager.VERTICAL) {
            val itemTop = item.top - layoutParams.topMargin
            val headerBottom = getListTop(parent) + header.bottom + mTempRect1.bottom + mTempRect1.top
            if (itemTop >= headerBottom) {
                return false
            }
        } else {
            val itemLeft = item.left - layoutParams.leftMargin
            val headerRight = getListLeft(parent) + header.right + mTempRect1.right + mTempRect1.left
            if (itemLeft >= headerRight) {
                return false
            }
        }

        return true
    }

    private fun getListTop(view: RecyclerView): Int {
        return if (view.layoutManager?.clipToPadding == true) {
            view.paddingTop
        } else {
            0
        }
    }

    private fun getListLeft(view: RecyclerView): Int {
        return if (view.layoutManager?.clipToPadding == true) {
            view.paddingLeft
        } else {
            0
        }
    }

    /**
     * 计算header的位置
     */
    fun calculateHeaderPosition() {
        headerPositions.clear()
        for (index in 0 until mAdapter.getItemCount()) {
            val viewType = mAdapter.getItemViewType(index)
            if (mAdapter.isStickyHeaderViewType(viewType)) {
                headerPositions.add(index)
            }
        }
        if (headerPositions.isNotEmpty() && headerPositions.last() != mAdapter.getItemCount() - 1) {
            headerPositions.add(mAdapter.getItemCount() - 1)
        }
    }

    fun getHeaderPosition(fromPosition: Int): Int {
        if (headerPositions.isEmpty()) return RecyclerView.NO_POSITION
        headerPositions.reduce { acc, i ->
            if (fromPosition in acc until i) {
                return acc
            }
            i
        }
        return RecyclerView.NO_POSITION
    }

    private fun getNextHeaderPosition(position: Int): Int {
        if (headerPositions.isEmpty()) return RecyclerView.NO_POSITION
        headerPositions.reduce { acc, i ->
            if (position in acc until i) {
                return i
            }
            i
        }
        return RecyclerView.NO_POSITION
    }
}