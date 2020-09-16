package progtips.vn.asia.brightcovedemo.player

import android.content.Context
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import progtips.vn.asia.brightcovedemo.provider.BrightcoveProvider
import progtips.vn.asia.brightcovedemo.provider.MediaProvider

class PlayerManager(
    private val context: Context
) {
    private val mediaProvider: MediaProvider = BrightcoveProvider(context)

    fun playVideo(videoId: String, callback: (player: Player) -> Unit) {
        mediaProvider.getVideoSourceById(videoId) { mediaSource ->
            callback(initPlayer(mediaSource))
        }
    }

    private fun initPlayer(mediaSource: MediaItem): Player {
        return SimpleExoPlayer.Builder(context).build().apply {
            addMediaItem(mediaSource)
        }
    }
}