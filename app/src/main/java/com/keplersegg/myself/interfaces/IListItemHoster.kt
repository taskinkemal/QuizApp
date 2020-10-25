package com.keplersegg.myself.interfaces

import androidx.fragment.app.FragmentActivity
import com.keplersegg.myself.models.ListItem


interface IListItemHoster {

    fun onSelectListItem(item: ListItem)

    fun getActivity() : FragmentActivity?
}
