package com.example.notes.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM NOTES")
    fun getAllNote(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM NOTES WHERE title LIKE :searchQuery OR body LIKE :searchQuery")
    suspend fun getAllNote(searchQuery: String): List<NoteEntity>

    @Insert
    suspend fun insertNote(noteEntity: NoteEntity)

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)
}