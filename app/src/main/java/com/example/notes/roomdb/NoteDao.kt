package com.example.notes.roomdb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM NOTES")
    fun getAllNote(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM NOTES WHERE user_id =:userId")
    fun getAllNoteOfUser(userId: String): LiveData<List<NoteEntity>>

    @Insert
    suspend fun insertNote(noteEntity: NoteEntity)

    @Update
    suspend fun updateNote(noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNote(noteEntity: NoteEntity)
}