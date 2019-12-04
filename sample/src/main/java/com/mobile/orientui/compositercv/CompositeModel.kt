package com.mobile.orientui.compositercv

/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/10/30 13:31
 * @author songdongqi
 */
const val HEAD_ITEM_RIGHT = 0
const val HEAD_ITEM_LEFT=1
const val NORMAL_ITEM_LEFT = 2
const val NORMAL_ITEM_RIGHT=3
const val BOTTOM_ITEM = 4
const val NO_DATA_ITEM = 5

open class CompositeBaseItemModel(val itemType: Int)