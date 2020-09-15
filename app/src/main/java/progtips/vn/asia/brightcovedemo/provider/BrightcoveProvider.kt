package progtips.vn.asia.brightcovedemo.provider

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.brightcove.player.Constants
import com.brightcove.player.controller.ExoPlayerSourceSelector
import com.brightcove.player.drm.ExoPlayerDrmSessionManager
import com.brightcove.player.drm.WidevineMediaDrmCallback
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.EventEmitterImpl
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Source
import com.brightcove.player.model.Video
import com.google.android.exoplayer2.drm.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import progtips.vn.asia.brightcovedemo.MainActivity
import progtips.vn.asia.brightcovedemo.R

class BrightcoveProvider(
    private val context: Context
): MediaProvider {

    private val catalog = Catalog(
        EventEmitterImpl(),
        context.getString(R.string.account),
        context.getString(R.string.policy)
    )

    override fun getVideoSourceById(
        videoId: String,
        drmEventListener: DefaultDrmSessionEventListener,
        callBack: (mediaSource: MediaSource, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) -> Unit
    ) {
        catalog.findVideoByID(videoId, object: VideoListener() {
            override fun onVideo(p0: Video?) {
                p0?.let { video -> handleVideo(video, drmEventListener, callBack) }
            }
        })
    }

    private fun handleVideo(
        video: Video,
        drmEventListener: DefaultDrmSessionEventListener,
        callBack: (mediaSource: MediaSource, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) -> Unit
    ) {
        val source = ExoPlayerSourceSelector().selectSource(video)
        val mediaSource = buildMediaSource(source)
        val drmSessionManager = buildDrmSessionManager(video, source, drmEventListener)

        callBack.invoke(mediaSource, drmSessionManager)
    }

    private fun buildDrmSessionManager(video: Video, source: Source, drmEventListener: DefaultDrmSessionEventListener): DrmSessionManager<FrameworkMediaCrypto>? {
        return if (source.hasKeySystem("com.widevine.alpha") || video.properties.containsKey("defaultUrl")) {
            val drmSessionManager = ExoPlayerDrmSessionManager(
                Constants.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(
                    Constants.WIDEVINE_UUID
                ), WidevineMediaDrmCallback.create(video.properties, source.properties), null, Handler(), drmEventListener
            )

            video.offlinePlaybackLicenseKey?.let { playbackLicense ->
                drmSessionManager.setMode(DefaultDrmSessionManager.MODE_PLAYBACK, playbackLicense)
            }

            drmSessionManager
        } else
            null
    }

    private fun buildMediaSource(source: Source): MediaSource {
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, MainActivity.APP_NAME)
        )

        return when (val deliveryType = source.deliveryType) {
            DeliveryType.DASH -> {
                DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(source.url))
            }

            DeliveryType.HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(
                Uri.parse(source.url))

            DeliveryType.MP4 -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                Uri.parse(source.url))

            else -> throw IllegalStateException("Unsupported type: $deliveryType")
        }
    }
}