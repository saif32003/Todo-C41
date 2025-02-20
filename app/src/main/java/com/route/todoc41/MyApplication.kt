package com.route.todoc41

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.route.todoc41.database.MyDatabase
import com.route.todoc41.ui.util.Constants
import com.route.todoc41.ui.util.applyModeChange

class MyApplication:Application() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        MyDatabase.init(this)
        setNightMode()
    }

    private fun setNightMode() {
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES , Context.MODE_PRIVATE)
        val isDark = sharedPreferences.getBoolean(Constants.IS_DARK_MODE , getDeviceModeState())
        applyModeChange(isDark)
    }

    private fun getDeviceModeState(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}

