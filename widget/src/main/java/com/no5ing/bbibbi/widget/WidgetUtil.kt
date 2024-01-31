package com.no5ing.bbibbi.widget

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlin.streams.toList

fun String.toCodePointList() = codePoints().toList().map { String(Character.toChars(it)) }
fun String.codePointLength() = codePoints().count()

fun getCurrentHour() = System.currentTimeMillis() / 1000 / 60 / 60 % 24

fun getAuthTokenFromContext(context: Context): String? {
    val preferences = EncryptedSharedPreferences.create(
        context,
        "Encrypted_Shared_Preferences",
        MasterKey.Builder(context)
            .setKeyGenParameterSpec(KeyGenParameterSpec.Builder(
                MasterKey.DEFAULT_MASTER_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
                .build())
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    val json = preferences.getString("auth_result_key", null)
    return runCatching {
        JsonParser.parseString(json).asJsonObject.get("accessToken").asString
    }.getOrElse { null }
}