package com.keplersegg.myself.fragments

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.keplersegg.myself.R
import com.keplersegg.myself.models.BadgeLevel
import kotlinx.android.synthetic.main.dialog_new_badge.*


class DialogNewBadgeFragment : DialogFragment() {

    var badgeId: Int = 0
    var imageResourceId: Int = 0
    var level: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_new_badge, container, false)
    }

    override fun onResume() {

        super.onResume()
        val params = this.dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT
        this.dialog!!.window!!.attributes = params

        setContent()
    }

    private fun setContent() {

        imgBadge.setImageResource(imageResourceId)

        val levelInfo = BadgeLevel.GetBadgeLevels(dialog!!.context).firstOrNull { b -> b.BadgeId == badgeId && b.Level == level }

        if (levelInfo != null) {

            lblNameCurrentLevel.text = levelInfo.Name
        }
    }
}