package com.keplersegg.myself.adapters

import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.keplersegg.myself.R

class AppTutorialAdapter : PagerAdapter() {

    val mSize = 4

    override fun getCount(): Int {
        return mSize
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(view: ViewGroup, position: Int, `object`: Any) {
        view.removeView(`object` as View)
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val viewAppTutorial = LayoutInflater.from(view.context).inflate(R.layout.view_app_tutorial, view, false)

        setContent(viewAppTutorial, position)

        view.addView(viewAppTutorial)
        return viewAppTutorial
    }

    private fun setContent(viewAppTutorial: View, position: Int) {

        val imgTutorial = viewAppTutorial.findViewById<ImageView>(R.id.imgTutorial)
        val lblTutorialHeader = viewAppTutorial.findViewById<TextView>(R.id.lblTutorialHeader)
        val lblTutorial = viewAppTutorial.findViewById<TextView>(R.id.lblTutorial)

        when (position) {
            0 -> {

                imgTutorial.setImageResource(R.drawable.ic_logo)
                lblTutorialHeader.setText(R.string.tutorial_1)
                lblTutorial.setText(R.string.tutorial_1_desc)
            }
            1 -> {

                imgTutorial.setImageResource(R.drawable.ic_startup)
                lblTutorialHeader.setText(R.string.tutorial_2)
                lblTutorial.setText(R.string.tutorial_2_desc)
            }
            2 -> {

                imgTutorial.setImageResource(R.drawable.ic_target)
                lblTutorialHeader.setText(R.string.tutorial_3)
                lblTutorial.setText(R.string.tutorial_3_desc)
            }
            3 -> {

                imgTutorial.setImageResource(R.drawable.ic_laptop)
                lblTutorialHeader.setText(R.string.tutorial_4)
                lblTutorial.setText(R.string.tutorial_4_desc)
            }
            else -> { }
        }
    }
}