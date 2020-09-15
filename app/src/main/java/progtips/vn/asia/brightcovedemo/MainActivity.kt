package progtips.vn.asia.brightcovedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import progtips.vn.asia.brightcovedemo.player.PlayerManager

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        const val APP_NAME = "app-name"
    }

    private lateinit var playerManager: PlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPlayer()
        playVideo(getString(R.string.videoId))
    }

    private fun initPlayer() {
        playerManager = PlayerManager(this)
    }

    private fun playVideo(videoId: String) {
        playerManager.playVideo(videoId) { player ->
            player.playWhenReady = true
            videoView.player = player
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        videoView.player.release()
    }

}