package com.nolbee.memtopic.security

import android.content.SharedPreferences

class SecureSharedPreferences(private val sharedPref: SharedPreferences) {
    fun contains(key: String) = sharedPref.contains(key)
    fun get(key: String, defaultValue: Boolean): Boolean = getInternal(key, defaultValue)
    fun get(key: String, defaultValue: Int): Int = getInternal(key, defaultValue)
    fun get(key: String, defaultValue: Long): Long = getInternal(key, defaultValue)
    fun get(key: String, defaultValue: String): String = getInternal(key, defaultValue)
    fun put(key: String, value: Boolean) = putInternal(key, value)
    fun put(key: String, value: Int) = putInternal(key, value)
    fun put(key: String, value: Long) = putInternal(key, value)
    fun put(key: String, value: String) = putInternal(key, value)

    private fun <T : Any> getInternal(key: String, defaultValue: T): T {
        val str = sharedPref.getString(key, "")
        if (str.isNullOrEmpty()) {
            return defaultValue
        }
        @Suppress("PlatformExtensionReceiverOfInline", "UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        return when (defaultValue) {
            is Boolean -> str.toBoolean()
            is Int -> str.toInt()
            is Long -> str.toLong()
            is String -> str
            else -> throw IllegalArgumentException("defaultValue only could be one of these types: Boolean, Int, Long, String")
        } as T
    }

    private fun putInternal(key: String, value: Any?) {
        try {
            sharedPref.edit().run {
                if (value == null) {
                    remove(key)
                } else {
                    putString(key, value.toString())
                }
                apply()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
