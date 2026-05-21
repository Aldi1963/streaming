package com.sonzaix.streaming.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    private object Keys {
        val PROVIDER = stringPreferencesKey("provider")
        val LANGUAGE = stringPreferencesKey("language")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val provider: Flow<String> = context.dataStore.data.map { it[Keys.PROVIDER] ?: "melolo" }
    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "id" }
    val themeMode: Flow<String> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "dark" }

    suspend fun setProvider(value: String) {
        context.dataStore.edit { it[Keys.PROVIDER] = value }
    }

    suspend fun setLanguage(value: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = value }
    }

    suspend fun setThemeMode(value: String) {
        context.dataStore.edit { it[Keys.THEME_MODE] = value }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
