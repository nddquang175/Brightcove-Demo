package progtips.vn.asia.brightcovedemo.provider

import android.content.Context
import android.net.Uri
import com.brightcove.player.controller.ExoPlayerSourceSelector
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.PlaylistListener
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Playlist
import com.brightcove.player.model.Source
import com.brightcove.player.model.Video
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DrmSessionEventListener
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import progtips.vn.asia.brightcovedemo.MainActivity
import progtips.vn.asia.brightcovedemo.R

class BrightcoveProvider(
    context: Context
): MediaProvider {

    private val catalog = Catalog(
        EventEmitterImpl(),
        context.getString(R.string.account),
        context.getString(R.string.policy)
    )

    override fun getVideoSourceById(videoId: String, callBack: (mediaItem: MediaItem) -> Unit) {
        catalog.findVideoByID(videoId, object : VideoListener() {
            override fun onVideo(p0: Video?) {
                p0?.let { video -> handleVideo(video, callBack) }
            }
        })
    }

    private fun handleVideo(
        video: Video,
        callBack: (mediaItem: MediaItem) -> Unit
    ) {
        val source = ExoPlayerSourceSelector().selectSource(video)
        val mediaItem = buildMediaItem(video, source)

        callBack.invoke(mediaItem)
    }

    private fun buildMediaItem(video: Video, source: Source): MediaItem {
        val mediaItem = MediaItem.Builder()
            .setUri(source.url)
            .setDrmUuid(C.WIDEVINE_UUID)
            .setDrmMultiSession(true)

        if (source.hasKeySystem(Source.Fields.WIDEVINE_KEY_SYSTEM)) {
            mediaItem.setDrmLicenseUri(source.getLicenseUrl())
        }

        return mediaItem.build()
    }

    /**
     * Extension function for getting License URL from source
     */
    private fun Source.getLicenseUrl() = (properties[Source.Fields.KEY_SYSTEMS] as Map<String, Map<String, String>>).get(Source.Fields.WIDEVINE_KEY_SYSTEM)!!.get(Source.Fields.LICENSE_URL)
}