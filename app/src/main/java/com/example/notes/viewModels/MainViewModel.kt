package com.example.notes.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import kotlinx.coroutines.launch

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    var noteData: MutableLiveData<List<NoteEntity>>? = null

    var noteLiveData :LiveData<List<NoteEntity>>?=null

    fun getAllNotes(): LiveData<List<NoteEntity>> {
        //noteLiveData = noteRepository.getAllNotes()
        return noteRepository.getAllNotes()
    }

    fun insertNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.insertNote(noteEntity)
            getAllNotes()
        }
    }

    fun deleteNote(noteEntity: NoteEntity) {
        viewModelScope.launch {
            noteRepository.deleteNote(noteEntity)
        }
    }
}