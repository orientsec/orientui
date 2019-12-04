package com.mobile.orientui.compositercv

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat
import org.jetbrains.anko.dip


/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/11/1 9:12
 * @author songdongqi
 */
class ItemRightLLayout : LinearLayout {

    var firstItemWidth: Int = dip(120)
        private set
    var displayCount: Int = 3
        private set
    var itemWidth: Int = (getPhoneWidth() - firstItemWidth) / displayCount
        private set

    var itemListTV = mutableListOf<TextView>()

    var onHeadItemClick: (Int) -> Unit = {}

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initAttrs(context, attrs, defStyle)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ItemRightLLayout, defStyle, 0)
        firstItemWidth = a.getDimensionPixelSize(R.styleable.ItemRightLLayout_firstItemWidth, dip(120))
        displayCount = a.getInt(R.styleable.ItemRightLLayout_displayCount, 1)
        itemWidth = (getPhoneWidth() - firstItemWidth) / displayCount
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //测量所有子控件的宽和高
        var measuredWidth = 0
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                measuredWidth += child.measuredWidth
            }
        }
        measuredWidth += paddingLeft + paddingRight
        setMeasuredDimension(measuredWidth, heightMeasureSpec)
    }

    fun addHeadItem(index: Int, value: String) {
        val tv = TextView(context).apply {
            compoundDrawablePadding = dip(5f)
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            setPadding(0, 0, dip(15f), 0)
            text = value
            setOnClickListener { onHeadItemClick(index) }
        }

        val lp = LayoutParams(itemWidth, LayoutParams.MATCH_PARENT)
        addView(tv, lp)
        itemListTV.add(tv)
    }

    @SuppressLint("InlinedApi")
    fun addItemTextView(value: String, minTextSize: Int = 10, maxTextSize: Int = 19) {
        val tv = AppCompatTextView(context).apply {
            textSize = 19f
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            maxLines = 1
            text = value
            setPadding(0, 0, dip(15f), 0)
        }
        TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(tv, minTextSize, maxTextSize, 1, TypedValue.COMPLEX_UNIT_SP)
        val lp = LayoutParams(itemWidth, LayoutParams.MATCH_PARENT)
        addView(tv, lp)
        itemListTV.add(tv)
    }

    private fun getPhoneWidth(): Int {
        return resources.displayMetrics.widthPixels
    }
}