package com.hisavana.ssp.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.Data
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.hisavana.common.bean.TAdErrorCode
import com.hisavana.common.bean.TAdNativeInfo
import com.hisavana.common.bean.TAdRequestBody
import com.hisavana.common.constant.NativeContextMode
import com.hisavana.common.interfacz.TAdListener
import com.hisavana.mediation.ad.TAdNativeView
import com.hisavana.mediation.ad.TNativeAd
import com.hisavana.mediation.ad.ViewBinder
import com.hisavana.ssp.R
import com.hisavana.ssp.ui.HotAppActivity.HotHolder
import com.hisavana.ssp.util.DemoConstants
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * Created  ON 2023/12/13
 * @author :fangxuhui
 */
class IconAdActivity : BaseActivity(), OnRefreshLoadMoreListener {

    lateinit var smartRefreshLayout: SmartRefreshLayout
    lateinit var iconListView: RecyclerView
    lateinit var mAdapter: IconAdapter
    val data = mutableListOf<DataBean>()
    val adList = mutableListOf<AdRequest>()
    val mHandler = Handler(Looper.getMainLooper())
    var page = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle("Native Icon");
        setContentView(R.layout.activity_icon)
        smartRefreshLayout = findViewById(R.id.icon_refresh)
        iconListView = findViewById(R.id.icon_list_view)
        iconListView.layoutManager = LinearLayoutManager(this)
        mAdapter = IconAdapter(data)
        iconListView.adapter = mAdapter
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
        smartRefreshLayout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 0

        adList.forEach {
            it.destroy()
        }
        adList.clear()
        data.forEach {
            it.nativeInfo?.destroyAd()
        }
        data.clear()
        mHandler.postDelayed({
            getItemData()
            smartRefreshLayout.finishRefresh()
        }, 200)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        mHandler.postDelayed({
            getItemData()
            mAdapter.notifyDataSetChanged()
            smartRefreshLayout.finishLoadMore()
        }, 200)
    }

    fun getItemData() {
        val position = data.size
        // 媒体数据模拟
        getData(data.size)
        //获取广告数据
        val adRequest = AdRequest(this, data.size )
        val ads = adRequest.loadAd()
        ads.forEach {
            data.add(DataBean(null, null, adRequest.tNativeAd, it, adRequest.sceneToken, 1))
        }
        val count = data.size - position
        if(position == 0){
            mAdapter.notifyDataSetChanged()
        }else{
            mAdapter.notifyItemRangeInserted(position, count)
        }

    }

    fun getData(startPosition: Int) {
        for (i in startPosition until startPosition + 10) {
            data.add(DataBean("$i ", "normal item", null, null, null, 0))
        }
    }

  fun setAdData(ads:List<TAdNativeInfo>, tNativeAd: TNativeAd,position: Int){
      if (ads.isEmpty()){
          return
      }
      val d = mutableListOf<DataBean>()
      ads.forEach {
          d.add(DataBean(null, null, tNativeAd, it, null, 1))
      }
      if(data.size<=position){
          val start = data.size
          data.addAll(d)
          mAdapter.notifyItemRangeChanged(start,d.size)
      }else{
          data.addAll(position,d)
          mAdapter.notifyItemRangeChanged(position,d.size)
      }
  }


    inner class IconAdapter(data: MutableList<DataBean>) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return when (viewType) {
                0 -> NormalHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_normal, parent, false))
                1 -> AdHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_ad, parent, false))
                else -> NormalHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_icon_normal, parent, false))
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (holder) {
                is NormalHolder -> {
                    holder.title.text = data[position].title
                    holder.des.text = data[position].content
                }
                is AdHolder -> {
                    holder.bindAd(data[position])
                }
            }

        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            if (data.size > position) {
                return data[position].itemType
            }
            return 0
        }

    }

    inner class NormalHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById<TextView>(R.id.title)
        val des: TextView = itemView.findViewById<TextView>(R.id.des)
    }

    inner class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tAdNativeView = itemView.findViewById<TAdNativeView>(R.id.native_view)

        fun bindAd(ad: DataBean) {
            val viewBinder = ViewBinder.Builder(R.layout.layout_icon_ad)
                .iconId(R.id.icon).titleId(R.id.title).descriptionId(R.id.des).contextMode(NativeContextMode.LIST).build()
            ad.nativeInfo?.let {
                ad.tNativeAd?.bindNativeView(tAdNativeView, it, viewBinder, ad.sceneToken)
            }
        }
    }

    inner class AdRequest(context: Context, position: Int) {
        val tNativeAd by lazy {
            TNativeAd(context, DemoConstants.TEST_SLOT_ID_ICON)
        }

        var sceneToken: String? = null

        val adListener = object : TAdListener() {

            override fun onLoad() {
               setAdData( tNativeAd.nativeAdInfo, tNativeAd, position)
            }

            override fun onError(p0: TAdErrorCode?) {
            }

            override fun onShow(p0: Int) {
            }

            override fun onClicked(p0: Int) {
            }

            override fun onNativeFeedClicked(p0: Int, p1: TAdNativeInfo?) {

            }

            override fun onNativeFeedShow(p0: Int, p1: TAdNativeInfo?) {

            }

            override fun onClosed(p0: Int) {
            }

        }

        init {
            tNativeAd.setRequestBody(TAdRequestBody.AdRequestBodyBuild()
                .setAdListener(adListener)
                .build())
        }

        fun loadAd(): MutableList<TAdNativeInfo> {
            if (TextUtils.isEmpty(sceneToken)) {
                sceneToken = tNativeAd.enterScene("icon_ad_scene_id")
            }
            var ads: MutableList<TAdNativeInfo> = tNativeAd.nativeAdInfo
            if (ads.isEmpty()) {
                tNativeAd.loadAd()
            }
            return ads
        }

        fun destroy() {
            tNativeAd.destroy()
        }
    }


    data class DataBean(
        var title: String?,
        var content: String?,
        var tNativeAd: TNativeAd?,
        var nativeInfo: TAdNativeInfo?,
        var sceneToken: String?,
        var itemType: Int = 0
    )
}