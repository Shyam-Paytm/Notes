package com.example.notes.repositories

import androidx.lifecycle.LiveData
import com.example.notes.roomdb.NoteDao
import com.example.notes.roomdb.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {

    // Fetch all Notes
    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getAllNote()
    }

    // Insert a Note
    suspend fun insertNote(noteEntity: NoteEntity) {
        noteDao.insertNote(noteEntity)
    }

    // Update a Note
    suspend fun updateNote(noteEntity: NoteEntity) {
        noteDao.updateNote(noteEntity)
    }

    // Delete a Note
    suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

}