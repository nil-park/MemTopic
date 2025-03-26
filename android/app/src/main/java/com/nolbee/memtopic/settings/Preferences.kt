package com.nolbee.memtopic.settings

object SettingKeys {
    const val PLAYBACK_SPEED = "playbackSpeed"
    const val SENTENCE_REPUTATION = "sentenceReputation"
    const val PRE_INTERVAL_MULTIPLIER = "preIntervalMultiplier"
    const val POST_INTERVAL_MULTIPLIER = "postIntervalMultiplier"
}

data class Preferences(
    val playbackSpeed: Float = DEFAULT_PLAYBACK_SPEED,
    val sentenceReputation: Int = DEFAULT_SENTENCE_REPUTATION,
    val preIntervalMultiplier: Float = DEFAULT_PRE_INTERVAL_MULTIPLIER,
    val postIntervalMultiplier: Float = DEFAULT_POST_INTERVAL_MULTIPLIER,
) {
    companion object {
        const val DEFAULT_PLAYBACK_SPEED = 1.0f
        const val DEFAULT_SENTENCE_REPUTATION = 2
        const val DEFAULT_PRE_INTERVAL_MULTIPLIER = 0.0f
        const val DEFAULT_POST_INTERVAL_MULTIPLIER = 1.0f

        fun fromStrings(
            playbackSpeed: String?,
            sentenceReputation: String?,
            preIntervalMultiplier: String?,
            postIntervalMultiplier: String?,
        ) = Preferences(
            playbackSpeed = playbackSpeed?.toFloatOrNull() ?: DEFAULT_PLAYBACK_SPEED,
            sentenceReputation = sentenceReputation?.toIntOrNull() ?: DEFAULT_SENTENCE_REPUTATION,
            preIntervalMultiplier = preIntervalMultiplier?.toFloatOrNull() ?: DEFAULT_PRE_INTERVAL_MULTIPLIER,
            postIntervalMultiplier = postIntervalMultiplier?.toFloatOrNull() ?: DEFAULT_POST_INTERVAL_MULTIPLIER,
        )
    }

    fun toSettingsMap(): Map<String, String> {
        return mapOf(
            SettingKeys.PLAYBACK_SPEED to playbackSpeed.toString(),
            SettingKeys.SENTENCE_REPUTATION to sentenceReputation.toString(),
            SettingKeys.PRE_INTERVAL_MULTIPLIER to preIntervalMultiplier.toString(),
            SettingKeys.POST_INTERVAL_MULTIPLIER to postIntervalMultiplier.toString(),
        )
    }
}
