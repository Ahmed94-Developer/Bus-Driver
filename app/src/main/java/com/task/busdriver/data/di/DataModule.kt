package com.task.busdriver.data.di
import android.content.Context
import androidx.room.Room
import com.task.busdriver.data.local.dao.UserDao
import com.task.busdriver.data.local.db.AppDatabase
import com.task.busdriver.data.repositoryImpl.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "room_db"
        ).build()
    }
    @Provides
    @Singleton
    fun provideDao(appDatabase: AppDatabase) : UserDao{
        return appDatabase.userDao();
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: UserDao): RepositoryImpl {
        return RepositoryImpl(taskDao)
    }

}