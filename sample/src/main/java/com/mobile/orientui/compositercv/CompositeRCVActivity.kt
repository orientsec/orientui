package com.mobile.orientui.compositercv

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.R
import com.mobile.orientui.divider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.composite_rcv_activity.*

/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/10/30 10:14
 * @author songdongqi
 */
class CompositeRCVActivity : AppCompatActivity() {
    private lateinit var mAdapter: CompositeRCVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.composite_rcv_activity)
        mAdapter = CompositeRCVAdapter(10)
        rcv_view.apply {
            adapter = mAdapter
            layoutManager = QuoteLayoutManager.Builder()
                    .setColumnCount(2)
                    .build()
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(HorizontalDividerItemDecoration.Builder(context)
                    .margin(15, 15)
                    .size(1)
                    .color(Color.parseColor("#c4c4c4"))
                    .build())
            addOnScrollListener(scrollListener)
        }

        val list = listOf(CompositeBaseItemModel(HEAD_ITEM_LEFT),
                CompositeBaseItemModel(HEAD_ITEM_RIGHT)).toMutableList()
        for (i in 0 until 15) {
            list.add(CompositeBaseItemModel(NORMAL_ITEM_LEFT))
            list.add(CompositeBaseItemModel(NORMAL_ITEM_RIGHT))
        }
        list.add(CompositeBaseItemModel(BOTTOM_ITEM))
        mAdapter.submitList(list)
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val firstVisibleItem = (recyclerView.layoutManager as QuoteLayoutManager).getDisplayRowStart()
            val lastVisibleItem = (recyclerView.layoutManager as QuoteLayoutManager).getDisplayRowEnd()
            Log.v("scrollListener","firstVisibleItem:$firstVisibleItem   lastVisibleItem:$lastVisibleItem")
        }
    }
}

