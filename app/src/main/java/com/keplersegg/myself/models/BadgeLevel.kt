package com.keplersegg.myself.models

import android.content.Context
import com.keplersegg.myself.R

class BadgeLevel {

    var BadgeId: Int = 0
    var Level: Int = 0
    var Name: String? = ""
    var Description: String = ""

    companion object {

        private fun CreateItem(badgeId: Int, level: Int, name: String, description: String): BadgeLevel
        {
            val item = BadgeLevel()
            item.BadgeId = badgeId
            item.Level = level
            item.Name = name
            item.Description = description
            return item
        }

        fun GetBadgeLevels(context: Context) : ArrayList<BadgeLevel>
        {
            val result = ArrayList<BadgeLevel>()

            result.add(CreateItem(1, 1, context.getString(R.string.badge_name_1_1), context.getString(R.string.badge_desc_1_1)))
            result.add(CreateItem(1, 2, context.getString(R.string.badge_name_1_2), context.getString(R.string.badge_desc_1_2)))
            result.add(CreateItem(1, 3, context.getString(R.string.badge_name_1_3), context.getString(R.string.badge_desc_1_3)))
            result.add(CreateItem(2, 1, context.getString(R.string.badge_name_2_1), context.getString(R.string.badge_desc_2_1)))
            result.add(CreateItem(2, 2, context.getString(R.string.badge_name_2_2), context.getString(R.string.badge_desc_2_2)))
            result.add(CreateItem(2, 3, context.getString(R.string.badge_name_2_3), context.getString(R.string.badge_desc_2_3)))
            result.add(CreateItem(3, 1, context.getString(R.string.badge_name_3_1), context.getString(R.string.badge_desc_3_1)))
            result.add(CreateItem(3, 2, context.getString(R.string.badge_name_3_2), context.getString(R.string.badge_desc_3_2)))
            result.add(CreateItem(3, 3, context.getString(R.string.badge_name_3_3), context.getString(R.string.badge_desc_3_3)))

            return result
        }
    }
}
