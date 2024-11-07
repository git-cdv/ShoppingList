package chkan.ua.data.di

import chkan.ua.data.repos.ListsRepositoryImpl
import chkan.ua.domain.repos.ListsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoriesModule {
    @Binds
    fun listsRepository(impl: ListsRepositoryImpl) : ListsRepository
}