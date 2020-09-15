package progtips.vn.asia.brightcovedemo.player

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
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

    private val drmEventListener = object : DefaultDrmSessionEventListener {
        override fun onDrmKeysRestored() {
        }

        override fun onDrmKeysLoaded() {
        }

        override fun onDrmKeysRemoved() {
        }

        override fun onDrmSessionManagerError(error: Exception?) {
        }
    }

    fun playVideo(videoId: String, callback: (player: Player) -> Unit) {
        mediaProvider.getVideoSourceById(videoId, drmEventListener) { mediaSource, drmSessionManager ->
            callback(initPlayer(mediaSource, drmSessionManager))
        }
    }

    private fun initPlayer(mediaSource: MediaSource, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?): Player {
        val trackSelector = DefaultTrackSelector().apply {
            setParameters(
                this.buildUponParameters().apply {
                    setMaxVideoSize(3840, 2160)
                    setMaxVideoFrameRate(5986000)
                    setRendererDisabled(C.TRACK_TYPE_TEXT, false)
                }
            )
        }

        return ExoPlayerFactory.newSimpleInstance(
            context,
            DefaultTrackSelector(),
            DefaultLoadControl(),
            drmSessionManager
        ).apply { prepare(mediaSource) }
    }
}