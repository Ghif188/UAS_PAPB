package com.example.uaspapb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "local_film")
data class Film (
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String = "",
    @ColumnInfo(name = "nama_film")
    var nama_film: String = "",
    @ColumnInfo(name = "genre")
    var genre: String = "",
    @ColumnInfo(name = "desc")
    var desc: String = "",
    @ColumnInfo(name = "director")
    var director: String = "",
    @ColumnInfo(name = "rate")
    var rate: String = "",
    @ColumnInfo(name = "poster")
    var poster: String = ""
) : Serializable