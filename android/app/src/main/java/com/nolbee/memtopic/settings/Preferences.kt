package com.nolbee.memtopic.settings

enum class IntervalPosition {
    BEFORE, AFTER;

    companion object {
        fun fromString(value: String): IntervalPosition {
            return when (value.uppercase()) {
                "BEFORE" -> BEFORE
                else -> AFTER
            }
        }
    }
}

object SettingKeys {
    const val PLAYBACK_SPEED = "playbackSpeed"
    const val SENTENCE_REPUTATION = "sentenceReputation"
    const val INTERVAL_TIME_MULTIPLIER = "intervalTimeMultiplier"
    const val INTERVAL_POSITION = "intervalPosition"
}

data class Preferences(
    val playbackSpeed: Float,
    val sentenceReputation: Int,
    val intervalTimeMultiplier: Float,
    val intervalPosition: IntervalPosition,
) {
    constructor(
        playbackSpeed: String?,
        sentenceReputation: String?,
        intervalTimeMultiplier: String?,
        intervalPosition: String?
    ) : this(playbackSpeed?.toFloatOrNull() ?: 1.0f,
        sentenceReputation?.toIntOrNull() ?: 2,
        intervalTimeMultiplier?.toFloatOrNull() ?: 1.0f,
        intervalPosition?.let { IntervalPosition.fromString(it) } ?: IntervalPosition.AFTER)

    fun toSettingsMap(): Map<String, String> {
        return mapOf(
            SettingKeys.PLAYBACK_SPEED to playbackSpeed.toString(),
            SettingKeys.SENTENCE_REPUTATION to sentenceReputation.toString(),
            SettingKeys.INTERVAL_TIME_MULTIPLIER to intervalTimeMultiplier.toString(),
            SettingKeys.INTERVAL_POSITION to intervalPosition.toString()
        )
    }
}
