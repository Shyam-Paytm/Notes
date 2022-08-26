package com.example.notes.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notes.repositories.NoteRepository
import com.example.notes.viewModels.MainViewModel

class MainViewModelFactory(private val noteRepository: NoteRepository):ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(noteRepository) as T
    }

}