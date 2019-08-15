package com.mobile.orientui.pinnedrecyclerview.widgets

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @PackageName com.mobile.orientui.pinnedrecyclerview
 * @date 2019/7/1 11:01
 * @author zhanglei
 */
internal interface HeaderProvider {

    /**
     * Will provide a header view for a given position in the RecyclerView
     *
     * @param recyclerView that will display the header
     * @param position     that will be headed by the header
     * @return a header view for the given position and list
     */
    fun getHeader(recyclerView: RecyclerView, position: Int): View

    /**
     * 刷新headers
     */
    fun invalidate()
}