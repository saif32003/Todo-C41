package com.route.todoc41.ui.home.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.Fragment
import com.route.todoc41.R
import com.route.todoc41.databinding.FragmentSettingsBinding
import com.route.todoc41.ui.util.Constants
import com.route.todoc41.ui.util.applyModeChange

class SettingsFragment:Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCES , Context.MODE_PRIVATE)

        setUpListeners()
    }

    override fun onStart() {
        super.onStart()
        initializeUI()
        setLanguageDropDownMenue()
        setModeDropDownMenue()
    }

    private fun setModeDropDownMenue() {
        val modes = resources.getStringArray(R.array.modes).toList()
        val adapter = ArrayAdapter(requireContext() , R.layout.drop_down_item , modes)
        binding.modesAutoCompleteTV.setAdapter(adapter)
    }

    private fun setLanguageDropDownMenue() {
        val languages = resources.getStringArray(R.array.languages).toList()
        val adapter = ArrayAdapter(requireContext() , R.layout.drop_down_item , languages)
        binding.languagesAutoCompleteTV.setAdapter(adapter)
    }

    private fun setUpListeners() {
        setLanguageDropDownMenueListener()
        setModeDropDownMenueListener()

    }

    private fun setLanguageDropDownMenueListener() {
        binding.languagesAutoCompleteTV.setOnItemClickListener { _, _, position, _ ->
            val selectedLanguage = binding.languagesAutoCompleteTV.adapter.getItem(position).toString()
            binding.languagesAutoCompleteTV.setText(selectedLanguage)
            val languageCode = when(selectedLanguage){
                getString(R.string.english) -> Constants.ENGLISH_CODE
                getString(R.string.arabic) -> Constants.ARABIC_CODE
                else -> Constants.ENGLISH_CODE
            }
            applyLanguageChange(languageCode)
        }
    }

    private fun applyLanguageChange(languageCode: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }

    private fun setModeDropDownMenueListener() {
        binding.modesAutoCompleteTV.setOnItemClickListener { _, _, position, _ ->
            val selectedMode = binding.modesAutoCompleteTV.adapter.getItem(position).toString()
            binding.modesAutoCompleteTV.setText(selectedMode)
            val isDark = selectedMode == getString(R.string.dark)
            applyModeChange(isDark)
            saveModeToSharedPreferences(isDark)
        }
    }

    private fun saveModeToSharedPreferences(isDark: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.IS_DARK_MODE , isDark)
        editor.apply()
    }

    private fun initializeUI() {
        setInitialLanguageState()
        setInitialModeState()
    }

    private fun setInitialModeState() {
        val currentDeviceMode = AppCompatDelegate.getDefaultNightMode()
        val mode = when(currentDeviceMode){
            AppCompatDelegate.MODE_NIGHT_YES -> R.string.dark
            else -> R.string.light
        }
        binding.modesAutoCompleteTV.setText(mode)
    }

    private fun setInitialLanguageState() {
        val currentLanguageCode = AppCompatDelegate.getApplicationLocales()[0]?.language ?: getCurrentDeviceLanguageCode()
        val language = when(currentLanguageCode){
            Constants.ENGLISH_CODE -> R.string.english
            Constants.ARABIC_CODE-> R.string.arabic
            else -> R.string.english
        }
        binding.languagesAutoCompleteTV.setText(language)
    }

    private fun getCurrentDeviceLanguageCode() : String{
        return resources.configuration.locales[0].language
    }


}