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
        if (noteEntity.fireStoreId != null) {
            val db = Firebase.firestore
            db.collection("notes").document(noteEntity.fireStoreId!!)
                .delete()
        }
        noteDao.deleteNote(noteEntity)
    }

    // Delete All notes of user
    private suspend fun deleteUserNotes(userId: String) {
        noteDao.deleteUserNotes(userId)
    }

    // Back up all Modified notes of user in Firestore
    fun backupNotesInFirestore(deleteNotes: Boolean = false) {
        val db = Firebase.firestore
        val batch = db.batch()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        CoroutineScope(Dispatchers.IO).launch {
            // Fetch all modified notes
            noteDao.getAllModifiedNotesOfUser(userId).value?.forEach {
                // If document already exist in Firestore then update it
                val collection = db.collection("notes")
                if (it.fireStoreId != null) {
                    batch.set(collection.document(it.fireStoreId!!), it)
                }
                batch.set(collection.document(), it)
            }
            batch.commit()
            if (deleteNotes) {
                deleteUserNotes(userId)
            }
        }
    }
}