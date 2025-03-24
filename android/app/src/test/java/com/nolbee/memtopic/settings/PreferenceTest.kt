package com.nolbee.memtopic.settings

import kotlinx.coroutines.runBlocking
import org.junit.Test

class PreferenceTest {
    @Test
    fun testIntervalPositionConversion(): Unit = runBlocking {
        assert(IntervalPosition.fromString("before") == IntervalPosition.BEFORE)
        assert(IntervalPosition.fromString("after") == IntervalPosition.AFTER)
        assert(IntervalPosition.fromString("Before") == IntervalPosition.BEFORE)
        assert(IntervalPosition.fromString("After") == IntervalPosition.AFTER)
        assert(IntervalPosition.fromString("xxx") == IntervalPosition.AFTER)
    }

    @Test
    fun testPreferencesCreation(): Unit = runBlocking {
        var prefs = Preferences(null, null, null, null)
        assert(prefs.playbackSpeed == 1.0f)
        assert(prefs.sentenceReputation == 2)
        assert(prefs.intervalTimeMultiplier == 1.0f)
        assert(prefs.intervalPosition == IntervalPosition.AFTER)
        prefs =  Preferences("2.0", "5", "0.5", "before")
        assert(prefs.playbackSpeed == 2.0f)
        assert(prefs.sentenceReputation == 5)
        assert(prefs.intervalTimeMultiplier == 0.5f)
        assert(prefs.intervalPosition == IntervalPosition.BEFORE)
        val settingsMap = prefs.toSettingsMap()
        assert(settingsMap[SettingKeys.PLAYBACK_SPEED] == "2.0")
        assert(settingsMap[SettingKeys.SENTENCE_REPUTATION] == "5")
        assert(settingsMap[SettingKeys.INTERVAL_TIME_MULTIPLIER] == "0.5")
        assert(settingsMap[SettingKeys.INTERVAL_POSITION] == "BEFORE")
    }
}
