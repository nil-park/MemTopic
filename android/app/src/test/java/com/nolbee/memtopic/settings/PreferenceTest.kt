package com.nolbee.memtopic.settings

import kotlinx.coroutines.runBlocking
import org.junit.Test

class PreferenceTest {
    @Test
    fun testPreferencesCreation(): Unit = runBlocking {
        var prefs = Preferences.fromStrings(null, null, null, null)
        assert(prefs.playbackSpeed == 1.0f)
        assert(prefs.sentenceReputation == 2)
        assert(prefs.preIntervalMultiplier == 0.0f)
        assert(prefs.postIntervalMultiplier == 1.0f)
        prefs = Preferences.fromStrings("2.0", "5", "0.5", "2.0")
        assert(prefs.playbackSpeed == 2.0f)
        assert(prefs.sentenceReputation == 5)
        assert(prefs.preIntervalMultiplier == 0.5f)
        assert(prefs.postIntervalMultiplier == 2.0f)
        val settingsMap = prefs.toSettingsMap()
        assert(settingsMap[SettingKeys.PLAYBACK_SPEED] == "2.0")
        assert(settingsMap[SettingKeys.SENTENCE_REPUTATION] == "5")
        assert(settingsMap[SettingKeys.PRE_INTERVAL_MULTIPLIER] == "0.5")
        assert(settingsMap[SettingKeys.POST_INTERVAL_MULTIPLIER] == "2.0")
        prefs = Preferences.fromStrings("a", "b", "c", "d")
        assert(prefs.playbackSpeed == 1.0f)
        assert(prefs.sentenceReputation == 2)
        assert(prefs.preIntervalMultiplier == 0.0f)
        assert(prefs.postIntervalMultiplier == 1.0f)
    }
}
