package com.mobile.orientui.compositercv

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import org.jetbrains.anko.dip


/**
 *
 * @PackageName com.mobile.orientui.compositercv
 * @date 2019/11/1 9:12
 * @author songdongqi
 */
class ItemRightLLayout : LinearLayout {

    /**
     * 首列的宽度
     * 用于计算child的宽度
     */
    private var firstItemWidth: Int = dip(120)
    /**
     * child按照几等分显示
     */
    private var displayCount: Int = 3
    /**
     * child的宽度
     */
    private var itemWidth: Int = (getPhoneWidth() - firstItemWidth) / displayCount

    private var mTextSize: Float = 0f

    private var mTextColor: Int = 0

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
        displayCount = a.getInt(R.styleable.ItemRightLLayout_displayCount, 3)
        itemWidth = (getPhoneWidth() - firstItemWidth) / displayCount
        mTextSize = a.getFloat(R.styleable.ItemRightLLayout_textSize, 19f)
        mTextColor = a.getColor(R.styleable.ItemRightLLayout_textColor, Color.parseColor("#777777"))
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

    fun addHeadItem(index: Int, value: String = " ") {
        val tv = TextView(context).apply {
            textSize = mTextSize
            setTextColor(mTextColor)
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
    fun addItemTextView(value: String = " ", minTextSize: Int = 10, maxTextSize: Int = 19) {
        val tv = AppCompatTextView(context).apply {
            textSize = mTextSize
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