package com.kotlin.integrador.data.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.model.IptvProvider.Companion.Iptvchannels
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class IptvViewModel : ViewModel() {

    // LiveData para observar la lista de canales desde la vista
    private val _channels = MutableLiveData<List<IptvModel>>()
    val channels: LiveData<List<IptvModel>> get() = _channels

    // MutableLiveData para manejar los errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Método para obtener los canales desde el proveedor
    fun fetchIptvChannels(playlistUrl: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(playlistUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Manejar el error y actualizar el LiveData del error
                e.printStackTrace()
                _error.postValue("Error de conexión: ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        if (responseBody != null && responseBody.isNotBlank()) {
                            // Parsear la lista de canales usando el proveedor (IptvProvider)
                            val parsedChannels = Iptvchannels(responseBody)
                            _channels.postValue(parsedChannels)
                        } else {
                            _error.postValue("La respuesta está vacía o no es válida.")
                        }
                    } else {
                        _error.postValue("Error al obtener canales: ${response.message}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _error.postValue("Error procesando la respuesta: ${e.localizedMessage}")
                }
            }
        })
    }
}
