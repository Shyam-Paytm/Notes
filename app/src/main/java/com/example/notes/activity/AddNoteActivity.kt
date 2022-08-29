package com.example.notes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
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

        // If passed data, then it is for Editing
        val data = intent.getSerializableExtra("data") as? NoteEntity
        if (data != null) {
            handleEditUI(data)
        }

        // On Cancel, go back to main activity
        binding.cancelButton.setOnClickListener {
            navigateToMainActivity()
        }

        /* Add Note in Database
        if data is passed through intent then edit it
        and navigate to main activity
        */
        binding.addButton.setOnClickListener {
            val title = binding.addTitle.text.toString()
            val body = binding.addBody.text.toString()
            if (data != null) {
                data.apply {
                    this.title = title
                    this.body = body
                }
                viewModel.updateNote(data)
            } else {
                viewModel.insertNote(NoteEntity(title = title, body = body))
            }
            navigateToMainActivity()
        }
    }

    /*
    If initial data has been passed,
    then change UI as per Edit page
     */
    private fun handleEditUI(data: NoteEntity) {
        binding.addButton.text = getString(R.string.edit)
        binding.addTitle.apply {
            setText(data.title)
        }
        binding.addBody.apply {
            setText(data.body)
        }
    }

    override fun onBackPressed() {
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        finish()
    }

}