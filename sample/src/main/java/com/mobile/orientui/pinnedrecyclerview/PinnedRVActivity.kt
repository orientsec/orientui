package com.mobile.orientui.pinnedrecyclerview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.*
import com.mobile.orientui.R
import kotlinx.android.synthetic.main.pinned_rv_activity.*
import com.mobile.orientui.divider.HorizontalDividerItemDecoration

class PinnedRVActivity : AppCompatActivity() {

    private lateinit var mAdapter: PinnedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pinned_rv_activity)

        initAdapter()
        initView()
        initData()
    }

    private fun initAdapter() {
        mAdapter = PinnedAdapter()
    }

    private fun initView() {

        pinned_rv.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                    .margin(15, 15)
                    .size(1)
                    .color(Color.parseColor("#c4c4c4"))
                    .build())
        }
    }

    private fun initData() {
        var list = listOf<RankingBaseItemModel>(HeadItem())

        for (i in 0 until 20) {
            list += listOf(BodyItem())
        }

        val endList = list + list + list
        mAdapter.setupItemList(endList)
    }
}

class PinnedAdapter : RecyclerView.Adapter<PinnedAdapter.ViewHolder>(), PinnedHeaderCallBack {
    internal var itemList = mutableListOf<RankingBaseItemModel>()
        private set

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
            is HeadItem -> R.layout.pinned_rv_layout_head_item
            is BodyItem -> R.layout.pinned_rv_layout_body_item
            else -> 0
        }
    }

    override fun isPinnedViewType(viewType: Int): Boolean = viewType == R.layout.pinned_rv_layout_head_item

    override fun isPlateViewType(viewType: Int): Boolean = false

    override fun onBindViewHolder(holder: PinnedAdapter.ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PinnedAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bindView(item: RankingBaseItemModel) {

        }
    }

    /**
     * 设置数据
     */
    internal fun setupItemList(list: List<RankingBaseItemModel>) {
        itemList = list as MutableList<RankingBaseItemModel>
        notifyDataSetChanged()
    }
}