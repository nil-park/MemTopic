package com.nolbee.memtopic.account_view

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecureKeyValueStore(context: Context) {
    private val sharedPreferencesName = "MemTopicPrefs"
    private val androidKeyStore = "AndroidKeyStore"
    private val keyAlias = "com.nolbee.memtopic"
    private val cipherTransformation = "AES/GCM/NoPadding"
    private val keyStore = KeyStore.getInstance(androidKeyStore).apply { load(null) }
    private val secretKey: SecretKey
    private val sharedPreferences =
        context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    init {
        if (!keyStore.containsAlias(keyAlias)) {
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeyStore)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()

            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
        secretKey = keyStore.getKey(keyAlias, null) as SecretKey
    }

    fun set(key: String, data: String) {
        val cipher = Cipher.getInstance(cipherTransformation)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedData = cipher.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        val iv = cipher.iv

        sharedPreferences.edit()
            .putString("$key.enc", Base64.encodeToString(encryptedData, Base64.DEFAULT))
            .putString("$key.iv", Base64.encodeToString(iv, Base64.DEFAULT))
            .apply()
    }

    fun get(key: String): String? {
        val encString = sharedPreferences.getString("$key.enc", null) ?: return null
        val ivString = sharedPreferences.getString("$key.iv", null) ?: return null

        val encryptedData = Base64.decode(encString, Base64.DEFAULT)
        val iv = Base64.decode(ivString, Base64.DEFAULT)

        return try {
            val cipher = Cipher.getInstance(cipherTransformation)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decrypted = cipher.doFinal(encryptedData)
            String(decrypted, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            sharedPreferences.edit().clear().apply()
            null
        }
    }
}
