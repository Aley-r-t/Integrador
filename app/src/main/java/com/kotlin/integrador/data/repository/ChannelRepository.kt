package com.kotlin.integrador.data.repository

import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.model.IptvProvider
import com.kotlin.integrador.data.network.NetworkConstants
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ChannelRepository {
    private val client = OkHttpClient()

    fun fetchChannelsList(onSuccess: (List<IptvModel>) -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url(NetworkConstants.IPTV_CHANNELS_URL)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                onError(e.message ?: "Unknown error")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val channels = IptvProvider.Iptvchannels(responseBody)
                        onSuccess(channels)
                    } ?: onError("Empty response")
                } else {
                    onError("Failed to fetch channels: ${response.message}")
                }
            }
        })
    }
}
