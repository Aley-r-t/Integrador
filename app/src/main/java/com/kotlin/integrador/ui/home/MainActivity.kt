package com.kotlin.integrador.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
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
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.viewmodel.IptvViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var logoContainer: LinearLayout
    private val IptvModel = mutableListOf<IptvModel>()
    private val IptvViewModel = IptvViewModel()

    private var currentChannelIndex = 0

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

        //fetchChannelsList("https://iptv-org.github.io/iptv/languages/spa.m3u")
        fetchChannels()

        binding.btnTemporal.setOnClickListener {
            goNewActivity()
        }
    }

    private fun goNewActivity() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    private fun fetchChannels() {
        // Delegate the network call and data processing to the ViewModel
        IptvViewModel.fetchChannelsList("https://iptv-org.github.io/iptv/languages/spa.m3u", onSuccess =  { channels ->
            // Update UI with obtained channels on success
            runOnUiThread{
                IptvModel.clear()
                IptvModel.addAll(channels)
                displayChannels()
                playChannel(currentChannelIndex)
            }
        },
            onError = { error ->
                runOnUiThread {
                    // Show an error message to the user on failure
                    showError(error)
                }
            }
        )
    }


    private fun showError(message: String) {
        // Aquí puedes mostrar un Toast, un Snackbar, o actualizar alguna vista de error
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

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
