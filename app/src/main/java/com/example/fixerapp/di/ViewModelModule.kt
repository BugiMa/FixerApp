package com.example.fixerapp.di

import com.example.fixerapp.api.FixerApi
import com.example.fixerapp.repo.FixerAppRepository
import com.example.fixerapp.repo.IFixerAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @ViewModelScoped
    @Provides
    fun provideRepository(
        api: FixerApi
    ) = FixerAppRepository(api) as IFixerAppRepository
}