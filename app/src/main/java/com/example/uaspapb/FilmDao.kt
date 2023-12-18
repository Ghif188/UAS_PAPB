package com.example.uaspapb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilmDao {
    @Insert
    fun insert(film: Film)
    @Delete
    fun delete(film: Film)

    @Query("SELECT * from local_film")
    fun getAllFilm(): List<Film>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Film>)

    @Query("DELETE FROM local_film")
    fun deleteAll()
}