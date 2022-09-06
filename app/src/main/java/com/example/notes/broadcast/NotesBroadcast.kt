package com.example.notes.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB

class NotesBroadcast : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        val noteRepository = NoteRepository(NoteDB.getInstance(p0!!).getNoteDao())
        noteRepository.backupNotesInFirestore()
    }
}