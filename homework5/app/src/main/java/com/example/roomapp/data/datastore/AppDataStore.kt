package com.example.roomapp.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppDataStore(private val context: Context) {
    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val SORT_TYPE_KEY = stringPreferencesKey("sort_type")
    }

    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(USER_EMAIL_KEY)
        }
    }

    suspend fun saveSortType(sortType: String) {
        context.dataStore.edit { preferences ->
            preferences[SORT_TYPE_KEY] = sortType
        }
    }

    val userId: Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_ID_KEY]
        }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }

}