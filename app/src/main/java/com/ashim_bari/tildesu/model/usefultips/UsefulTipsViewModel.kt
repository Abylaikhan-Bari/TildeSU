package com.ashim_bari.tildesu.model.usefultips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsefulTipsViewModel : ViewModel() {

    private val repository = UsefulTipsRepository()
    private val _usefulTips = MutableStateFlow<List<UsefulTip>>(emptyList())
    val usefulTips: StateFlow<List<UsefulTip>> = _usefulTips

    init {
        fetchUsefulTips()
    }

    private fun fetchUsefulTips() {
        viewModelScope.launch {
            repository.getUsefulTips().collect {
                _usefulTips.value = it
            }
        }
    }
}
