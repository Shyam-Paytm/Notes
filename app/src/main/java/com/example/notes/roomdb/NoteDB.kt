package com.example.notes.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class], version = 3)
abstract class NoteDB : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao

    companion object {
        private var INSTANCE: NoteDB? = null

        fun getInstance(context: Context): NoteDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDB::class.java,
                        "NotesDB"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as NoteDB
        }
    }
}