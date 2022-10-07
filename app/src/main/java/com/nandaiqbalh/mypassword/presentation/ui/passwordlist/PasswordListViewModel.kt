package com.nandaiqbalh.mypassword.presentation.ui.passwordlist

import androidx.lifecycle.ViewModel
import com.nandaiqbalh.mypassword.data.repository.LocalRepository

class PasswordListViewModel(private val repository: LocalRepository) : ViewModel() {

    fun checkIfAppKeyIsExist(): Boolean {
        return repository.checkIfAppKeyIsExist()
    }

}