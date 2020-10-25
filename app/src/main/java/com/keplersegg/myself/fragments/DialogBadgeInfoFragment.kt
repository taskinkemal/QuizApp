package com.keplersegg.myself.fragments

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.keplersegg.myself.R
import com.keplersegg.myself.models.BadgeLevel
import kotlinx.android.synthetic.main.dialog_badge_info.*


class DialogBadgeInfoFragment : DialogFragment() {

    var badgeId: Int = 0
    var imageResourceId: Int = 0
    var level: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.dialog_badge_info, container, false)
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

        val badgeLevels = BadgeLevel.GetBadgeLevels(dialog!!.context).filter { b -> b.BadgeId == badgeId }

        val currentLevel = badgeLevels.firstOrNull { b -> b.Level == level }
        val nextLevel = badgeLevels.firstOrNull { b -> b.Level == level + 1 }

        if (currentLevel != null) {

            lblNameCurrentLevel.text = currentLevel.Name
        }
        else {

            lblNameCurrentLevel.text = ""
        }

        if (nextLevel != null) {

            lytNextLevel.visibility = View.VISIBLE
            lblNameNextLevel.text = nextLevel.Name
            lblDescNextLevel.text = nextLevel.Description
        }
        else {

            lytNextLevel.visibility = View.GONE
            lblDescNextLevel.setText(R.string.lblBadge_completed)
        }


    }
}