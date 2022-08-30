package com.example.notes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import kotlinx.coroutines.launch

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteRepository.getAllNotes()
    }

    fun insertNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.insertNote(noteEntity)
        }
    }

    fun updateNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNote(noteEntity)
        }
    }

    fun deleteNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteEntity)
        }
    }
}