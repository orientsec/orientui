package com.mobile.orientui.pinnedrecyclerview

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.mobile.orientui.BodyItem
import com.mobile.orientui.HeadItem
import com.mobile.orientui.R
import com.mobile.orientui.divider.HorizontalDividerItemDecoration
import com.mobile.orientui.rankinggroup.RankingBaseItemModel
import kotlinx.android.synthetic.main.pinned_rv_activity.*
import kotlinx.android.synthetic.main.pinned_rv_layout_head_item.view.*

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
            val decoration = StickyHeadersDecoration(mAdapter)
            addItemDecoration(decoration)
            addOnItemTouchListener(HeaderTouchListener(this, decoration))
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                    .margin(15, 15)
                    .size(1)
                    .color(Color.parseColor("#c4c4c4"))
                    .build())


        }
    }

    private fun initData() {
        val list = mutableListOf<RankingBaseItemModel>(HeadItem())

        for (i in 0 until 20) {
            list += listOf(BodyItem())
        }

        val list2 = mutableListOf(RankingBaseItemModel(2))

        for (i in 0 until 20) {
            list2 += listOf(BodyItem())
        }

        val list3 = mutableListOf(RankingBaseItemModel(3))
        for (i in 0 until 20) {
            list3 += listOf(BodyItem())
        }

        val endList = list + list2 + list3 + list + list2 + list3 + list + list2 + list3
        mAdapter.setupItemList(endList)
    }
}

class PinnedAdapter : RecyclerView.Adapter<PinnedAdapter.ViewHolder>(), StickyHeadersAdapter<PinnedAdapter.ViewHolder> {
    companion object {
        private const val TAG = "Pinned"
    }

    private var itemList = mutableListOf<RankingBaseItemModel>()

    override fun getItemViewType(position: Int): Int {
        return itemList[position].itemType
    }

    override fun isStickyHeaderViewType(viewType: Int): Boolean = viewType != 1

//    override fun isPlateViewType(viewType: Int): Boolean = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemId = when (viewType) {
            1 -> R.layout.pinned_rv_layout_body_item
            else -> R.layout.pinned_rv_layout_head_item
        }
        val itemView = LayoutInflater.from(parent.context).inflate(itemId, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bindView(item: RankingBaseItemModel) {
            when (item.itemType) {
                1 -> {
                    itemView.setOnClickListener { Log.d(TAG, "item click") }
                }
                else -> {// head
                    itemView.apply {
                        tabLayout.removeOnTabSelectedListener(listener)
                        tabLayout.addOnTabSelectedListener(listener)
                    }
                }
            }
        }
    }

    val listener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            Log.i(TAG, "head  ,tab position ${p0?.position}")
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