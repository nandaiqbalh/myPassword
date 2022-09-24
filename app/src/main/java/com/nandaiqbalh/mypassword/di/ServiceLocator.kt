package com.nandaiqbalh.mypassword.di

import android.content.Context
import com.nandaiqbalh.mypassword.data.local.preference.UserPreference
import com.nandaiqbalh.mypassword.data.local.preference.UserPreferenceDataSource
import com.nandaiqbalh.mypassword.data.local.preference.UserPreferenceDataSourceImpl
import com.nandaiqbalh.mypassword.data.local.repository.LocalRepository
import com.nandaiqbalh.mypassword.data.local.repository.LocalRepositoryImpl

object ServiceLocator {

    fun providerUserPreference(context: Context): UserPreference{
        return UserPreference(context)
    }

    fun providerUserPreferenceDataSource(context: Context): UserPreferenceDataSource{
        return UserPreferenceDataSourceImpl(providerUserPreference(context))
    }

    fun providerLocalRepository(context: Context): LocalRepository{
        return LocalRepositoryImpl(providerUserPreferenceDataSource(context))
    }
}