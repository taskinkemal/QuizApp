package com.keplersegg.myself.fragments


import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Bundle

import com.keplersegg.myself.R
import com.keplersegg.myself.models.ListItem
import kotlinx.android.synthetic.main.fragment_app_usage.*
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import com.keplersegg.myself.helper.AutoTaskType
import org.jetbrains.anko.doAsync

class AppUsageFragment : ListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.layout = R.layout.fragment_app_usage
    }

    override fun showImages(): Boolean { return true }

    override fun getRecyclerView(): RecyclerView { return rcylApps }

    override fun getList(): List<ListItem> {

        val list: ArrayList<ListItem> = ArrayList()

        val pm = activity.getPackageManager()
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        packages.forEach { appInfo ->

            if (appInfo.sourceDir.startsWith("/data/app/") && pm.getLaunchIntentForPackage(appInfo.packageName) != null) {

                list.add(toListItem(pm, appInfo))
            }
        }

        list.sortBy { item -> item.Label.toLowerCase() }
        return list
    }

    override fun onResume() {
        super.onResume()
        SetTitle(R.string.autotask_appUsage)
    }

    override fun onSelectListItem(item: ListItem) {

        activity.NavigateFragment(true, AddTaskFragment.newInstance(AutoTaskType.AppUsage, item))
    }

    companion object {

        fun newInstance(): AppUsageFragment {
            return AppUsageFragment()
        }

        fun getItemById(context: Context, id: String): ListItem? {

            val pm = context.getPackageManager()
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val appInfo = packages.firstOrNull { p -> p.packageName.hashCode().toString() == id }

            if (appInfo != null)
                return toListItem(pm, appInfo)
            else
                return null
        }

        private fun toListItem(pm: PackageManager, appInfo: ApplicationInfo): ListItem {

            val item = ListItem()
            item.ItemId = appInfo.packageName
            item.ImageDrawable = pm.getApplicationIcon(appInfo)
            item.Label = pm.getApplicationLabel(appInfo).toString().trim()

            return item
        }

        private var appIcon : Drawable? = null

        fun getApplicationIcon(context: Context, id: String) : Drawable? {

            if (appIcon != null) return appIcon
            doAsync {
                val pm = context.getPackageManager()
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                val appInfo = packages.firstOrNull { p -> p.packageName.hashCode().toString() == id }

                if (appInfo != null) {
                    appIcon = pm.getApplicationIcon(appInfo)
                }
            }

            return appIcon
        }
    }
}
