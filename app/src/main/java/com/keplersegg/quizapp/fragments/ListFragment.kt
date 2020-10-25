package com.keplersegg.myself.fragments


import android.os.Bundle
import android.view.View
import com.keplersegg.myself.adapters.ListItemAdapter
import com.keplersegg.myself.models.ListItem

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.keplersegg.myself.interfaces.IListItemHoster
import com.keplersegg.myself.models.ListSource


abstract class ListFragment : MasterFragment(), IListItemHoster {

    private var listSource: ListSource = ListSource()
    private var adapter: ListItemAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listSource.showImage = showImages()
        adapter = ListItemAdapter(this, listSource)
        getRecyclerView().adapter = adapter
        getRecyclerView().layoutManager = LinearLayoutManager(activity)
    }

    abstract fun getList(): List<ListItem>
    abstract fun showImages(): Boolean
    abstract fun getRecyclerView(): RecyclerView

    override fun onResume() {
        super.onResume()

        adapter!!.updateData(getList())
    }
}
