package com.example.notes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        viewModel.getAllNotes().observe(this) {
            binding.notesList.adapter = NotesAdapter(it, viewModel)
        }

        // Navigate to Add Note Page
        binding.addNote.setOnClickListener {
            navigateToAddNoteActivity()
        }

        // Search on Notes
        binding.searchButton.setOnClickListener {
            val searchText = binding.searchText.text.toString()
            Toast(this).apply {
                setText(searchText)
                duration = Toast.LENGTH_LONG
                show()
            }
            // TODO : Add Logic to filter the Notes
        }
    }

    private fun navigateToAddNoteActivity() {
        startActivity(Intent(this, AddNoteActivity::class.java))
    }
}

/* insert kr rha hun toh apne aap, new data kasie fetch kr rha hai
jbki get all notes wala method call bhi ni kiya
 */