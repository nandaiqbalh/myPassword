package com.nandaiqbalh.mypassword.di

import android.content.Context
import com.nandaiqbalh.mypassword.data.local.database.AppDatabase
import com.nandaiqbalh.mypassword.data.local.database.dao.PasswordDao
import com.nandaiqbalh.mypassword.data.local.database.datasource.PasswordDataSource
import com.nandaiqbalh.mypassword.data.local.database.datasource.PasswordDataSourceImpl
import com.nandaiqbalh.mypassword.data.local.preference.UserPreference
import com.nandaiqbalh.mypassword.data.local.preference.UserPreferenceDataSource
import com.nandaiqbalh.mypassword.data.local.preference.UserPreferenceDataSourceImpl
import com.nandaiqbalh.mypassword.data.repository.LocalRepository
import com.nandaiqbalh.mypassword.data.repository.LocalRepositoryImpl

object ServiceLocator {

    fun provideUserPreference(context: Context): UserPreference {
        return UserPreference(context)
    }

    fun provideUserPreferenceDataSource(context: Context): UserPreferenceDataSource {
        return UserPreferenceDataSourceImpl(provideUserPreference(context))
    }

    fun provideAppDatabase(appContext: Context): AppDatabase {
        return AppDatabase.getInstance(appContext)
    }

    fun providePasswordDao(appContext: Context): PasswordDao {
        return provideAppDatabase(appContext).passwordDao()
    }

    fun providePasswordDataSource(appContext: Context): PasswordDataSource {
        return PasswordDataSourceImpl(providePasswordDao(appContext))
    }

    fun provideLocalRepository(context: Context): LocalRepository {
        return LocalRepositoryImpl(
            provideUserPreferenceDataSource(context),
            providePasswordDataSource(context)
        )
    }

}
