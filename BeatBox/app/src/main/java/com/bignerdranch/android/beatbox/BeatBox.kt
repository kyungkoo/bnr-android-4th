package com.bignerdranch.android.beatbox

import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import java.io.IOException

private const val TAG = "BeatBox"
private const val SOUND_FOLDER = "sample_sounds"
private const val MAX_SOUNDS = 5

class BeatBox(private val assets: AssetManager) {

    val sounds: List<Sound>
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS)
        .build()

    init {
        sounds = loadSounds()
    }

    private fun loadSounds(): List<Sound> {
        try {
            val soundNames = assets.list(SOUND_FOLDER) ?: emptyArray()
            val sounds = soundNames
                .map { Sound("$SOUND_FOLDER/$it") }
                .toList()

            sounds.forEach {
                try {
                    load(it)
                } catch (ioe: IOException) {
                    Log.e(TAG, "Could not load sound $it", ioe)
                }
            }

            return sounds
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets", e)
            return emptyList()
        }
    }

    private fun load(sound: Sound) {
        val afd = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(afd, 1)
        sound.soundId = soundId
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }
}