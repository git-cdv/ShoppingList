package chkan.ua.data.di

import android.content.Context
import androidx.room.Room
import chkan.ua.data.sources.room.ItemsDao
import chkan.ua.data.sources.room.ListsDao
import chkan.ua.data.sources.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): RoomDatabase {
        return Room
            .databaseBuilder(
                appContext,
                RoomDatabase::class.java,
                "lists_db")
            .build()
    }

    @Provides
    fun provideListsDao(db: RoomDatabase): ListsDao {
        return db.listsDao
    }

    @Provides
    fun provideItemsDao(db: RoomDatabase): ItemsDao {
        return db.itemsDao
    }

}