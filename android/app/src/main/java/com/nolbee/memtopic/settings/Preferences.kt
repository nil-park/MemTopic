package com.nolbee.memtopic.settings

object SettingKeys {
    const val PLAYBACK_SPEED = "playbackSpeed"
    const val SENTENCE_REPUTATION = "sentenceReputation"
    const val PRE_INTERVAL_MULTIPLIER = "preIntervalMultiplier"
    const val POST_INTERVAL_MULTIPLIER = "postIntervalMultiplier"
}

data class Preferences(
    val playbackSpeed: Float,
    val sentenceReputation: Int,
    val preIntervalMultiplier: Float,
    val postIntervalMultiplier: Float,
) {
    constructor(
        playbackSpeed: String?,
        sentenceReputation: String?,
        preIntervalMultiplier: String?,
        postIntervalMultiplier: String?,
    ) : this(
        playbackSpeed?.toFloatOrNull() ?: 1.0f,
        sentenceReputation?.toIntOrNull() ?: 2,
        preIntervalMultiplier?.toFloatOrNull() ?: 0.0f,
        postIntervalMultiplier?.toFloatOrNull() ?: 1.0f
    )

    fun toSettingsMap(): Map<String, String> {
        return mapOf(
            SettingKeys.PLAYBACK_SPEED to playbackSpeed.toString(),
            SettingKeys.SENTENCE_REPUTATION to sentenceReputation.toString(),
            SettingKeys.PRE_INTERVAL_MULTIPLIER to preIntervalMultiplier.toString(),
            SettingKeys.POST_INTERVAL_MULTIPLIER to postIntervalMultiplier.toString(),
        )
    }
}
