package com.kotlin.integrador.ui.home

import PlayerManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.kotlin.integrador.R
import com.kotlin.integrador.databinding.ActivityMainBinding
import com.kotlin.integrador.data.adapter.IptvAdapter
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.repository.ChannelRepository

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // Player Manager
    private lateinit var playerManager: PlayerManager

    // Data Models
    private val channelRepository = ChannelRepository()

    // Current Channel Index
    private var currentChannelIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupViewBinding()
        setupWindowInsets()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.main)

        toggle = ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        initializePlayerManager()
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_create_account -> Toast.makeText(this,"Item 1", Toast.LENGTH_SHORT).show()
            R.id.nav_categories -> Toast.makeText(this,"Item 2",Toast.LENGTH_SHORT).show()
            R.id.nav_endpoint -> Toast.makeText(this,"Item 3",Toast.LENGTH_SHORT).show()
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onPostCreate(savedInstanceState: Bundle?){
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun initializePlayerManager() {
        playerManager = PlayerManager(this, binding.playerView)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setupButtonListeners() {
        binding.btnTemporal.setOnClickListener {
            goNewActivity()
        }
    }

    private fun fetchChannels() {
        channelRepository.fetchChannelsList(
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
            showMessage(error)
        }
    }

    private fun playChannel(channelIndex: Int) {
        if (channelIndex in 0 until (binding.recyclerView.adapter?.itemCount ?: 0)) {
            currentChannelIndex = channelIndex
            val channel = (binding.recyclerView.adapter as IptvAdapter).getChannel(channelIndex)
            playerManager.play(channel.streamUrl)
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun goNewActivity() {
        val intent = Intent(this, MainActivity2::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.releasePlayer()
    }
}
