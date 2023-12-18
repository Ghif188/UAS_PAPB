package com.example.uaspapb

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Film::class], version = 1, exportSchema = false)
abstract class FilmRoomDatabase : RoomDatabase(){
    abstract fun filmDao(): FilmDao

    companion object {
        @Volatile
        private var INSTANCE: FilmRoomDatabase? = null
        fun getDatabase(context: Context): FilmRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(FilmRoomDatabase::class.java) {
                    INSTANCE = databaseBuilder(
                        context.applicationContext,
                        FilmRoomDatabase::class.java, "local_film"
                    )
                        .build()
                }
            }
            return INSTANCE
        }
    }
}