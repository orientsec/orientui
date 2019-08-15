package com.mobile.orientui.pinnedrecyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 10:54
 * @author zhanglei
 */
interface StickyHeadersAdapter<VH : RecyclerView.ViewHolder> {
    /**
     * 是否为标题栏可以悬浮显示
     */
    fun isStickyHeaderViewType(viewType: Int): Boolean

    /**
     * Called when RecyclerView needs a new [RecyclerView.ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [RecyclerView.ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [RecyclerView.ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    fun onBindViewHolder(holder: VH, position: Int)

    /**
     * Return the view type of the item at `position` for the purposes
     * of view recycling.
     *
     *
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    fun getItemViewType(position: Int): Int

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    fun getItemCount(): Int

    /**
     * Register a new observer to listen for data changes.
     *
     *
     * The adapter may publish a variety of events describing specific changes.
     * Not all adapters may support all change types and some may fall back to a generic
     * [ &quot;something changed&quot;][RecyclerView.AdapterDataObserver.onChanged] event if more specific data is not available.
     *
     *
     * Components registering observers with an adapter are responsible for
     * [ unregistering][.unregisterAdapterDataObserver] those observers when finished.
     *
     * @param observer Observer to register
     *
     * @see .unregisterAdapterDataObserver
     */
    fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver)
}