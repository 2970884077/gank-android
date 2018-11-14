package com.guuguo.gank.app.gank.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import com.guuguo.android.lib.extension.hideKeyboard
import com.guuguo.android.lib.extension.safe
import com.guuguo.android.lib.extension.showKeyboard
import com.guuguo.android.lib.widget.simpleview.SimpleViewHelper
import com.guuguo.gank.R
import com.guuguo.gank.app.gank.activity.WebViewActivity
import com.guuguo.gank.app.gank.adapter.GankAdapter
import com.guuguo.gank.app.gank.viewmodel.SearchViewModel
import com.guuguo.gank.base.BaseListFragment
import com.guuguo.gank.databinding.FragmentSearchBinding
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.toolbar_search.*


class SearchFragment : BaseListFragment<FragmentSearchBinding>() {
    override fun getToolBar() = id_tool_bar
    val viewModel by lazy { SearchViewModel() }

    var page = 1
    val mSearchResultAdapter by lazy {
        GankAdapter()
    }

    override fun isNavigationBack() = false
    override fun getLayoutResId() = R.layout.fragment_search

    var simplerViewHelper: SimpleViewHelper? = null
    override fun initViewModelCallBack() {
        super.initViewModelCallBack()
        setupBaseViewModel(viewModel)
        viewModel.ganksListLiveData.observe(this, Observer {
            it?.let {
                if (it.isRefresh()) {
                    mSearchResultAdapter.setNewData(it.list)
                } else {
                    mSearchResultAdapter.addData(it.list?.toMutableList().safe())
                }
            }
        })
        viewModel.isEmpty.observe(this, Observer {
            if (it == true)
                simplerViewHelper?.showEmpty("搜索结果为空", imgRes = R.drawable.empty_cute_girl_box)
            else {
                simplerViewHelper?.restore()
            }
        })
    }

    override fun loadingStatusChange(it: Boolean) {
        if (it.safe() && viewModel.isRefresh) {
            simplerViewHelper?.showLoading("正在加载搜索结果")
        } else if (!it.safe()) {
            simplerViewHelper?.restore()
        }
    }

    override fun initView() {
        super.initView()
        mSearchResultAdapter.setOnItemClickListener { _, _, position ->
            val bean = mSearchResultAdapter.getItem(position)!!
            WebViewActivity.intentTo(bean, activity)
        }
        simplerViewHelper = SimpleViewHelper(recycler)
        simplerViewHelper?.showEmpty("请输入搜索关键字", imgRes = R.drawable.empty_cute_girl_box)
        iv_back.setOnClickListener {
            findNavController().popBackStack()
        }
        iv_search.setOnClickListener {
            viewModel.searchText = edt_search.text.toString()
            viewModel.fetchData(true)
        }
        edt_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchText = edt_search.text.toString()
                viewModel.fetchData(true)
                true
            } else false
        }
        edt_search.showKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        edt_search?.hideKeyboard()
    }

    override fun initRecycler() {
        super.initRecycler()
        binding.recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        binding.recycler.adapter = mSearchResultAdapter
    }
}
