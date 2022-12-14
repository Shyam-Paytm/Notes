package com.example.notes.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "user_id")
    var userId: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "body")
    var body: String,
    @ColumnInfo(name = "modified")
    var modified: Boolean = false,
    @ColumnInfo(name = "fireStore_id")
    var fireStoreId: String? = null
) : Serializable