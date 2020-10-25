package com.keplersegg.myself.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.keplersegg.myself.R
import androidx.core.view.GravityCompat
import com.google.android.material.snackbar.Snackbar
import com.keplersegg.myself.MySelfApplication
import com.keplersegg.myself.fragments.*
import com.keplersegg.myself.helper.AutoTaskType
import com.keplersegg.myself.interfaces.ISyncTasksHost
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : MasterActivity(), ISyncTasksHost {

    override fun GetApplication(): MySelfApplication {
        return app
    }

    override fun onSyncTasksSuccess(missingPermissions: List<AutoTaskType>) {

        requestPermissions(missingPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigationView()

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {

            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24px)
        }
    }

    private fun setupNavigationView() {

        navigation.setNavigationItemSelectedListener { menuItem ->

            menuItem.isChecked = true

            lytDrawerHome.closeDrawers()

            navigateFromMenu(menuItem.itemId)

            true
        }
    }

    override fun onResume() {
        super.onResume()

        val imgUserPicture = navigation.getHeaderView(0).findViewById<ImageView>(R.id.imgUserPicture)
        val lblNavUserName = navigation.getHeaderView(0).findViewById<TextView>(R.id.lblNavUserName)

        lblNavUserName.text = if (app.user != null) app.user!!.FirstName + " " + app.user!!.LastName else "Guest"

        if (app.user != null && app.user!!.PictureUrl != null && app.user!!.PictureUrl!!.isNotEmpty()) {

            Glide.with(this)
                    .load(app.user!!.PictureUrl)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.ic_baseline_account_circle_24px)
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUserPicture)
        } else {

            Glide.with(this)
                    .load(R.drawable.ic_baseline_account_circle_24px)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imgUserPicture)
        }

        showNewBadgeDialog()
    }

    private fun navigateFromMenu(menuItemID: Int) {

        when (menuItemID) {

            R.id.action_tasks ->
                NavigateFragment(true, TasksPagerFragment.newInstance())
            R.id.action_profile ->
                NavigateFragment(true, ProfileFragment.newInstance())
            R.id.action_add_task ->
                NavigateFragment(true, AddTaskFragment.newInstance(-1))
            R.id.action_add_automated_task ->
                NavigateFragment(true, AutoTaskSelectorFragment.newInstance())
            R.id.action_add_goal ->
                NavigateFragment(true, AddGoalFragment.newInstance(-1, -1))
            R.id.action_goals ->
                NavigateFragment(true, GoalsFragment.newInstance())
            R.id.action_statistics ->
                NavigateFragment(true, StatsMainFragment.newInstance())
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            lytDrawerHome.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(menuItem)
    }

    fun NavigateFragment(addToBackStack: Boolean, fragment: MasterFragment) {

        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
                R.animator.fragment_enter,
                R.animator.fragment_exit,
                R.animator.fragment_pop_enter,
                R.animator.fragment_pop_exit)

        transaction.replace(R.id.fragment_frame, fragment)

        if (addToBackStack)
            transaction.addToBackStack(null)

        transaction.commit()
    }

    fun showSnackbarMessage(message: String) {

        val snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG)

        snackbar.show()
    }

    fun showNewBadgeDialog() {

        val newBadgeId = app.dataStore.popNewBadge()

        if (newBadgeId != null) {

            doAsync {

                val badge = AppDB().userBadgeDao()[newBadgeId]
                val imageResourceId =
                        when (newBadgeId) {
                            1 -> R.drawable.ic_startup
                            2 -> R.drawable.ic_flag
                            else -> R.drawable.ic_trophy
                        }

                uiThread {

                    if (badge != null) {
                        val dialog = DialogNewBadgeFragment()
                        dialog.badgeId = newBadgeId
                        dialog.imageResourceId = imageResourceId
                        dialog.level = badge.Level

                        dialog.show(supportFragmentManager, "")
                    }
                }
            }
        }
    }

    private fun navigateToCallDuration() {
        NavigateFragment(true, AddTaskFragment.newInstance(AutoTaskType.CallDuration, null))
    }

    private fun navigateToAppUsage() {
        NavigateFragment(true, AppUsageFragment.newInstance())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                when (requestCode) {
                    1 -> {
                        navigateToCallDuration()
                    }
                    2 -> {
                        navigateToAppUsage()
                    }
                    else -> {

                    }
                }
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                when (requestCode) {
                    1 -> {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                            showErrorMessage(getString(R.string.permission_error_phoneCalls))
                        }
                        else {
                            //Never ask again selected, or device policy prohibits the app from having that permission.
                            //TODO: improvement : completely disabling the feature.
                        }
                    }
                    2 -> {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PACKAGE_USAGE_STATS)) {
                            showErrorMessage(getString(R.string.permission_error_apps))
                        }
                        else {
                            //Never ask again selected, or device policy prohibits the app from having that permission.
                            //TODO: improvement : completely disabling the feature.
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}
