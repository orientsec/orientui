# OrientUI
东方证券Android的UI组件库
## 添加仓库
```html
allprojects {
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```
## 组件库
### 1. PinnedRecyclerView
#### 特性
----
支持顶部悬浮的recyclerview，主要用于行情分类列表，便于用户浏览和点击标题栏
#### 如何使用
----
- 添加下述依赖：
```api
api 'com.github.Orientsec.OrientUI : PinnedRecyclerView:1.0.0'
```
- 加入布局
在布局的xml中加入：
```
<com.mobile.orientui.PinnedHeaderRecyclerView
	app:isRetractable="false"//判断是否支持点击收放列表
    .../>
```
- 代码继承
```
class PinnedAdapter : RecyclerView.Adapter<PinnedAdapter.ViewHolder>(), PinnedHeaderCallBack {
		//根据viewType类型判断是否为headItem，是顶部item悬浮显示
		override fun isPinnedViewType(viewType: Int): Boolean {}

		override fun isPlateViewType(viewType: Int): Boolean = false
}
```

### 2. RankingGroup
支持列表的上下左右滑动，主要用于行情综合列表展示
#### 特性
----
1.上拉刷新
2.实时更新数据

#### 如何使用
----
- 添加下述依赖：
```
api 'com.github.Orientsec.OrientUI: RankingGroup:1.0.0'
```
- 加入布局
在布局的xml中加入：
```
<com.mobile.orientui.RankingGroupView
	app:maxRefreshCount="20"//根据后端的承载能力设置刷新数量
	app:cacheCount="1"//缓存
    .../>
```
- 代码
1、自定义左右两边的adapter，数据类型为HEAD_ITEM、NORMAL_ITEM和NO_DATA_ITEM
```
    private lateinit var mLeftAdapter: RankingBaseAdapter<*>

	class LeftAdapter : RankingBaseAdapter<LeftAdapter.ViewHolder>() {}

    private lateinit var mRightAdapter: RankingBaseAdapter<*>

	class RightAdapter : RankingBaseAdapter<RightAdapter.ViewHolder>() {}
```
2、继承回调
```
	//监听滑动事件
	private val scrollCallback = object : RankingGroupView.OnScrollCallback {
	........
	}
```
3、初始化rankinggroup
```
	//初始化view
    private fun initView(view: View) {
        view.ranking_gv.apply {
            setAdapter(mLeftAdapter, mRightAdapter)
            addItemDecoration(...)
            mScrollCallback = scrollCallback
        }
    }
```

