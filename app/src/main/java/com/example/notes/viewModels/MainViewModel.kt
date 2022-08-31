package com.example.notes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.adapters.NotesAdapter
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteRepository.getAllNotes()
    }

    fun getAllNotes(searchQuery: String, adapter: NotesAdapter) {
        viewModelScope.launch {
            val data = noteRepository.getAllNotes(searchQuery)
            withContext(Dispatchers.Main) {
                adapter.changeList(data)
            }
        }
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