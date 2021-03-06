package com.guuguo.gank.ui.gank.adapter

import android.view.View
import android.widget.ImageView
import com.guuguo.gank.R

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guuguo.android.lib.extension.getTimeSpan
import com.guuguo.android.lib.extension.safe
import com.guuguo.gank.model.GankSection


class GankWithCategoryAdapter : BaseSectionQuickAdapter<GankSection, BaseViewHolder> {

    override fun convertHeader(helper: BaseViewHolder, item: GankSection?) {
        helper?.setText(R.id.tv_category, item?.header)
    }

    constructor() : super(R.layout.item_gank, R.layout.item_gank_category, null)

    constructor(data: MutableList<GankSection>?) : super(
        R.layout.item_gank,
        R.layout.item_gank_category,
        data
    )

    override fun convert(holder: BaseViewHolder, gankBean: GankSection?) {
        if (gankBean?.t == null) return
        holder.setText(R.id.tv_content, gankBean.t!!.desc)
            .setText(R.id.tv_hint, gankBean.t!!.who + " · " + gankBean.t!!.publishedAt?.getTimeSpan())
        val image = holder.getView<ImageView>(R.id.iv_image)
        if (gankBean.t!!.images.safe().isNotEmpty()) {
            image.visibility = View.VISIBLE
            Glide.with(context).asBitmap().load(gankBean.t!!.images.safe()[0])
                .apply(RequestOptions().centerCrop()).into(image)
        } else {
            image.visibility = View.GONE
        }
//        var res = R.drawable.ic_other
//        when (gankBean.t.type) {
//            "iOS" -> res = R.drawable.ic_ios
//            "Android" -> res = R.drawable.ic_android
//            "前端" -> res = R.drawable.ic_web
//            "瞎推荐" -> res = R.drawable.ic_other
//            "休息视频" -> res = R.drawable.ic_relax
//            "拓展资源" -> res = R.drawable.ic_extension
//            "APP" -> res = R.drawable.ic_app
//        }
//        val drawable = holder.getConvertView().context
//                .resources.getDrawable(res)
        /// 这一步必须要做,否则不会显示.  
//        drawable.setBounds(0, 0, 20.dpToPx(), 20.dpToPx())
//        holder.getView<TextView>(R.id.tv_content).setCompoundDrawables(drawable, null, null, null)
    }


}

