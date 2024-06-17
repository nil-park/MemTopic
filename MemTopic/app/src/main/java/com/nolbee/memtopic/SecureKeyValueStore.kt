package com.nolbee.memtopic

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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
        val iv = cipher.iv.joinToString(separator = ",", transform = { byte -> byte.toString() })

        sharedPreferences.edit().putString(
            "$key.enc",
            encryptedData.joinToString(separator = ",", transform = { byte -> byte.toString() })
        )
            .putString("$key.iv", iv)
            .apply()
    }

    fun get(key: String): String? {
        val encryptedData =
            sharedPreferences.getString("$key.enc", null)?.split(",")?.map { it.toByte() }
                ?.toByteArray()
        val iv = sharedPreferences.getString("$key.iv", null)?.split(",")?.map { it.toByte() }
            ?.toByteArray()

        if (encryptedData == null || iv == null) return null

        val cipher = Cipher.getInstance(cipherTransformation)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, StandardCharsets.UTF_8)
    }
}
