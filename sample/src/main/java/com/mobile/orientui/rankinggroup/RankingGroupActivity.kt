package com.mobile.orientui.rankinggroup

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.*
import com.mobile.orientui.R
import com.mobile.orientui.divider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.randking_group_activity.*

class RankingGroupActivity : AppCompatActivity() {
    private lateinit var mLeftAdapter: RankingLeftAdapter
    private lateinit var mRightAdapter: RankingRightAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.randking_group_activity)

        initAdapter()

        initView()

        initData()
    }

    private fun initAdapter() {
        mLeftAdapter = RankingLeftAdapter()
        mRightAdapter = RankingRightAdapter()
    }

    private fun initView() {
        rankingGV.apply {
            setAdapter(mLeftAdapter, mRightAdapter)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                    .margin(15, 15)
                    .size(1)
                    .color(Color.parseColor("#c4c4c4"))
                    .build())
            mScrollCallback = scrollCallback
        }
    }

    private fun initData() {
        var list = listOf<RankingBaseItemModel>(HeadItem())

        for (i in 0 until 50) {
            list += listOf(BodyItem())
        }

        val endList = list
        mLeftAdapter.setupItemList(endList)
        mRightAdapter.setupItemList(endList)
    }

    private val scrollCallback = object : RankingGroupView.OnScrollCallback {
        override fun startPollList(firstVisibleItem: Int, visibleItemCount: Int) {

        }

        override fun stopPollList() {

        }

        override fun loadMoreList() {

        }
    }
}

class RankingLeftAdapter : RankingBaseAdapter<RankingLeftAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: RankingLeftAdapter.ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingLeftAdapter.ViewHolder {

        val view = when (viewType) {
            HEAD_ITEM -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_group_left_head_item, parent, false)
            NORMAL_ITEM -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_group_left_body_item, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_group_empty_layout, parent, false)
        }
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bindView(item: RankingBaseItemModel) {

        }
    }
}

class RankingRightAdapter : RankingBaseAdapter<RankingRightAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: RankingRightAdapter.ViewHolder, position: Int) {
        holder.bindView(itemList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingRightAdapter.ViewHolder {

        val view = when (viewType) {
            HEAD_ITEM -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_right_head_global, parent, false) as LinearLayout
            NORMAL_ITEM -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_right_content_global, parent, false) as LinearLayout
            else -> LayoutInflater.from(parent.context).inflate(R.layout.ranking_group_empty_layout, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal fun bindView(item: RankingBaseItemModel) {

        }
    }

}