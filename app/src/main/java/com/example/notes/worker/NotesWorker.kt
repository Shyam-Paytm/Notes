package com.example.notes.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB

class NotesWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        val noteRepository = NoteRepository(NoteDB.getInstance(context).getNoteDao())
        noteRepository.backupNotesInFirestore()
        return Result.success()
    }
}