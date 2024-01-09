package com.no5ing.bbibbi.data.datasource.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.data.model.member.Member
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataStorage @Inject constructor(val context: Context) {
    private val spec = KeyGenParameterSpec.Builder(
        MasterKey.DEFAULT_MASTER_KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(MasterKey.DEFAULT_AES_GCM_MASTER_KEY_SIZE)
        .build()

    private val masterKey = MasterKey.Builder(context)
        .setKeyGenParameterSpec(spec)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        "Encrypted_Shared_Preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        const val AUTH_RESULT_KEY = "auth_result_key"
        const val ME_KEY = "me_key"
        const val REGISTRATION_TOKEN_KEY = "registration_token"
        const val LANDING_SEEN_KEY = "landing_seen"
    }

    fun login(member: Member, authToken: AuthResult) {
        Timber.d("[LocalDataSource] logged in with ${authToken.accessToken}")
        setAuthTokens(authToken)
        setMe(member)
    }

    fun logOut() {
        Timber.d("[LocalDataSource] logged out")
        clearPreference()
    }

    private fun clearPreference() {
        Timber.d("[LocalDataSource] clear internal preferences")
        val editor = preferences.edit()

        editor.remove(AUTH_RESULT_KEY)
        editor.remove(ME_KEY)
        editor.remove(REGISTRATION_TOKEN_KEY)
        editor.apply()
        editor.commit()
    }

    fun getAuthTokens(): AuthResult? {
        val json = preferences.getString(AUTH_RESULT_KEY, null)
        return json?.let { Gson().fromJson(it, AuthResult::class.java) }
    }

    fun setAuthTokens(authResult: AuthResult) {
        val editor = preferences.edit()
        val json = Gson().toJson(authResult)

        editor.putString(AUTH_RESULT_KEY, json)
        editor.apply()
        editor.commit()
    }

    fun getMe(): Member? {
        val json = preferences.getString(ME_KEY, null)
        return json?.let { Gson().fromJson(it, Member::class.java) }
    }

    fun setMe(member: Member) {
        val editor = preferences.edit()
        val json = Gson().toJson(member)

        editor.putString(ME_KEY, json)
        editor.apply()
        editor.commit()
    }

    fun setRegistrationToken(token: String) {
        val editor = preferences.edit()
        editor.putString(REGISTRATION_TOKEN_KEY, token)
        editor.apply()
        editor.commit()
    }

    fun getAndDeleteRegistrationToken(): String? {
        val token = preferences.getString(REGISTRATION_TOKEN_KEY, null)
        if (token != null) {
            val editor = preferences.edit()
            editor.remove(REGISTRATION_TOKEN_KEY)
            editor.apply()
            editor.commit()
        }
        return token
    }

    fun hasRegistrationToken(): Boolean {
        return preferences.contains(REGISTRATION_TOKEN_KEY)
    }

    fun setLandingSeen() {
        val editor = preferences.edit()
        editor.putBoolean(LANDING_SEEN_KEY, true)
        editor.apply()
        editor.commit()
    }

    fun getLandingSeen(): Boolean {
        return preferences.getBoolean(LANDING_SEEN_KEY, false)
    }
}