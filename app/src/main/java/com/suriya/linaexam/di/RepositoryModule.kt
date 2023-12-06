package com.suriya.linaexam.di

import com.suriya.linaexam.data.repository.CoinRepository
import com.suriya.linaexam.data.repository.CoinRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    fun bindsCoinRepository(impl: CoinRepositoryImpl): CoinRepository
}
