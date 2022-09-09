package com.example.notes.viewModels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.notes.adapters.NotesAdapter
import com.example.notes.broadcast.BatteryReceiver
import com.example.notes.broadcast.NotesBroadcast
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteEntity
import com.example.notes.worker.NotesWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

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
                        // Store Firestore id too, inorder to update it if any change occurs
                        tempNotes.add(
                            NoteEntity(
                                userId = userId as String,
                                title = title as String,
                                body = body as String,
                                modified = false,
                                fireStoreId = document.id
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

    // Run worker to backup notes
    @Deprecated("Same is being now handled by Alarm Manager")
    private fun backupNotesInFirestoreWork(context: Context) {
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workerRequest = PeriodicWorkRequest.Builder(NotesWorker::class.java, 24, TimeUnit.HOURS)
            .setConstraints(constraint).build()
        WorkManager.getInstance(context).enqueue(workerRequest)
    }

    // Backup notes in Firestore using Alarm Manager
    fun backupNotesInFirestore(context: Context) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 1)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotesBroadcast::class.java)
        val pi = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            12 * 60 * 60 * 1000,
            pi
        )
    }

    /*
    First back up notes in Firestore
    then delete all records
     */
    fun handleLogout() {
        noteRepository.backupNotesInFirestore(deleteNotes = true)
    }

    /*
    Send broadcast
     */
    fun sendCustomBroadcast(context: Context, receiver: BatteryReceiver) {
        context.registerReceiver(receiver, IntentFilter("android.intent.br.CUSTOM_INTENT"))
        val intent = Intent()
        intent.action = "android.intent.br.CUSTOM_INTENT"
        context.sendBroadcast(intent)
        Log.d("Debug", "Register Receiver")
    }
}