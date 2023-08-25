package com.nolbee.memtopic.security

import java.security.SecureRandom
import javax.crypto.KeyGenerator

object AndroidAesCipherHelper {
    fun generateRandomKey(lengthBits: Int): ByteArray {
        return with(KeyGenerator.getInstance("AES")) {
            init(lengthBits, SecureRandom())
            generateKey().encoded
        }
    }
}
