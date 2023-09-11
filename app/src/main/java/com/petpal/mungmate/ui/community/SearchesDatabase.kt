package com.petpal.mungmate.ui.community

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SearchesEntity::class], version = 1)
abstract class SearchesDatabase:RoomDatabase() {

    abstract fun SearchesDao(): SearchesDao

    companion object {
        @Volatile
        private var INSTANCE: SearchesDatabase? = null

        fun getDataBase(
            context: Context
        ): SearchesDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchesDatabase::class.java,
                    "searches_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE =instance
                instance
            }
        }
    }
}