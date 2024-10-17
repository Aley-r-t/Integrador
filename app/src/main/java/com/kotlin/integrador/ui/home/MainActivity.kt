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
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.viewmodel.IptvViewModel

class MainActivity : AppCompatActivity() {
    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Player and UI Elements
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var logoContainer: LinearLayout

    // Data Models
    private val iptvModelList = mutableListOf<IptvModel>()
    private val iptvViewModel = IptvViewModel()

    // Current Channel Index
    private var currentChannelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupViewBinding()
        setupWindowInsets()
        initializeUIElements()
        initializePlayer()
        setupButtonListeners()
        fetchChannels()
    }

    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeUIElements() {
        playerView = binding.playerView
        logoContainer = binding.logoContainer
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer
    }

    private fun setupButtonListeners() {
        binding.btnTemporal.setOnClickListener {
            goNewActivity()
        }
    }

    private fun fetchChannels() {
        iptvViewModel.fetchChannelsList(
            "https://iptv-org.github.io/iptv/languages/spa.m3u",
            onSuccess = { channels ->
                handleFetchSuccess(channels)
            },
            onError = { error ->
                handleFetchError(error)
            }
        )
    }

    private fun handleFetchSuccess(channels: List<IptvModel>) {
        runOnUiThread {
            updateChannelList(channels)
            displayChannels()
            playChannel(currentChannelIndex)
        }
    }

    private fun handleFetchError(error: String) {
        runOnUiThread {
            showError(error)
        }
    }

    private fun updateChannelList(channels: List<IptvModel>) {
        iptvModelList.clear()
        iptvModelList.addAll(channels)
    }

    private fun displayChannels() {
        logoContainer.removeAllViews()
        iptvModelList.forEachIndexed { index, channel ->
            val logoView = createLogoView()
            logoContainer.addView(logoView)
            loadLogo(logoView, channel.logoUrl)
            setupLogoClickListener(logoView, index)
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

    private fun setupLogoClickListener(logoView: ImageView, index: Int) {
        logoView.setOnClickListener {
            playChannel(index)
        }
    }

    private fun playChannel(channelIndex: Int) {
        if (channelIndex in iptvModelList.indices) {
            currentChannelIndex = channelIndex
            val channel = iptvModelList[channelIndex]
            val mediaItem = MediaItem.fromUri(Uri.parse(channel.streamUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun goNewActivity() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        exoPlayer.release()
    }
}
