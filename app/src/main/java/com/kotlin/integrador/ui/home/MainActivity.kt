package com.kotlin.integrador.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.model.IptvProvider
import com.kotlin.integrador.data.viewmodel.IptvViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var logoContainer: LinearLayout
    private val IptvModel = mutableListOf<IptvModel>()


    private var currentChannelIndex = 0

    private val IptvViewModel: IptvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //inflate el layout usando Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        playerView = binding.playerView
        logoContainer = binding.logoContainer


        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer

        fetchChannelsList("https://iptv-org.github.io/iptv/languages/spa.m3u")

        binding.btnTemporal.setOnClickListener {
            goNewActivity()
        }
    }

    private fun goNewActivity() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    //Esta logica la lleve a IptvViewModel
    private fun fetchChannelsList(playlistUrl: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(playlistUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //handle failure
                e.printStackTrace()
            }

            //Esto cambiamos un poco
            override fun onResponse(call: Call, response: Response) {
                // Ejecutar en un bloque try para capturar posibles errores
                try {
                    if (response.isSuccessful) {
                        // Capturar el cuerpo de la respuesta en una variable
                        val responseBody = response.body
                        val playlist = responseBody?.string()

                        if (playlist != null && playlist.isNotBlank()) {
                            // Parsear la lista de canales
                            val parsedChannels = IptvProvider.Iptvchannels(playlist)

                            // Ejecutar actualizaciones en el hilo principal
                            runOnUiThread {
                                IptvModel.clear()
                                IptvModel.addAll(parsedChannels)
                                displayChannels()
                                playChannel(currentChannelIndex)
                            }
                        } else {
                            // Manejo del caso en que la respuesta no tiene contenido
                            runOnUiThread {
                                showError("La respuesta está vacía o no es válida.")
                            }
                        }

                        // Cerrar el cuerpo de la respuesta
                        responseBody?.close()
                    } else {
                        // Si la respuesta no es exitosa, mostrar un mensaje de error en el hilo principal
                        runOnUiThread {
                            showError("Error al obtener canales: ${response.message}")
                        }
                    }
                } catch (e: Exception) {
                    // En caso de excepción, manejar el error de manera adecuada
                    e.printStackTrace()
                    runOnUiThread {
                        showError("Error procesando la respuesta: ${e.localizedMessage}")
                    }
                }
            }
            // Función para mostrar mensajes de error
        })
    }

    private fun showError(message: String) {
        // Aquí puedes mostrar un Toast, un Snackbar, o actualizar alguna vista de error
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

   //private fun parsePlaylist(playlist: String?): List<Channel> {
    //    val channels = mutableListOf<Channel>()

      //  playlist?.let {
    //        val lines = it.lines()
   //         var channelName = ""
   //         var logoUrl = ""
   //         var streamUrl: String

    //        for (line in lines) {
    //            if (line.startsWith("#EXTINF:")) {
    //                channelName = line.substringAfter("tvg-id=\"").substringBefore("\"")
    //                logoUrl = line.substringAfter("tvg-logo=\"").substringBefore("\"")
    //            } else if (line.startsWith("https")) {
    //                streamUrl = line
    //                val channel = Channel(channelName, streamUrl, logoUrl)
    //                channels.add(channel)
      //          }
     //       }
    //    }
      //  return channels
   //}

    private fun displayChannels() {
        logoContainer.removeAllViews()
        IptvModel.forEachIndexed { index, channel ->
            val logoView = createLogoView()
            logoContainer.addView(logoView)
            loadLogo(logoView, channel.logoUrl)
            logoView.setOnClickListener {
                playChannel(index)
            }
        }
    }

    private fun createLogoView(): ImageView {
        val logoView = ImageView(this)
        val layoutParams = LinearLayout.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.logo_size),
            resources.getDimensionPixelSize(R.dimen.logo_size)
        )
        val margin = resources.getDimensionPixelSize(R.dimen.logo_margin)
        layoutParams.setMargins(margin, margin, margin, margin)
        logoView.layoutParams = layoutParams
        return logoView
    }

    private fun loadLogo(imageView: ImageView, logoUrl: String) {
        Glide.with(this)
            .load(logoUrl)
            .into(imageView)

    }

    private fun playChannel(channelIndex: Int) {
        if (channelIndex in IptvModel.indices) {
            currentChannelIndex = channelIndex
            val channel = IptvModel[channelIndex]
            val mediaItem = MediaItem.fromUri(Uri.parse(channel.streamUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}
