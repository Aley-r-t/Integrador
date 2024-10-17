package com.kotlin.integrador.data.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.integrador.R
import com.kotlin.integrador.data.model.IptvModel

//  Esto va de la mano con el IptvAdapte
//  para configurar la recycleview falta terminar
class IptvVIiewHolder(view:View):RecyclerView.ViewHolder(view){

    val iptv_name = view.findViewById<TextView>(R.id.iptv_name)
    val iptv_categori = view.findViewById<TextView>(R.id.iptv_categori)
    val iptv_logo = view.findViewById<ImageView>(R.id.iptv_logo)

    fun render(iptvModel: IptvModel){
        iptv_name.text = iptvModel.channelName
        iptv_categori.text = iptvModel.channelCategory
    }
}