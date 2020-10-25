package com.keplersegg.myself.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.keplersegg.myself.R
import com.keplersegg.myself.activities.MasterActivity
import com.keplersegg.myself.fragments.DialogBadgeInfoFragment
import kotlinx.android.synthetic.main.component_badge.view.*

class BadgeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleRes: Int = 0
) : CardView(context, attrs, defStyleRes) {

    private var badgeId: Int = 0
    private var imageResourceId: Int = 0
    private var level: Int = 0

    init {
        LayoutInflater.from(context)
                .inflate(R.layout.component_badge, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it,
                    R.styleable.BadgeView, 0, 0)

            badgeId = typedArray.getInteger(R.styleable.BadgeView_badgeId, 0)
            imageResourceId = typedArray.getResourceId(R.styleable.BadgeView_iconSrc, R.drawable.ic_baseline_remove_24px)

            imgBadge.setImageResource(imageResourceId)

            typedArray.recycle()
        }

        rootView.setOnClickListener {

            val dialog = DialogBadgeInfoFragment()
            dialog.badgeId = badgeId
            dialog.imageResourceId = imageResourceId
            dialog.level = level

            dialog.show((context as MasterActivity).supportFragmentManager, "")
        }
    }

    fun setLevel(level: Int) {

        this.level = level
        rbLevel.rating = level.toFloat()
    }
}