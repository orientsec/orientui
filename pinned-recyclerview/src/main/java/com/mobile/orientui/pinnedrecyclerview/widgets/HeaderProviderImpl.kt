package com.mobile.orientui.pinnedrecyclerview.widgets

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.pinnedrecyclerview.StickyHeadersAdapter

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 11:08
 * @author zhanglei
 */
internal class HeaderProviderImpl<VH : RecyclerView.ViewHolder>(private val adapter: StickyHeadersAdapter<VH>) : HeaderProvider {
    private val mHeaderViews = SparseArray<VH>()

    override fun getHeader(recyclerView: RecyclerView, position: Int): View {
        val headerViewType = adapter.getItemViewType(position)
        var vh: VH? = mHeaderViews.get(headerViewType)
        if (vh == null) {
            vh = adapter.onCreateViewHolder(recyclerView, headerViewType)
            val itemView = vh.itemView
            if (itemView.layoutParams == null) {
                itemView.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            }

            val widthSpec: Int
            val heightSpec: Int

            if (recyclerView.orientation == RecyclerView.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.UNSPECIFIED)
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.UNSPECIFIED)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.EXACTLY)
            }

            val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    recyclerView.paddingLeft + recyclerView.paddingRight, itemView.layoutParams.width)
            val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    recyclerView.paddingTop + recyclerView.paddingBottom, itemView.layoutParams.height)
            itemView.measure(childWidth, childHeight)
            itemView.layout(0, 0, itemView.measuredWidth, itemView.measuredHeight)
            mHeaderViews.put(headerViewType, vh)
        }
        adapter.onBindViewHolder(vh, position)
        return vh.itemView
    }

    override fun invalidate() {
        mHeaderViews.clear()
    }

}
