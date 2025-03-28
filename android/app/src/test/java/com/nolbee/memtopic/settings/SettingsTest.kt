package com.nolbee.memtopic.settings

import kotlinx.coroutines.runBlocking
import org.junit.Test

class SettingsTest {
    @Test
    fun testSettingsCreation(): Unit = runBlocking {
        var settings = Settings.fromStrings(null, null, null, null)
        assert(settings.playbackSpeed == 1.0f)
        assert(settings.sentenceReputation == 2)
        assert(settings.preIntervalMultiplier == 0.0f)
        assert(settings.postIntervalMultiplier == 1.0f)
        settings = Settings.fromStrings("2.0", "5", "0.5", "2.0")
        assert(settings.playbackSpeed == 2.0f)
        assert(settings.sentenceReputation == 5)
        assert(settings.preIntervalMultiplier == 0.5f)
        assert(settings.postIntervalMultiplier == 2.0f)
        val settingsMap = settings.toSettingsMap()
        assert(settingsMap[SettingKeys.PLAYBACK_SPEED] == "2.0")
        assert(settingsMap[SettingKeys.SENTENCE_REPUTATION] == "5")
        assert(settingsMap[SettingKeys.PRE_INTERVAL_MULTIPLIER] == "0.5")
        assert(settingsMap[SettingKeys.POST_INTERVAL_MULTIPLIER] == "2.0")
        settings = Settings.fromStrings("a", "b", "c", "d")
        assert(settings.playbackSpeed == 1.0f)
        assert(settings.sentenceReputation == 2)
        assert(settings.preIntervalMultiplier == 0.0f)
        assert(settings.postIntervalMultiplier == 1.0f)
    }
}
