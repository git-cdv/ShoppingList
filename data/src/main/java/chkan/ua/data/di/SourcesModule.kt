package chkan.ua.data.di

import chkan.ua.data.sources.DataSource
import chkan.ua.data.sources.room.RoomSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SourcesModule {
    @Binds
    fun roomSource(impl: RoomSourceImpl) : DataSource
}