package com.mobile.orientui.theme

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.R
import com.mobile.orientui.divider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.ranking_group_left_head_item.view.*
import kotlinx.android.synthetic.main.test_theme_activity_layout.*

/**
 *
 * @PackageName com.mobile.orientui.theme
 * @date 2019/9/26 16:16
 * @author songdongqi
 */
class TestThemeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_theme_activity_layout)

        val mAdapter = TestAdapter()
        mAdapter.onItemClickListener = {
            AlertDialog.Builder(this)
                    .setMessage("xxxxxxxx")
                    .show()
        }
        mAdapter.dataList= listOf(1,2)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@TestThemeActivity)
            addItemDecoration(HorizontalDividerItemDecoration.Builder(this@TestThemeActivity)
                    .color(Color.parseColor("#cccccc"))
                    .size(1)
                    .build())
            adapter = mAdapter
        }
    }

}

class TestAdapter : RecyclerView.Adapter<TestAdapter.ItemViewHolder>() {
    var dataList = listOf<Int>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onItemClickListener: (Int) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_group_left_head_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: TestAdapter.ItemViewHolder, position: Int) {
        holder.bindView(dataList[position])
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(text: Int) {
            itemView.tv.text = text.toString()
            itemView.setOnClickListener { onItemClickListener(text) }
        }
    }
}
