package com.brownik.newmediaplayer.service

import android.media.MediaPlayer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.brownik.newmediaplayer.userinterface.MyObject

class MediaPlayerAdapter(private val playbackInfoListener: PlaybackInfoListener) {
    private lateinit var mediaPlayer: MediaPlayer
    private var mediaState: Int = 0

    fun initMediaPlayerAdapter() {
        mediaPlayer = MediaPlayer()
    }

    fun onPlay(metadata: MediaMetadataCompat) {
        MyObject.makeLog("MediaPlayerAdapter.onPlay")
        MyObject.makeLog("mediaState: $mediaState")
        if (mediaPlayer.isPlaying || mediaState == 0) {
            mediaPlayer.apply {
                stop()
                reset()
                setDataSource(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
                prepare()
                start()
            }
        } else {
            mediaPlayer.start()
            setNewState(PlaybackStateCompat.STATE_PLAYING)
        }
    }

    fun onPause() {
        MyObject.makeLog("MediaPlayerAdapter.onPause")
        mediaPlayer.pause()
        setNewState(PlaybackStateCompat.STATE_PAUSED)
    }

    fun release() {
        MyObject.makeLog("MediaPlayerAdapter.release")
        mediaPlayer.release()
    }

    private fun setNewState(newPlayerState: Int) {
        MyObject.makeLog("MediaPlayerAdapter.setNewState")
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(getAvailableActions())
        stateBuilder.setState(newPlayerState, 0, 1.0f)
        playbackInfoListener.onPlaybackStateChange(stateBuilder.build())
    }

    @PlaybackStateCompat.Actions
    private fun getAvailableActions(): Long {
        val actions: Long = PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID or
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS

        return when (mediaState) {
            PlaybackStateCompat.STATE_STOPPED -> actions or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
            PlaybackStateCompat.STATE_PLAYING -> actions or PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_SEEK_TO
            PlaybackStateCompat.STATE_PAUSED -> actions or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP
            else -> actions or PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_STOP or PlaybackStateCompat.ACTION_PAUSE
        }
    }
}