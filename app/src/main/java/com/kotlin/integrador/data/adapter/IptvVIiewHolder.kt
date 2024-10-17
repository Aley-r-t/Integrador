package com.kotlin.integrador.data.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.integrador.R
import com.kotlin.integrador.data.model.IptvModel

// Esto va de la mano con el IptvAdapter
class IptvViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val iptvName: TextView = view.findViewById(R.id.iptv_name)
    private val iptvCategory: TextView = view.findViewById(R.id.iptv_categori)
    private val iptvLogo: ImageView = view.findViewById(R.id.iptv_logo)

    fun render(iptvModel: IptvModel) {
        iptvName.text = iptvModel.channelName
        iptvCategory.text = iptvModel.channelCategory

        // Usar Glide para cargar el logotipo del canal
        Glide.with(itemView.context)
            .load(iptvModel.logoUrl)
            .into(iptvLogo)
    }
}
