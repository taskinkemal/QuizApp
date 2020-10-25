package com.keplersegg.myself.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.keplersegg.myself.R
import com.keplersegg.myself.activities.MainActivity
import com.keplersegg.myself.adapters.AppTutorialAdapter
import kotlinx.android.synthetic.main.dialog_app_tutorial.*
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


class DialogAppTutorial : DialogFragment() {

    lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_app_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AppTutorialAdapter()
        viewPager.adapter = adapter

        indicator.setViewPager(viewPager)

        adapter.registerDataSetObserver(indicator.dataSetObserver)

        lblSkip.setOnClickListener { closeTutorial() }
        lblDone.setOnClickListener { closeTutorial() }
        lblDone.visibility = View.INVISIBLE

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {

                lblSkip.visibility = if (position < adapter.mSize - 1) View.VISIBLE else View.INVISIBLE
                lblDone.visibility = if (position == adapter.mSize - 1) View.VISIBLE else View.INVISIBLE
            }
        })
    }

    private fun closeTutorial() {

        activity.app.dataStore.setTutorialDone()
        this.dismiss()
    }

    override fun onResume() {

        super.onResume()
        val params = this.dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        this.dialog!!.window!!.attributes = params
    }
}