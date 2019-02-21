package com.mobile.orientui

/**
 * 用于定义Adapter的item类型
 */
interface PinnedHeaderCallBack {
    /**
     * 是否为标题栏可以悬浮显示
     */
    fun isPinnedViewType(viewType: Int): Boolean

    /**
     * 是否为板块类型，多列显示
     */
    fun isPlateViewType(viewType: Int): Boolean
}