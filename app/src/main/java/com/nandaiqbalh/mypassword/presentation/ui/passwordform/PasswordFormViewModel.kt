package com.nandaiqbalh.mypassword.presentation.ui.passwordform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nandaiqbalh.mypassword.data.local.database.entity.PasswordEntity
import com.nandaiqbalh.mypassword.data.repository.LocalRepository
import com.nandaiqbalh.mypassword.wrapper.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PasswordFormViewModel(private val repository: LocalRepository) : ViewModel() {

    val detailDataResult = MutableLiveData<Resource<PasswordEntity?>>()
    val insertResult = MutableLiveData<Resource<Number>>()
    val updateResult = MutableLiveData<Resource<Number>>()

    fun getPasswordById(id: Int) {
        viewModelScope.launch {
            detailDataResult.postValue(Resource.Loading())
            delay(1000)
            detailDataResult.postValue(repository.getPasswordById(id))
        }
    }

    fun insertNewPassword(item: PasswordEntity) {
        viewModelScope.launch {
            insertResult.postValue(Resource.Loading())
            delay(1000)
            insertResult.postValue(repository.insertPassword(item))
        }
    }

    fun updatePassword(item: PasswordEntity) {
        viewModelScope.launch {
            updateResult.postValue(Resource.Loading())
            delay(1000)
            updateResult.postValue(repository.updatePassword(item))
        }
    }

}