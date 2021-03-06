package com.guuguo.gank.view

/**
 * 基础View接口
 * Created by panl on 15/12/24.
 */
interface IBaseView {
    fun initIView()
    fun showProgress()
    fun hideProgress()
    fun showErrorView(e:Throwable)
    fun showTip(msg: String) 
}
