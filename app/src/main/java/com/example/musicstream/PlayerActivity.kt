package com.example.musicstream

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.musicstream.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityPlayerBinding
    lateinit var exoPlayer: ExoPlayer

    var playerListener = object :Player.Listener{
        //method
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying)
        }
    }
    @OptIn(UnstableApi::class) override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = Color.BLACK
        MyExoPlayer.getCurrentSong()?.apply {
            binding.songTitleTextView.text=title
            binding.songSubtitleTextView.text=subtitle
            Glide.with(binding.songCoverImageView).load(coverUrl)
                .circleCrop()
                .into(binding.songCoverImageView)
            Glide.with(binding.gifImageView).load(R.drawable.media_playing)
                .circleCrop()
                .into(binding.gifImageView)
            exoPlayer =MyExoPlayer.getInstance()!!
            binding.playerView.player =exoPlayer
            binding.playerView.showController()
            exoPlayer.addListener(playerListener)   // for fixing the gif when play or pause
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.removeListener(playerListener)
    }
    fun showGif(show:Boolean)
    {
        if (show)
        {
            binding.gifImageView.visibility = View.VISIBLE
        }
        else
            binding.gifImageView.visibility = View.INVISIBLE
    }

}