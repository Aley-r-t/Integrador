package com.kotlin.integrador.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.integrador.R
import com.kotlin.integrador.data.model.IptvModel

//falta llamar esto desde la vista de MainActivity
class IptvAdapter(private val iptvchannels:List<IptvModel>) :RecyclerView.Adapter<IptvVIiewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IptvVIiewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return IptvVIiewHolder(layoutInflater.inflate(R.layout.item_channels, parent, false))
    }

    override fun getItemCount(): Int = iptvchannels.size



    override fun onBindViewHolder(holder: IptvVIiewHolder, position: Int) {
        val item = iptvchannels[position]
        holder.render(item)
    }
}