package com.ashim_bari.tildesu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LanguageViewModel : ViewModel() {
    private val _language = MutableLiveData<String>()
    val language: LiveData<String> = _language

    fun setLanguage(language: String) {
        _language.value = language
    }
}
