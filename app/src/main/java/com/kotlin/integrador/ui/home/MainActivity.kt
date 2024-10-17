package com.kotlin.integrador.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import com.kotlin.integrador.data.adapter.IptvAdapter
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.viewmodel.IptvViewModel

class MainActivity : AppCompatActivity() {
    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Player
    private lateinit var exoPlayer: ExoPlayer

    // Data Models
    private val iptvViewModel = IptvViewModel()

    // Current Channel Index
    private var currentChannelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupViewBinding()
        setupWindowInsets()
        initializePlayer()
        setupRecyclerView()
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

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
            setupRecyclerViewAdapter(channels)
            playChannel(currentChannelIndex)
        }
    }

    private fun setupRecyclerViewAdapter(channels: List<IptvModel>) {
        val adapter = IptvAdapter(channels)
        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener { index ->
            playChannel(index)
        }
    }

    private fun handleFetchError(error: String) {
        runOnUiThread {
            showError(error)
        }
    }

    private fun playChannel(channelIndex: Int) {
        if (channelIndex in 0 until (binding.recyclerView.adapter?.itemCount ?: 0)) {
            currentChannelIndex = channelIndex
            val channel = (binding.recyclerView.adapter as IptvAdapter).getChannel(channelIndex)
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
