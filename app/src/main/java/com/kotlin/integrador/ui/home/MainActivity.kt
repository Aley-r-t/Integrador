package com.kotlin.integrador.ui.home

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer


import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
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

    private val channels = mutableListOf<Channel>()
    private var currentChannelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //inflate el layout usando Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setContentView(R.layout.activity_main)
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
    }


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

            override fun onResponse(call: Call, response: Response) {
                val playlist = response.body?.string()
                val parsedChannels = parsePlaylist(playlist)

                runOnUiThread {
                    channels.clear()
                    channels.addAll(parsedChannels)
                    displayChannels()
                    playChannel(currentChannelIndex)
                }
            }
        })
    }

    private fun parsePlaylist(playlist: String?): List<Channel> {
        val channels = mutableListOf<Channel>()

        playlist?.let {
            val lines = it.lines()
            var channelName = ""
            var logoUrl = ""
            var streamUrl = ""

            for (line in lines) {
                if (line.startsWith("#EXTINF:")) {
                    channelName = line.substringAfter("tvg-id=\"").substringBefore("\"")
                    logoUrl = line.substringAfter("tvg-logo=\"").substringBefore("\"")
                } else if (line.startsWith("http")) {
                    streamUrl = line
                    val channel = Channel(channelName, streamUrl, logoUrl)
                    channels.add(channel)
                }
            }
        }
        return channels
    }


    private fun displayChannels() {
        logoContainer.removeAllViews()

        channels.forEachIndexed { index, channel ->
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
        if (channelIndex in channels.indices) {
            currentChannelIndex = channelIndex
            val channel = channels[channelIndex]
            val mediaItem = MediaItem.fromUri(Uri.parse(channel.channelUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    data class Channel(val name: String, val channelUrl: String, val logoUrl: String)
}
