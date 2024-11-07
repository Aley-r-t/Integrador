package com.kotlin.integrador.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.integrador.data.model.IptvModel
import com.kotlin.integrador.data.services.PlayerService
import com.kotlin.integrador.data.repository.ChannelRepository

class ChannelViewModel(
    private val channelRepository: ChannelRepository,
    private val playerService: PlayerService
) : ViewModel() {

    private val _channels = MutableLiveData<List<IptvModel>>()
    val channels: LiveData<List<IptvModel>> get() = _channels

    private val _currentChannelIndex = MutableLiveData<Int>()
    val currentChannelIndex: LiveData<Int> get() = _currentChannelIndex

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        fetchChannels()
    }

    private fun fetchChannels() {
        channelRepository.fetchChannelsList(
            onSuccess = { channels -> _channels.postValue(channels) },
            onError = { error -> _errorMessage.postValue(error) }
        )
    }

    fun playChannel(index: Int) {
        if (index in 0 until (_channels.value?.size ?: 0)) {
            _currentChannelIndex.postValue(index)
            _channels.value?.get(index)?.let { playerService.playChannel(it) }
        }
    }
}