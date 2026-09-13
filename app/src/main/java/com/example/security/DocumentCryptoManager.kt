package com.example.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object DocumentCryptoManager {
    private const val KEY_ALIAS = "PrayagiDocumentMasterKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
    private const val GCM_TAG_LENGTH = 128

    init {
        createKeyIfNeeded()
    }

    private fun createKeyIfNeeded() {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(ALGORITHM, ANDROID_KEYSTORE)
                val spec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(BLOCK_MODE)
                    .setEncryptionPaddings(PADDING)
                    .setKeySize(256)
                    .build()
                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun encryptFile(inputFile: File, outputFile: File): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey())
        }
        val iv = cipher.iv
        val fileBytes = if (inputFile.exists()) inputFile.readBytes() else byteArrayOf()
        val encryptedBytes = cipher.doFinal(fileBytes)

        outputFile.writeBytes(iv + encryptedBytes)
        return iv
    }

    fun decryptFile(encryptedFile: File, outputFile: File) {
        val allBytes = encryptedFile.readBytes()
        if (allBytes.size < 12) return
        val iv = allBytes.copyOfRange(0, 12)
        val encryptedPayload = allBytes.copyOfRange(12, allBytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(), GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        val decryptedBytes = cipher.doFinal(encryptedPayload)
        outputFile.writeBytes(decryptedBytes)
    }
}
