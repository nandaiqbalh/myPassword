package com.nandaiqbalh.mypassword.data.local.preference

interface UserPreferenceDataSource {

    fun getUserAppKey(): String?
    fun setUserAppKey(newAppKey: String)
}

class UserPreferenceDataSourceImpl(private val userPreference: UserPreference) :
    UserPreferenceDataSource {
    override fun getUserAppKey(): String? {
        return userPreference.appKey
    }

    override fun setUserAppKey(newAppKey: String) {
        userPreference.appKey = newAppKey
    }

}