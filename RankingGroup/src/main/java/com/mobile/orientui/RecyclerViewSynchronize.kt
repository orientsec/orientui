package com.mobile.orientui

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSynchronize {
    private lateinit var firstListener: RecyclerView.OnScrollListener
    private lateinit var secondListener: RecyclerView.OnScrollListener
    private lateinit var firstRecyclerView: RecyclerView
    private lateinit var secondRecyclerView: RecyclerView

    fun attach(firstRecyclerView: RecyclerView, secondRecyclerView: RecyclerView, scrollListener: RecyclerView.OnScrollListener? = null) {
        this.firstRecyclerView = firstRecyclerView
        this.secondRecyclerView = secondRecyclerView

        this.firstListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    secondRecyclerView.removeOnScrollListener(secondListener)
                    scrollListener?.onScrolled(recyclerView, dx, dy)
                    secondRecyclerView.scrollBy(dx, dy)
                    secondRecyclerView.addOnScrollListener(secondListener)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollListener?.onScrollStateChanged(recyclerView, newState)
            }
        }
        firstRecyclerView.addOnScrollListener(firstListener)

        this.secondListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE) {
                    firstRecyclerView.removeOnScrollListener(firstListener)
                    scrollListener?.onScrolled(recyclerView, dx, dy)
                    firstRecyclerView.scrollBy(dx, dy)
                    firstRecyclerView.addOnScrollListener(firstListener)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollListener?.onScrollStateChanged(recyclerView, newState)
            }
        }
        secondRecyclerView.addOnScrollListener(secondListener)
    }


}