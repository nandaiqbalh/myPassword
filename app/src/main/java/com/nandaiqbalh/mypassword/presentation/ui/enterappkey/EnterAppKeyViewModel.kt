package com.nandaiqbalh.mypassword.presentation.ui.enterappkey

import androidx.lifecycle.ViewModel
import com.nandaiqbalh.mypassword.data.local.repository.LocalRepository

class EnterAppKeyViewModel(private val repository: LocalRepository) : ViewModel() {
    fun checkIsAppKeyCorrect(appKey: String): Boolean {
        return repository.checkIsAppKeyCorrect(appKey)
    }
}