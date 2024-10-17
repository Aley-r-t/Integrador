package com.kotlin.integrador.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.integrador.R
import com.kotlin.integrador.data.model.IptvModel

class IptvAdapter(private val iptvChannels: List<IptvModel>) : RecyclerView.Adapter<IptvViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IptvViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_channels, parent, false)
        return IptvViewHolder(view)
    }

    override fun getItemCount(): Int = iptvChannels.size

    override fun onBindViewHolder(holder: IptvViewHolder, position: Int) {
        val item = iptvChannels[position]
        holder.render(item)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(position)
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun getChannel(index: Int): IptvModel {
        return iptvChannels[index]
    }
}
