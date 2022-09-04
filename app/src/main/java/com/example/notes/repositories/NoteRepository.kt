package com.example.notes.repositories

import androidx.lifecycle.LiveData
import com.example.notes.roomdb.NoteDao
import com.example.notes.roomdb.NoteEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NoteRepository(private val noteDao: NoteDao) {

    // Fetch all Notes
    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getAllNote()
    }

    // Fetch all Notes of User
    fun getAllNotesOfUser(userId: String): LiveData<List<NoteEntity>> {
        return noteDao.getAllNoteOfUser(userId)
    }

    // Insert a Note
    suspend fun insertNote(noteEntity: NoteEntity) {
        noteDao.insertNote(noteEntity)
    }

    // Insert List of Notes
    suspend fun insertAllNote(noteList: List<NoteEntity>) {
        noteDao.insertAllNote(noteList)
    }

    // Update a Note
    suspend fun updateNote(noteEntity: NoteEntity) {
        noteDao.updateNote(noteEntity)
    }

    // Delete a Note
    suspend fun deleteNote(noteEntity: NoteEntity) {
        noteDao.deleteNote(noteEntity)
    }

    // Delete All notes of user
    suspend fun deleteUserNotes(userId: String) {
        noteDao.deleteUserNotes(userId)
    }

    // Back up all notes of user in Firestore
    fun backupNotesInFirestore() {
        val db = Firebase.firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val batch = db.batch()
        CoroutineScope(Dispatchers.IO).launch {
            noteDao.getAllNoteOfUser(firebaseAuth.currentUser!!.uid).value?.forEach {
                batch.set(db.collection("notes").document(), it)
            }
            batch.commit()
        }
    }

}