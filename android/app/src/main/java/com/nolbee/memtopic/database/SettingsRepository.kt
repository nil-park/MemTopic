package com.nolbee.memtopic.database

import com.nolbee.memtopic.settings.SettingKeys
import com.nolbee.memtopic.settings.Settings

class SettingsRepository(private val dao: SettingsDao) {
    suspend fun getSettings(): Settings {
        val settingsMap = dao.getAllSettings().associate { it.settingKey to it.settingValue }
        return Settings.fromStrings(
            playbackSpeed = settingsMap[SettingKeys.PLAYBACK_SPEED],
            sentenceReputation = settingsMap[SettingKeys.SENTENCE_REPUTATION],
            preIntervalMultiplier = settingsMap[SettingKeys.PRE_INTERVAL_MULTIPLIER],
            postIntervalMultiplier = settingsMap[SettingKeys.POST_INTERVAL_MULTIPLIER],
        )
    }

    suspend fun setSettings(settings: Settings) {
        val settingsMapPrev = dao.getAllSettings().associate { it.settingKey to it.settingValue }
        val settingsMapNext = settings.toSettingsMap()
        val entities = settingsMapNext.map { (key, value) ->
            SettingEntity(settingKey = key, settingValue = value)
        }
        dao.upsertSettings(*entities.toTypedArray())

        val keysToRemove = settingsMapPrev.keys.filter { !settingsMapNext.containsKey(it) }
        dao.deleteSettings(*keysToRemove.toTypedArray())
    }
}
