package com.example.gpstracker.ui

import android.graphics.Color
import android.os.Bundle
import androidx.preference.Preference

import androidx.preference.PreferenceFragmentCompat
import com.example.gpstracker.R

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var updateLocPref: Preference
    private lateinit var updateColorPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
        init()
    }

    private fun init() {
        updateLocPref = findPreference("update_loc_time")!!
        updateColorPref = findPreference("update_track_color")!!
        updateLocPref.onPreferenceChangeListener = onChangeListener()
        updateColorPref.onPreferenceChangeListener = onChangeListener()
        initSharedPref()
    }

    private fun onChangeListener(): Preference.OnPreferenceChangeListener {
        return Preference.OnPreferenceChangeListener { pref, value ->
            when (pref.key) {
                "update_loc_time" -> onUpdateLocTime(value.toString())
                "update_track_color" -> pref.icon?.setTint(Color.parseColor(value.toString()))
            }
            true
        }
    }

    private fun onUpdateLocTime(value: String) {
        val nameArray = resources.getStringArray(R.array.update_loc_time_name)
        val valueArray = resources.getStringArray(R.array.update_loc_time_value)
        updateLocPref.title = "${updateLocPref.title.toString().substringBefore(":")}: ${
            nameArray[valueArray.indexOf(value)]
        }"
    }

    private fun initSharedPref() {
        val sharedPref = updateLocPref.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.update_loc_time_name)
        val valueArray = resources.getStringArray(R.array.update_loc_time_value)
        updateLocPref.title = "${updateLocPref.title}: ${
            nameArray[valueArray.indexOf(
                sharedPref?.getString(
                    "update_loc_time",
                    "3000"
                )
            )]
        }"

        val trackColor = sharedPref?.getString("update_track_color", "#FF000000")
        updateColorPref.icon?.setTint(Color.parseColor(trackColor))
    }

}