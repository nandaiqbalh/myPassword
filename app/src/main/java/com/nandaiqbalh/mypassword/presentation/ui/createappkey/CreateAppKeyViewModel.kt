package com.nandaiqbalh.mypassword.presentation.ui.createappkey

import androidx.lifecycle.ViewModel
import com.nandaiqbalh.mypassword.data.local.repository.LocalRepository

class CreateAppKeyViewModel(private val repository: LocalRepository) : ViewModel() {
    fun setAppKey(newAppKey: String){
        repository.setAppKey(newAppKey)
    }
}
