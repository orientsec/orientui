package com.mobile.orientui.pinnedrecyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.pinnedrecyclerview.widgets.HeaderPositionCalculator
import com.mobile.orientui.pinnedrecyclerview.widgets.HeaderProvider
import com.mobile.orientui.pinnedrecyclerview.widgets.HeaderProviderImpl
import com.mobile.orientui.pinnedrecyclerview.widgets.drawHeader

/**
 *
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 10:52
 * @author zhanglei
 */
class StickyHeadersDecoration(private val adapter: StickyHeadersAdapter<out RecyclerView.ViewHolder>) : RecyclerView.ItemDecoration() {
    init {
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                invalidateHeaders()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                invalidateHeaders()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                invalidateHeaders()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                invalidateHeaders()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                invalidateHeaders()
            }

        })
    }

    private val headerProvider: HeaderProvider = HeaderProviderImpl(adapter)

    private val mHeaderRects = SparseArray<Rect>()

    private val calculator: HeaderPositionCalculator by lazy { HeaderPositionCalculator(adapter) }

    override fun getItemOffsets(outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)

        val childCount = parent.childCount
        if (childCount <= 0 || adapter.getItemCount() <= 0) {
            return
        }

        val firstChildPos = parent.getChildAdapterPosition(parent.getChildAt(0))
        if (!calculator.isInHeaderRegion(firstChildPos)) {
            return
        }
        val firstHeaderPos = calculator.getHeaderPosition(firstChildPos)
        val header = getHeaderView(parent, firstHeaderPos)
        //re-use existing Rect, if any.
        val headerOffset: Rect = mHeaderRects.get(firstHeaderPos) ?: Rect()
        mHeaderRects.put(firstHeaderPos, headerOffset)

        calculator.initHeaderBounds(headerOffset, parent, header)
        drawHeader(parent, canvas, header, headerOffset)
    }

    /**
     * Gets the position of the header under the specified (x, y) coordinates.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return position of header, or -1 if not found
     */
    internal fun findHeaderPositionUnder(x: Int, y: Int): Int {
        for (i in 0 until mHeaderRects.size()) {
            val rect = mHeaderRects.get(mHeaderRects.keyAt(i))
            if (rect.contains(x, y)) {
                return mHeaderRects.keyAt(i)
            }
        }
        return -1
    }

    /**
     * Gets the header view for the associated position.  If it doesn't exist yet, it will be
     * created, measured, and laid out.
     *
     * @param parent   the recyclerview
     * @param position the position to get the header view for
     * @return Header view
     */
    internal fun getHeaderView(parent: RecyclerView, position: Int): View {
        return headerProvider.getHeader(parent, position)
    }

    /**
     * Invalidates cached headers.  This does not invalidate the recyclerview, you should do that manually after
     * calling this method.
     */
    private fun invalidateHeaders() {
        headerProvider.invalidate()
        mHeaderRects.clear()
        calculator.calculateHeaderPosition()
    }
}