package com.kotlin.integrador.data.model

import android.util.Log

class IptvProvider {
    companion object {
     fun Iptvchannels(parseplayList: String?): List<IptvModel> {
        val IptvplayList = mutableListOf<IptvModel>()
        parseplayList?.let {
            val lines = it.lines()
            var channelName = ""
            var logoUrl = ""
            var channelcategory = ""
            var streamurl: String
            for (line in lines) {
                if (line.startsWith("#EXTINF:")) {
                    channelName = line.substringAfter("tvg-id=\"").substringBefore("\"")
                    channelcategory = line.substringAfter("group-title=\"").substringBefore("\"")
                    logoUrl = line.substringAfter("tvg-logo=\"").substringBefore("\"")
                } else if (line.startsWith("http")) {
                    streamurl = line
                    val chanels =IptvModel(channelName, logoUrl, channelcategory, streamurl)
                    IptvplayList.add(chanels)
                }
            }
        }
        return IptvplayList
    }
    }
}