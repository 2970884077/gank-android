package com.guuguo.gank.ui.gank.fragment

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.guuguo.android.lib.extension.dpToPx
import com.guuguo.android.lib.extension.getColorCompat
import com.guuguo.android.lib.extension.safe
import com.guuguo.android.lib.utils.DisplayUtil
import com.guuguo.gank.R
import com.guuguo.gank.ui.gank.activity.AboutActivity
import com.guuguo.baselib.mvvm.BaseFragment
import com.guuguo.gank.constant.AppLocalData
import com.guuguo.gank.databinding.FragmentHomeBinding
import com.guuguo.theme.ThemeUtils
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.*
import com.tencent.bugly.beta.Beta
import kotlinx.android.synthetic.main.base_toolbar_common.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(), Toolbar.OnMenuItemClickListener {
    override fun isNavigationBack() = false

    companion object {
        fun getInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun getHeaderTitle() = "Gank"
    override fun getMenuResId() = R.menu.main_menu
    override fun getToolBar() = contentView?.findViewById<Toolbar>(R.id.id_tool_bar)
    override fun getLayoutResId() = R.layout.fragment_home

    override fun setTitle(title: String) {
        tv_title.text = title
    }

    private var anim2: Animator? = null
    private var anim: Animator? = null
    lateinit var mNavHostFragment: NavHostFragment

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_check_up -> Beta.checkUpgrade()
            R.id.menu_search -> {
                SearchActivity.intentTo(activity, binding.toolbar.searchCard)
            }
            R.id.menu_about -> AboutActivity.intentTo(activity)
            else -> return false
        }
        return true
    }

    lateinit var drawer: Drawer

    override fun initView() {
        super.initView()
        val slideTextColor: Int = activity.getThemeRes(android.R.attr.textColorPrimary)!!
        val darkModeSetting = SwitchDrawerItem().withName("暗黑模式").withIdentifier(1).withTextColor(slideTextColor)
            .withChecked(ThemeUtils.isDark)
            .withSwitchEnabled(!ThemeUtils.darkFollowSystem)
            .withOnCheckedChangeListener { drawerItem, buttonView, isChecked ->
                ThemeUtils.changeToTheme(isChecked)
                (drawerItem as SwitchDrawerItem).withChecked(isChecked)
            }.withSelectable(false)

        val darkModeFollowSystemSetting =
            SwitchDrawerItem().withName("暗黑模式跟随系统").withIdentifier(1).withTextColor(slideTextColor)
                .withChecked(ThemeUtils.darkFollowSystem)
                .withOnCheckedChangeListener { _, _, isChecked ->
                    ThemeUtils.enableFollowSystem(isChecked)
                    darkModeSetting
                        .withChecked(ThemeUtils.isDark)
                        .withSwitchEnabled(!isChecked)
                    drawer.adapter.notifyAdapterDataSetChanged()
                }
                .withSelectable(false)

        drawer = DrawerBuilder()
            .withActivity(activity)
            .withSliderBackgroundColor(activity.getThemeRes(R.attr.cardBackgroundColor)!!)
            .withAccountHeader(
                AccountHeaderBuilder()
                    .withActivity(activity)
                    .withTextColor(slideTextColor)
                    .withSelectionListEnabled(false)
                    .addProfiles(
                        ProfileDrawerItem().withName("guuguo").withEmail("guuguo@qq.com").withIcon(R.mipmap.avatar_squre)
                    ).withPaddingBelowHeader(true)
                    .build()
            )
            .withHeaderPadding(true)
            .addDrawerItems(
                darkModeFollowSystemSetting,
                darkModeSetting,
                SecondaryDrawerItem().withName("关于").withIdentifier(2).withSelectable(false).withTextColor(
                    slideTextColor
                ).withOnDrawerItemClickListener { view, position, drawerItem ->
                    AboutActivity.intentTo(activity)
                    false
                }
            )
            .build()
        binding.toolbar.imageView2.setOnClickListener {
            drawer.openDrawer()
        }

        findNavController()
        mNavHostFragment = childFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        binding.toolbar.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(t: TabLayout.Tab?) {
                revealColor(t)
                val id = when (t?.position) {
                    2 -> R.id.dailyFragment
                    0 -> R.id.gankCategoryFragment
                    1 -> R.id.gankCategoryContentFragment
                    else -> R.id.gankCategoryFragment
                }
                onNavDestinationSelected(id, mNavHostFragment.navController, true)
            }
        })
        getToolBar()?.inflateMenu(getMenuResId())
        getToolBar()?.setOnMenuItemClickListener(this)
        binding.toolbar.tvTitle.setOnClickListener {
            SearchActivity.intentTo(activity, binding.toolbar.searchCard)
        }
        val color = getBgColor(0)
        binding.toolbar.vBarRevealColor.setBackgroundColor(color)
        activity.findViewById<View?>(R.id.systembar_statusbar_view)?.setBackgroundColor(color)
        activity.findViewById<View?>(R.id.systembar_foreground_view)?.setBackgroundColor(color)
    }

    inline fun <reified T> Context.getThemeRes(resAttr: Int): T? {
        val attrs = intArrayOf(resAttr)
        val typedArray = this.obtainStyledAttributes(attrs)
        var res: T? = null
        when (T::class) {
            Int::class -> res = typedArray.getColor(0, Color.BLACK) as T
            Boolean::class -> res = typedArray.getBoolean(0, false) as T
            Drawable::class -> res = typedArray.getDrawable(0) as T
        }
        typedArray.recycle()
        return res
    }

    /**执行 reveal 动画*/
    private fun revealColor(t: TabLayout.Tab?) {
        val color: Int = getBgColor(t?.position.safe())
        //reveal动画
        val location = IntArray(2) //view的位置
        val view = t?.view as View
        view?.getLocationInWindow(location)
        val radius = if (DisplayUtil.getScreenWidth() / 2 > location[0]) {
            DisplayUtil.getScreenWidth() - location[0]
        } else {
            location[0]
        }
        binding.toolbar.vBarRevealColor.setBackgroundColor(color)
        val sysbar = activity.findViewById<View?>(R.id.systembar_statusbar_view)
        val sysbarForeground = activity.findViewById<View?>(R.id.systembar_foreground_view)
        sysbar?.setBackgroundColor(color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            anim = ViewAnimationUtils.createCircularReveal(
                binding.toolbar.vBarRevealColor,
                location[0] + view.width.safe() / 2,
                location[1] + view.height.safe() + 20.dpToPx(),
                20.dpToPx().toFloat(),
                radius.toFloat()
            )
            anim2 = ViewAnimationUtils.createCircularReveal(
                sysbar,
                location[0] + view.width.safe() / 2,
                location[1] + view.height.safe() + 20.dpToPx(),
                20.dpToPx().toFloat(),
                radius.toFloat()
            )
            anim?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    binding.toolbar.root.setBackgroundColor(color)
                    sysbarForeground?.setBackgroundColor(color)
                }
            })
            anim?.start()
            anim2?.start()
        } else {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        anim?.cancel()
        anim2?.cancel()
    }

    private fun getBgColor(t: Int): Int {
        val color: Int = if (!ThemeUtils.isDark) when (t) {
            0 -> activity.getColorCompat(R.color.colorPrimary)
            1 -> activity.getColorCompat(R.color.color_red_ccfa3c55)
            2 -> activity.getColorCompat(R.color.colorPrimaryBlue)
            else -> activity.getColorCompat(R.color.colorPrimary)
        } else {
            activity.getThemeRes<Int>(R.attr.containerBackgroundLayer1).safe(Color.BLACK)
        }
        return color
    }

    internal fun onNavDestinationSelected(
        id: Int,
        navController: NavController,
        popUp: Boolean
    ): Boolean {
        try {
            navController.popBackStack()
            navController.navigate(id)
            return true
        } catch (var6: IllegalArgumentException) {
            return false
        }

    }
}
