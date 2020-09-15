package progtips.vn.asia.brightcovedemo.provider

import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
import com.google.android.exoplayer2.drm.DrmSessionManager
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
import com.google.android.exoplayer2.source.MediaSource

interface MediaProvider {
    fun getVideoSourceById(
        videoId: String,
        drmEventListener: DefaultDrmSessionEventListener,
        callBack: (mediaSource: MediaSource, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) -> Unit
    )
}