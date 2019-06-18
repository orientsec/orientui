package com.mobile.orientui.rankinggroup

import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.pinnedrecyclerview.PinnedHeaderCallBack
import java.util.ArrayList

/**
 * 行情列表base adapter
 */
abstract class RankingBaseAdapter<VH : RecyclerView.ViewHolder>(head: RankingBaseItemModel = RankingBaseItemModel(HEAD_ITEM)) : RecyclerView.Adapter<VH>(), PinnedHeaderCallBack {
    var itemList = mutableListOf(head)
        private set

    //头部item标题点击事件
    lateinit var titleClickListener: (Any) -> Unit

    override fun getItemViewType(position: Int): Int {
        return if (itemList.size > 0) (itemList[position]).itemType else NO_DATA_ITEM
    }

    override fun getItemCount(): Int = itemList.size

    override fun isPinnedViewType(viewType: Int): Boolean = viewType == HEAD_ITEM

    override fun isPlateViewType(viewType: Int): Boolean = false
    //最新数据列表
    var latestCodeList: List<RankingBaseItemModel> = emptyList()

    /**
     * 刷新列表数据
     * @param startPosition: 列表刷新的起始位置，绝对位置
     */
    fun updateItems(startPosition: Int, items: List<RankingBaseItemModel>, first: Int, visible: Int) {
        if (items.isNotEmpty()) {
            val list = if (itemCount > startPosition + items.size)
                itemList.subList(0, startPosition) + items + itemList.subList(startPosition + items.size, itemCount)
            else {
                itemList.subList(0, startPosition) + items.subList(0, itemCount - startPosition)
            }
            itemList = list as ArrayList
            val exactVisible = Math.min(visible, itemCount - first)
            notifyItemRangeChanged(first, exactVisible, 1)
            latestCodeList = items
        }
    }

    /**
     * 上拉加载
     * 添加数据，刷新列表
     */
    fun insertItems(items: List<RankingBaseItemModel>) {
        if (items.isNotEmpty()) {
            val startPosition = itemList.size
            itemList = (itemList + items) as ArrayList
            notifyItemRangeInserted(startPosition, items.size)
            latestCodeList = items
        }
    }

    /**
     * 重置item
     * 使每项item值为空
     */
    fun resetItems(resetModel: RankingBaseItemModel) {
        val emptyList = mutableListOf<RankingBaseItemModel>()
        for (i in 1 until itemList.size) {
            emptyList.add(resetModel)
        }
        itemList = (itemList.subList(0, 1) + emptyList) as ArrayList
    }

    /**
     * 设置数据
     */
    fun setupItemList(list: List<RankingBaseItemModel>) {
        itemList = list as MutableList<RankingBaseItemModel>
        notifyDataSetChanged()
    }
}