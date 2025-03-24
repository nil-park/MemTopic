package com.nolbee.memtopic.database

import com.nolbee.memtopic.settings.Preferences
import com.nolbee.memtopic.settings.SettingKeys

class SettingsRepository(private val dao: SettingsDao) {
    suspend fun getPreferences(): Preferences {
        val settingsMap = dao.getAllSettings().associate { it.settingKey to it.settingValue }
        return Preferences(
            playbackSpeed = settingsMap[SettingKeys.PLAYBACK_SPEED],
            sentenceReputation = settingsMap[SettingKeys.SENTENCE_REPUTATION],
            intervalTimeMultiplier = settingsMap[SettingKeys.INTERVAL_TIME_MULTIPLIER],
            intervalPosition = settingsMap[SettingKeys.INTERVAL_POSITION]
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
