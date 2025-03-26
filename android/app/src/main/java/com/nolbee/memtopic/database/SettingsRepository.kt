package com.nolbee.memtopic.database

import com.nolbee.memtopic.settings.Preferences
import com.nolbee.memtopic.settings.SettingKeys

class SettingsRepository(private val dao: SettingsDao) {
    suspend fun getPreferences(): Preferences {
        val settingsMap = dao.getAllSettings().associate { it.settingKey to it.settingValue }
        return Preferences.fromStrings(
            playbackSpeed = settingsMap[SettingKeys.PLAYBACK_SPEED],
            sentenceReputation = settingsMap[SettingKeys.SENTENCE_REPUTATION],
            preIntervalMultiplier = settingsMap[SettingKeys.PRE_INTERVAL_MULTIPLIER],
            postIntervalMultiplier = settingsMap[SettingKeys.POST_INTERVAL_MULTIPLIER],
        )
    }

    suspend fun setPreferences(prefs: Preferences) {
        val settingsMapPrev = dao.getAllSettings().associate { it.settingKey to it.settingValue }
        val settingsMapNext = prefs.toSettingsMap()
        val entities = settingsMapNext.map { (key, value) ->
            SettingEntity(settingKey = key, settingValue = value)
        }
        dao.upsertSettings(*entities.toTypedArray())

        val keysToRemove = settingsMapPrev.keys.filter { !settingsMapNext.containsKey(it) }
        dao.deleteSettings(*keysToRemove.toTypedArray())
    }
}
