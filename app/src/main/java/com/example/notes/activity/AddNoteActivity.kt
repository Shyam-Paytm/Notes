package com.example.notes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.notes.R
import com.example.notes.databinding.ActivityAddNoteBinding
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB
import com.example.notes.roomdb.NoteEntity
import com.example.notes.viewModelFactory.MainViewModelFactory
import com.example.notes.viewModels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var noteRepository: NoteRepository

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteRepository = NoteRepository(NoteDB.getInstance(this).getNoteDao())
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
            onBackPressed()
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
                viewModel.insertNote(
                    NoteEntity(
                        userId = firebaseAuth.currentUser!!.uid,
                        title = title,
                        body = body
                    )
                )
            }
            onBackPressed()
        }
    }

    /*
    If initial data has been passed,
    then change UI as per Edit page
     */
    private fun handleEditUI(data: NoteEntity) {
        binding.addButton.text = getString(R.string.save)
        binding.addTitle.apply {
            setText(data.title)
        }
        binding.addBody.apply {
            setText(data.body)
        }
    }

    /*
    TODO : REMOVE IT
    Add Data in Firestore
     */
    private fun addInFirestore() {
        val title = binding.addTitle.text.toString()
        val body = binding.addBody.text.toString()
        val uid = firebaseAuth.currentUser!!.uid

        val noteEntity = hashMapOf(
            "user_id" to uid,
            "title" to title,
            "body" to body
        )

        db.collection("notes")
            .add(noteEntity)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast(this).apply {
                        setText("Note Added Successfully")
                        duration = Toast.LENGTH_LONG
                        show()
                    }
                } else {
                    Toast(this).apply {
                        setText(it.exception?.message.toString())
                        duration = Toast.LENGTH_LONG
                        show()
                    }
                }
            }
    }

}