package com.nolbee.memtopic.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAKeyGenParameterSpec.F4
import javax.crypto.Cipher

object AndroidRsaCipherHelper {
    private const val KEY_LENGTH_BIT = 2048
    private const val KEY_PROVIDER_NAME = "AndroidKeyStore"
    private const val CIPHER_ALGORITHM =
        "${KeyProperties.KEY_ALGORITHM_RSA}/" +
        "${KeyProperties.BLOCK_MODE_ECB}/" +
        KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1

    private lateinit var keyEntry: KeyStore.Entry

    @Suppress("ObjectPropertyName")
    private var _isSupported = false

    private val isSupported: Boolean
        get() = _isSupported

    private lateinit var appContext: Context

    internal fun init(applicationContext: Context) {
        if (isSupported) {
            return
        }

        appContext = applicationContext
        val alias = "${appContext.packageName}.rsakeypairs"
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        val result: Boolean = if (keyStore.containsAlias(alias)) {
            true
        } else {
            initAndroidM(alias)
        }

        keyEntry = keyStore.getEntry(alias, null)
        _isSupported = result
    }

    private fun initAndroidM(alias: String): Boolean {
        try {
            with(KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER_NAME)) {
                val spec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(KEY_LENGTH_BIT, F4))
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setDigests(
                        KeyProperties.DIGEST_SHA512,
                        KeyProperties.DIGEST_SHA384,
                        KeyProperties.DIGEST_SHA256
                    )
                    .setUserAuthenticationRequired(false)
                    .build()
                initialize(spec)
                generateKeyPair()
            }

            return true
        } catch (e: GeneralSecurityException) {
            return false
        }
    }

    fun encrypt(plainText: String): String {
        if (!_isSupported) {
            return plainText
        }

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.ENCRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).certificate.publicKey)
        }
        val bytes = plainText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = cipher.doFinal(bytes)
        val base64EncryptedBytes = Base64.encode(encryptedBytes, Base64.DEFAULT)

        return String(base64EncryptedBytes)
    }

    fun decrypt(base64EncryptedCipherText: String): String {
        if (!_isSupported) {
            return base64EncryptedCipherText
        }

        val cipher = Cipher.getInstance(CIPHER_ALGORITHM).apply {
            init(Cipher.DECRYPT_MODE, (keyEntry as KeyStore.PrivateKeyEntry).privateKey)
        }
        val base64EncryptedBytes = base64EncryptedCipherText.toByteArray(Charsets.UTF_8)
        val encryptedBytes = Base64.decode(base64EncryptedBytes, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
}
