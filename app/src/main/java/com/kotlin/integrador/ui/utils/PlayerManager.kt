import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class PlayerManager(private val context: Context, private val playerView: PlayerView) {
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    init {
        playerView.player = exoPlayer
    }

    fun play(url: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun releasePlayer() {
        exoPlayer.release()
    }
}
