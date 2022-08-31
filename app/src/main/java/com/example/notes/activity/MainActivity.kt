package com.example.notes.activity

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var adapter: NotesAdapter

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


        adapter = NotesAdapter(this, mutableListOf(), viewModel)
        binding.notesList.adapter = adapter

        viewModel.getAllNotes().observe(this) {
            adapter.changeList(it)
        }

        // Navigate to Add Note Page
        binding.addNote.setOnClickListener {
            navigateToAddNoteActivity()
        }

        // Search on Notes
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchText.query.toString()
            handleSearch(searchText)
        }

        // Search on Notes
        binding.searchText.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    handleSearch(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
//                if (p0 != null) {
//                    handleSearch(p0)
//                }
                return true
            }
        })
    }

    // Handle Search
    private fun handleSearch(searchVal: String?) {
        binding.searchText.clearFocus()
        viewModel.getAllNotes("%$searchVal%", adapter)
    }


    // Navigate to add Activity page
    private fun navigateToAddNoteActivity() {
        startActivity(Intent(this, AddNoteActivity::class.java))
    }
}
