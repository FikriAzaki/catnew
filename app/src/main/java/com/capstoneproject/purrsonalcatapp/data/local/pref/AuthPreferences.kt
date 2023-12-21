package com.capstoneproject.purrsonalcatapp.data.local.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences constructor(private val dataStore: DataStore<Preferences>) {

    val authToken: kotlinx.coroutines.flow.Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_AUTH_TOKEN]
    }

    suspend fun saveAuthToken(token: String?) {
        dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token ?: ""
            Log.d("TOKEN STORAGE", "Token save successfully: $token")
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_AUTH_TOKEN)
            Log.d("Logout", "Token has been removed")
        }
    }

    val username: Flow<String?>
        get() = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[KEY_USERNAME]
            }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USERNAME] = username
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthPreferences(dataStore).also { INSTANCE = it }
            }
        }

        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USERNAME = stringPreferencesKey("key_username")
    }
}