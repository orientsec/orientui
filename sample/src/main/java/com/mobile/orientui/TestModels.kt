package com.mobile.orientui


const val HEAD = 0
const val Body = 1

open class TestItem(val category: Int)

data class HeadItem(val title:String) : RankingBaseItemModel(HEAD_ITEM)

class BodyItem : RankingBaseItemModel(NORMAL_ITEM)
