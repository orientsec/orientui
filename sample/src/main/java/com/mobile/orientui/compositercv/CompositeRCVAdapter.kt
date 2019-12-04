package com.mobile.orientui.compositercv

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.R
import kotlinx.android.synthetic.main.optional_rcv_head_item_right.view.*
import kotlinx.android.synthetic.main.optional_rcv_normal_item_right.view.*

/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/11/1 9:08
 * @author songdongqi
 */
class CompositeRCVAdapter(private val itemSize: Int) : ListAdapter<CompositeBaseItemModel, CompositeRCVAdapter.ViewHolder>(Diff()) {

    override fun getItemViewType(position: Int): Int = getItem(position).itemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            HEAD_ITEM_RIGHT -> {
                val headView = LayoutInflater.from(parent.context).inflate(R.layout.optional_rcv_head_item_right, parent, false)
                for (i in 0 until itemSize) headView.ll_head_item_right.addHeadItem(i,"总市值")
                headView
            }
            HEAD_ITEM_LEFT -> LayoutInflater.from(parent.context).inflate(R.layout.optional_rcv_head_item_left, parent, false)
            NORMAL_ITEM_LEFT -> LayoutInflater.from(parent.context).inflate(R.layout.optional_rcv_noraml_item_left, parent, false)
            NORMAL_ITEM_RIGHT -> {
                val normalView = LayoutInflater.from(parent.context).inflate(R.layout.optional_rcv_normal_item_right, parent, false)
                for (i in 0 until itemSize) normalView.ll_normal_item_right.addItemTextView("1000")
                normalView
            }
            BOTTOM_ITEM -> LayoutInflater.from(parent.context).inflate(R.layout.composite_rcv_bottom_item, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.composite_rcv_nodata_item, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("ClickableViewAccessibility")
        fun bindView(model: CompositeBaseItemModel) {
            when (model.itemType) {
                HEAD_ITEM_LEFT -> {

                }
                HEAD_ITEM_RIGHT -> {

                }
                NORMAL_ITEM_LEFT -> {

                }
                NORMAL_ITEM_RIGHT -> {

                }
            }
        }
    }

    class Diff : DiffUtil.ItemCallback<CompositeBaseItemModel>() {
        override fun areItemsTheSame(oldItem: CompositeBaseItemModel, newItem: CompositeBaseItemModel): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CompositeBaseItemModel, newItem: CompositeBaseItemModel): Boolean {
            return oldItem.equals(newItem)
        }
    }
}