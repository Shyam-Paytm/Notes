package com.example.notes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.notes.adapters.NotesAdapter
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB
import com.example.notes.viewModelFactory.MainViewModelFactory
import com.example.notes.viewModels.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Dao, Repository and View Model
        val noteDao = NoteDB.getInstance(this).getNoteDao()
        val noteRepository = NoteRepository(noteDao)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(noteRepository)
        )[MainViewModel::class.java]

        viewModel.getAllNotes().observe(this, Observer {
            println(it)
            binding.notesList.adapter = NotesAdapter(it)
        })

        // Navigate to Add Note Page
        binding.addNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }
}