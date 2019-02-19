package com.mobile.orientui.pinnedrecyclerview


const val HEAD = 0
const val Body = 1

open class TestItem(val category: Int)

data class HeadItem(val title:String) : TestItem(HEAD)

class BodyItem : TestItem(Body)