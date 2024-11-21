package com.cs460.finalprojectfirstdraft.utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Manager class to make storing shared prefs easier
 */
public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor
     * @param context context to get shared prefs from
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Put a boolean value using a String key in the shared prefs
     * @param key String key
     * @param value boolean value
     */
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a boolean value for a given key
     * @param key String key associated to desired value
     * @return boolean value
     */
    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Put a String value using a String key in the shared prefs
     * @param key String key
     * @param value String value
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a String value for a given key
     * @param key String key associated to desired value
     * @return String value
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * Clear the shared prefs
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

