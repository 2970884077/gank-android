package com.guuguo.gank.ui.gank.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.guuguo.gank.R
import com.guuguo.gank.databinding.ItemMeiziBinding
import com.guuguo.gank.model.entity.GankModel
import com.guuguo.gank.util.DisplayExtention
import com.guuguo.gank.util.dataBind

class MeiziAdapter : BaseQuickAdapter<GankModel, MeiziAdapter.ViewHolder> ,LoadMoreModule{
    constructor() : super(R.layout.item_meizi, null)

    class ViewHolder(view: View) : BaseViewHolder(view) {
        var binding: ItemMeiziBinding = view.dataBind()
    }

    val colors = arrayListOf(
        Color.parseColor("#bbdefb"), Color.parseColor("#90caf9")
        , Color.parseColor("#64b5f6"), Color.parseColor("#42a5f5"), Color.parseColor("#2196f3")
        , Color.parseColor("#1e88e5"), Color.parseColor("#1976d2"), Color.parseColor("#1565c0")
    )

    override fun convert(holder: MeiziAdapter.ViewHolder, gankBean: GankModel?) {
        if (gankBean == null) return
        holder.binding.model = gankBean
        holder.binding.executePendingBindings()
        val image = holder.getView<ImageView>(R.id.iv_image)
        holder/*.setText(R.id.date, gankBean.who + " · " + gankBean.publishedAt?.getTimeSpanUntilDay())
                .setText(R.id.tv_desc, gankBean.desc)*/
        addChildClickViewIds(R.id.iv_image)
        Glide.with(context).asBitmap()
            .load(gankBean.getWidthUrl(DisplayExtention.getScreenWidth()))
            .apply(
                RequestOptions()
                    .placeholder(ColorDrawable(colors[(Math.random() * colors.size).toInt()]))
                    .centerCrop().override(
                        ImageViewTarget.SIZE_ORIGINAL,
                        ImageViewTarget.SIZE_ORIGINAL
                    )
            )
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    gankBean.width = resource!!.width
                    gankBean.height = resource!!.height
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            })
            .into(image)

    }

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.url.hashCode().toLong()
    }

}

