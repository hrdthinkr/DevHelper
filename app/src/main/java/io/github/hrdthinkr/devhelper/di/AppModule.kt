package io.github.hrdthinkr.devhelper.di

import ToolsRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.hrdthinkr.devhelper.data.repository.JsonToKotlinGeneratorImpl
import io.github.hrdthinkr.devhelper.domain.generator.JsonToKotlinGenerator
import io.github.hrdthinkr.devhelper.domain.repository.ToolsRepo
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideToolsRepo(): ToolsRepo =
        ToolsRepoImpl()

    @Provides
    @Singleton
    fun provideJsonToKotlinGenerator(): JsonToKotlinGenerator =
        JsonToKotlinGeneratorImpl()
}
