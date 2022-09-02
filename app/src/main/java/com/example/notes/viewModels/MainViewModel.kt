package com.example.notes.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.adapters.NotesAdapter
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db = Firebase.firestore

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

    private fun insertAllNotes(notesList: List<NoteEntity>) {
        viewModelScope.launch {
            noteRepository.insertAllNote(notesList)
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

    fun deleteUserNotes(userId: String) {
        viewModelScope.launch {
            noteRepository.deleteUserNotes(userId)
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

    // Fetch from Firestore and store in DB
    fun storeInDBFromFirestore(context: Context) {
        viewModelScope.launch {
            db.collection("notes")
                .whereEqualTo("user_id", firebaseAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { result ->
                    val tempNotes = mutableListOf<NoteEntity>()
                    for (document in result) {
                        val title = document.data["title"]
                        val body = document.data["body"]
                        val userId = document.data["user_id"] ?: ""
                        tempNotes.add(
                            NoteEntity(
                                userId = userId as String,
                                title = title as String,
                                body = body as String
                            )
                        )
                    }
                    insertAllNotes(tempNotes)
                }
                .addOnFailureListener {
                    Toast(context).apply {
                        setText(it.message)
                        duration = Toast.LENGTH_LONG
                        show()
                    }
                }
        }
    }
}