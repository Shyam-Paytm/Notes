package com.example.notes.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.adapters.NotesAdapter
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    @Deprecated("Fetch All notes of user now")
    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteRepository.getAllNotes()
    }

    fun getAllNotesOfUser(): LiveData<List<NoteEntity>> {
        return noteRepository.getAllNotesOfUser(firebaseAuth.currentUser!!.uid)
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

    // Handle Search
    fun handleSearch(notesList: List<NoteEntity>, adapter: NotesAdapter, searchVal: String?) {
        val searchText = searchVal!!.lowercase(Locale.getDefault())
        val tempNotes: MutableList<NoteEntity> = mutableListOf()
        if (searchText.isNotEmpty()) {
            notesList.forEach {
                val tempTitle = it.title.lowercase(Locale.getDefault())
                val tempBody = it.body.lowercase(Locale.getDefault())
                if (tempTitle.contains(searchText) || tempBody.contains(searchText)) {
                    tempNotes.add(it)
                }
            }
            adapter.changeList(tempNotes)
        } else {
            adapter.changeList(notesList)
        }
    }
}