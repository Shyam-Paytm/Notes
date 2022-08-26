package com.example.notes.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB
import com.example.notes.roomdb.NoteEntity
import com.example.notes.viewModelFactory.MainViewModelFactory
import com.example.notes.viewModels.MainViewModel

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noteDao = NoteDB.getInstance(this).getNoteDao()
        val noteRepository = NoteRepository(noteDao)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(noteRepository)
        )[MainViewModel::class.java]

        // Add Note in Database and navigate to main activity
        binding.addButton.setOnClickListener {
            val title = binding.addTitle.text.toString()
            val body = binding.addBody.text.toString()
            viewModel.insertNote(NoteEntity(title = title, body = body))
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}