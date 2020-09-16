package progtips.vn.asia.brightcovedemo.provider

import com.google.android.exoplayer2.MediaItem

interface MediaProvider {
    fun getVideoSourceById(
        videoId: String,
        callBack: (mediaItem: MediaItem) -> Unit
    )

}