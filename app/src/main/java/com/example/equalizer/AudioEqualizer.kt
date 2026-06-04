package com.example.equalizer

import android.media.audiofx.Equalizer
import androidx.compose.runtime.mutableStateListOf

class AudioEqualizer {
    private var equalizer: Equalizer? = null
    val bands = mutableStateListOf<Band>()

    data class Band(val index: Short, val centerFreq: Int, var level: Short, val minLevel: Short, val maxLevel: Short)

    fun init() {
        try {
            // Session 0 applies the effect to the global audio mix
            equalizer = Equalizer(0, 0)
            equalizer?.enabled = true
            
            val numBands = equalizer?.numberOfBands ?: 0
            val minMax = equalizer?.bandLevelRange ?: shortArrayOf(0, 0)
            
            bands.clear()
            for (i in 0 until numBands) {
                val freq = equalizer?.getCenterFreq(i.toShort()) ?: 0
                val level = equalizer?.getBandLevel(i.toShort()) ?: 0
                bands.add(Band(i.toShort(), freq, level, minMax[0], minMax[1]))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(bandIndex: Short, level: Short) {
        equalizer?.setBandLevel(bandIndex, level)
        val index = bands.indexOfFirst { it.index == bandIndex }
        if (index != -1) {
            bands[index] = bands[index].copy(level = level)
        }
    }

    fun release() {
        equalizer?.release()
    }
}
