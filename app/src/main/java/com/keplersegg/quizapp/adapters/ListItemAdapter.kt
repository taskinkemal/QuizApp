package com.keplersegg.myself.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.keplersegg.myself.interfaces.IListItemHoster
import com.keplersegg.myself.models.ListItem
import com.keplersegg.myself.models.ListSource
import com.keplersegg.myself.R

class ListItemAdapter(private val hoster: IListItemHoster?, private val items: ListSource?) : RecyclerView.Adapter<ListItemAdapter.DataObjectHolder>() {

    inner class DataObjectHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lblLabel: TextView = itemView.findViewById(R.id.lblLabel)
        val imgIcon: ImageView = itemView.findViewById(R.id.imgIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_simple, parent, false)

        return DataObjectHolder(itemView = view)
    }

    init {

        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {

        // object item based on the position
        val item = if (items != null) items.list[position] else null

        holder.lblLabel.text = item!!.Label

        if (items!!.showImage) {
            holder.imgIcon.visibility = View.VISIBLE
            Glide.with(hoster!!.getActivity()!!)
                    .load(item.ImageDrawable)
                    .into(holder.imgIcon)
        }
        else {
            holder.imgIcon.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { hoster!!.onSelectListItem(item) }
    }

    override fun getItemId(position: Int): Long {

        return items!!.list[position].ItemId.hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return items?.list?.size ?: 0
    }

    fun updateData(list: List<ListItem>) {

        items!!.list.clear()
        items.list.addAll(list)

        notifyDataSetChanged()
    }
}