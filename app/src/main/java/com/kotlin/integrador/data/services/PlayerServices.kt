package com.kotlin.integrador.data.services

import PlayerManager
import com.kotlin.integrador.data.model.IptvModel

class PlayerService(private val playerManager: PlayerManager) {

    fun playChannel(channel: IptvModel) {
        playerManager.play(channel.streamUrl)
    }

    fun releasePlayer() {
        playerManager.releasePlayer()
    }
}
