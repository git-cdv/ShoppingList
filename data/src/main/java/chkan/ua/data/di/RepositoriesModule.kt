package chkan.ua.data.di

import chkan.ua.data.repos.HistoryRepositoryImpl
import chkan.ua.data.repos.ItemsRepositoryImpl
import chkan.ua.data.repos.ListsRepositoryImpl
import chkan.ua.data.repos.RemoteRepositoryImpl
import chkan.ua.domain.repos.HistoryRepository
import chkan.ua.domain.repos.ItemsRepository
import chkan.ua.domain.repos.ListsRepository
import chkan.ua.domain.repos.RemoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {
    @Binds
    fun listsRepository(impl: ListsRepositoryImpl) : ListsRepository

    @Binds
    fun itemsRepository(impl: ItemsRepositoryImpl) : ItemsRepository

    @Binds
    fun historyRepository(impl: HistoryRepositoryImpl) : HistoryRepository

    @Binds
    fun remoteRepository(impl: RemoteRepositoryImpl) : RemoteRepository
}