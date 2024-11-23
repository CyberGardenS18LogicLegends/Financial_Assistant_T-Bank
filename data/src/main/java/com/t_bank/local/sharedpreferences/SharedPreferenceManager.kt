package com.t_bank.local.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import com.t_bank.common_models.UserDataClass
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SharedPreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val PREFS_NAME = "UserDataPrefs"
        private const val KEY_USER_NAME = "Name"
        private const val KEY_USER_PHOTO = "Photo"
        private const val KEY_EMAIL = "Email"
        private const val KEY_PASSWORD = "Password"
    }

    private val securePreferencesManager: SecurePreferencesManager = SecurePreferencesManager(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isUserRegistered(): Boolean {
        val email = sharedPreferences.getString(KEY_EMAIL, null)
        val password = securePreferencesManager.getString(KEY_PASSWORD, null)
        return email != null && password != null
    }

    fun updateUserName(userName: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_USER_NAME, userName)
            apply()
        }
    }

    fun getUserName(): String {
        return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
    }

    fun saveUserPhotoUri(filePath: String?) {
        sharedPreferences.edit().putString(KEY_USER_PHOTO, filePath).apply()
    }

    fun getUserPhotoUri(): String? {
        return sharedPreferences.getString(KEY_USER_PHOTO, null)
    }

    fun saveAuthData(userData: UserDataClass) {
        with(sharedPreferences.edit()) {
            putString(KEY_EMAIL, userData.login)
            putString(KEY_USER_NAME, userData.login.substringBefore('@'))
            apply()
        }

        securePreferencesManager.putString(KEY_PASSWORD, userData.password)
    }

    fun getUserData(): UserDataClass {
        return UserDataClass(
            login = sharedPreferences.getString(KEY_EMAIL, "") ?: "",
            password = securePreferencesManager.getString(KEY_PASSWORD, "") ?: ""
        )
    }


    fun clearSharedPreference() {
        sharedPreferences.edit().clear().apply()
        securePreferencesManager.clear()
    }


}