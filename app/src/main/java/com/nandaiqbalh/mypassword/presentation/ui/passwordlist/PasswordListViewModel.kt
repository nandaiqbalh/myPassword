package com.nandaiqbalh.mypassword.presentation.ui.passwordlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import com.nandaiqbalh.mypassword.data.repository.LocalRepository
import com.nandaiqbalh.mypassword.wrapper.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PasswordListViewModel(private val repository: LocalRepository) : ViewModel() {

    val passwordListResult = MutableLiveData<Resource<List<PasswordEntity>>>()

    val deleteResult = MutableLiveData<Resource<Number>>()

    fun checkIfAppKeyIsExist(): Boolean {
        return repository.checkIfAppKeyIsExist()
    }

    fun getPasswordList() {
        viewModelScope.launch {
            passwordListResult.postValue(Resource.Loading())
            delay(1000)
            passwordListResult.postValue(repository.getPasswordList())
        }
    }

    fun deletePassword(item: PasswordEntity) {
        viewModelScope.launch {
            deleteResult.postValue(Resource.Loading())
            delay(1000)
            deleteResult.postValue(repository.deletePassword(item))
        }
    }


}