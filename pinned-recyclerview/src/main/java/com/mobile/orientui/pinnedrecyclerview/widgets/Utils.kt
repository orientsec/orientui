package com.mobile.orientui.pinnedrecyclerview.widgets

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 13:03
 * @author zhanglei
 */

/**
 * Draws a header to a canvas, offsetting by some x and y amount
 *
 * @param recyclerView the parent recycler view for drawing the header into
 * @param canvas       the canvas on which to draw the header
 * @param header       the view to draw as the header
 * @param offset       a Rect used to define the x/y offset of the header. Specify x/y offset by setting
 * the [Rect.left] and [Rect.top] properties, respectively.
 */
internal fun drawHeader(recyclerView: RecyclerView,
                        canvas: Canvas,
                        header: View,
                        offset: Rect) {
    canvas.save()

    if (recyclerView.layoutManager?.clipToPadding == true) {
        val mTempRect = Rect()
        // Clip drawing of headers to the padding of the RecyclerView. Avoids drawing in the padding
        initClipRectForHeader(mTempRect, recyclerView, header)
        canvas.clipRect(mTempRect)
    }

    canvas.translate(offset.left.toFloat(), offset.top.toFloat())

    header.draw(canvas)
    canvas.restore()
}

/**
 * Initializes a clipping rect for the header based on the margins of the header and the padding of the
 * recycler.
 * FIXME: Currently right margin in VERTICAL orientation and bottom margin in HORIZONTAL
 * orientation are clipped so they look accurate, but the headers are not being drawn at the
 * correctly smaller width and height respectively.
 *
 * @param clipRect [Rect] for clipping a provided header to the padding of a recycler view
 * @param recyclerView for which to provide a header
 * @param header       for clipping
 */
private fun initClipRectForHeader(clipRect: Rect,
                                  recyclerView: RecyclerView,
                                  header: View) {
    initMargins(clipRect, header)
    if (recyclerView.orientation == RecyclerView.VERTICAL) {
        clipRect.set(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.width - recyclerView.paddingRight - clipRect.right,
                recyclerView.height - recyclerView.paddingBottom)
    } else {
        clipRect.set(
                recyclerView.paddingLeft,
                recyclerView.paddingTop,
                recyclerView.width - recyclerView.paddingRight,
                recyclerView.height - recyclerView.paddingBottom - clipRect.bottom)
    }
}

/**
 * Populates [Rect] with margins for any view.
 *
 *
 * @param margins rect to populate
 * @param view for which to get margins
 */
internal fun initMargins(margins: Rect, view: View) {
    val layoutParams = view.layoutParams
    if (layoutParams is ViewGroup.MarginLayoutParams) {
        initMarginRect(margins, layoutParams)
    } else {
        margins.set(0, 0, 0, 0)
    }
}

/**
 * Converts [ViewGroup.MarginLayoutParams] into a representative [Rect].
 *
 * @param marginRect Rect to be initialized with margins coordinates, where
 * [ViewGroup.MarginLayoutParams.leftMargin] is equivalent to [Rect.left], etc.
 * @param marginLayoutParams margins to populate the Rect with
 */
private fun initMarginRect(marginRect: Rect, marginLayoutParams: ViewGroup.MarginLayoutParams) {
    marginRect.set(
            marginLayoutParams.leftMargin,
            marginLayoutParams.topMargin,
            marginLayoutParams.rightMargin,
            marginLayoutParams.bottomMargin
    )
}

@RecyclerView.Orientation
internal val RecyclerView.orientation: Int
    get() {
        throwIfNotLinearLayoutManager(layoutManager)
        return (layoutManager as LinearLayoutManager).orientation
    }

private fun throwIfNotLinearLayoutManager(layoutManager: RecyclerView.LayoutManager?) {
    if (layoutManager !is LinearLayoutManager) {
        throw IllegalStateException("PinnedHeadersDecoration can only be used with a " + "LinearLayoutManager.")
    }
}