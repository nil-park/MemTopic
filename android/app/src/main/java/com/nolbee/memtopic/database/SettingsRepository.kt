package com.nolbee.memtopic.database

import com.nolbee.memtopic.settings.Preferences
import com.nolbee.memtopic.settings.SettingKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingsRepository(private val dao: SettingsDao) {
    fun getPreferences(): Flow<Preferences> {
        return dao.getAllSettings().map { list ->
            val settingsMap = list.associate { it.settingKey to it.settingValue }
            Preferences(
                playbackSpeed = settingsMap[SettingKeys.PLAYBACK_SPEED],
                sentenceReputation = settingsMap[SettingKeys.SENTENCE_REPUTATION],
                preIntervalMultiplier = settingsMap[SettingKeys.PRE_INTERVAL_MULTIPLIER],
                postIntervalMultiplier = settingsMap[SettingKeys.POST_INTERVAL_MULTIPLIER],
            )
        }
    }

    suspend fun setPreferences(prefs: Preferences) {
        val settingsMapPrev =
            dao.getAllSettings().first().associate { it.settingKey to it.settingValue }
        val settingsMapNext = prefs.toSettingsMap()
        val entities = settingsMapNext.map { (key, value) ->
            SettingEntity(settingKey = key, settingValue = value)
        }
        dao.upsertSettings(*entities.toTypedArray())

        val keysToRemove = settingsMapPrev.keys.filter { !settingsMapNext.containsKey(it) }
        dao.deleteSettings(*keysToRemove.toTypedArray())
    }
}
