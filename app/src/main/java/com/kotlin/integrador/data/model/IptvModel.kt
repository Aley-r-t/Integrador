package com.kotlin.integrador.data.model

data class IptvModel(
    val channelName: String,
    val logoUrl: String,
    val channelCategory: String,  // He corregido el nombre para que siga el estilo camelCase
    val streamUrl: String  // He corregido el nombre para que siga el estilo camelCase
)
