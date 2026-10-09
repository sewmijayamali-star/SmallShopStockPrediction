package com.ssps.stockprediction

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages the logged-in user session using DataStore Preferences.
 * Persists the userId so the user stays logged in across app restarts.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val USER_ID_KEY = longPreferencesKey("logged_in_user_id")
    }

    /**
     * Flow emitting the currently logged-in user ID, or null if not logged in.
     */
    val loggedInUserId: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    /**
     * Saves the user ID to persist the login session.
     */
    suspend fun setLoggedInUser(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    /**
     * Clears the session (logout).
     */
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }
}
