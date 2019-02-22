package com.mobile.orientui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.orientui.rankinggroup.R
import kotlinx.android.synthetic.main.ranking_group_layout.view.*

class RankingGroupView : FrameLayout {

    lateinit var mScrollCallback: OnScrollCallback
    /**
     * recyclerview 刷新的起始位置，绝对位置值
     * 初始值为1
     */
    private var startPosition: Int = 1
    private var previousTotal: Int = 0

    private var isLoadingNewItem: Boolean = false

    companion object {
        const val MAX_REFRESH_COUNT = 20
        const val CACHE_COUNT = 1
    }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : this(context, attrs, defStyle, 0)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int, defStyleRes: Int) : super(context, attrs, defStyle, defStyleRes) {
        initAttrs(context, attrs, defStyle)
        initView(context)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RankingGroupView, defStyle, 0)

        a.recycle()
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.ranking_group_layout, this)
        val horizontalViewSynchronize = RecyclerViewSynchronize()
        horizontalViewSynchronize.attach(recycler_view_left, recycler_view_right, scrollListener)
    }

    fun setAdapter(leftAdapter: RecyclerView.Adapter<*>, rightAdapter: RecyclerView.Adapter<*>) {
        recycler_view_left.apply {
            adapter = leftAdapter
            layoutManager = LinearLayoutManager(context)
        }
        recycler_view_right.apply {
            adapter = rightAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun addItemDecoration(decor: RecyclerView.ItemDecoration){
        recycler_view_left.apply {
            addItemDecoration(decor)
        }
        recycler_view_right.apply {
            addItemDecoration(decor)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        private var loading = true
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val visibleItemCount = lastVisibleItem - firstVisibleItem + 1
            when (newState) {
                0 -> {
                    if (!isLoadingNewItem) {
                        /*resetDataList()
                        startPoll(firstVisibleItem, visibleItemCount)*/
                        if (::mScrollCallback.isInitialized) mScrollCallback.startPollList(firstVisibleItem, visibleItemCount)
                    }
                }
                else -> /*stopPoll()*/ {
                    if (::mScrollCallback.isInitialized) mScrollCallback.stopPollList()
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dx == 0 && dy == 0) return
            val totalItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
            val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val visibleItemCount = lastVisibleItem - firstVisibleItem + 1
            if (totalItemCount < MAX_REFRESH_COUNT) return

            //上拉逻辑
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false
                    previousTotal = totalItemCount
                }
            } else if (totalItemCount - visibleItemCount <= firstVisibleItem) {
                isLoadingNewItem = true
                /*loadListData(totalItemCount - 1)*/
                if (::mScrollCallback.isInitialized) mScrollCallback.loadMoreList()
                loading = true
            }

            //滑动过程中，上拉刷新
            if (dy > 0 && firstVisibleItem - startPosition >= MAX_REFRESH_COUNT - visibleItemCount) {
                startPosition = firstVisibleItem - CACHE_COUNT
            }

            //滑动过程中，下拉刷新
            if (dy < 0 && firstVisibleItem <= startPosition && startPosition > 1) {
                val position = firstVisibleItem - (MAX_REFRESH_COUNT - CACHE_COUNT - visibleItemCount)
                startPosition = if (position <= 0) 1 else position
            }
        }
    }

    interface OnScrollCallback {
        fun startPollList(firstVisibleItem: Int, visibleItemCount: Int)

        fun stopPollList()

        fun loadMoreList()
    }
}
