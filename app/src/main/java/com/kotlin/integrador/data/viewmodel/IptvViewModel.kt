package com.kotlin.integrador.data.viewmodel

import com.kotlin.integrador.data.model.IptvProvider
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

//seleccionar

import com.kotlin.integrador.data.model.IptvModel


class IptvViewModel {


    private val client = OkHttpClient()

    fun fetchChannelsList(playlistUrl: String, onSuccess: (List<IptvModel>) -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url(playlistUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException)
            {
                onError("Error al obtener canales: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val parsedChannels = responseBody?.let { IptvProvider.Iptvchannels(it) } ?: emptyList()
                        onSuccess(parsedChannels)
                    } else {
                        onError("Error al obtener canales: ${response.message}")
                    }
                } catch (e: Exception) {
                    onError("Error procesando la respuesta: ${e.localizedMessage}")
                } finally {
                    response.body?.close()
                }
            }
        })
    }
}