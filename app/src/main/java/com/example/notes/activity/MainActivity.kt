package com.example.notes.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.notes.R
import com.example.notes.adapters.NotesAdapter
import com.example.notes.databinding.ActivityMainBinding
import com.example.notes.repositories.NoteRepository
import com.example.notes.roomdb.NoteDB
import com.example.notes.roomdb.NoteEntity
import com.example.notes.viewModelFactory.MainViewModelFactory
import com.example.notes.viewModels.MainViewModel
import com.example.notes.worker.NotesWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: NotesAdapter
    private lateinit var searchView: SearchView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var noteRepository: NoteRepository
    private var notesList: MutableList<NoteEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchView = binding.searchText
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize the Dao, Repository and View Model
        val noteDao = NoteDB.getInstance(this).getNoteDao()
        noteRepository = NoteRepository(noteDao)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(noteRepository)
        )[MainViewModel::class.java]

        adapter = NotesAdapter(this, mutableListOf(), viewModel)
        binding.notesList.adapter = adapter

        // Clear Search and Update Adapter
        viewModel.getAllNotesOfUser().observe(this) {
            searchView.setQuery("", false)
            searchView.clearFocus()
            notesList.clear()
            notesList.addAll(it)
            adapter.changeList(notesList)
        }

        // Navigate to Add Note Page
        binding.addNote.setOnClickListener {
            navigateToAddNoteActivity()
        }

        // Search on Notes
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    viewModel.handleSearch(notesList, adapter, p0)
                }
                return true
            }
        })

        backupNotesInFirestore()
    }

    // Navigate to add Activity page
    private fun navigateToAddNoteActivity() {
        startActivity(Intent(this, AddNoteActivity::class.java))
    }

    // Create Menu Options
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle menu options
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> navigateToLoginPage()
        }
        return super.onOptionsItemSelected(item)
    }

    /*
    Delete al notes of user from DB
    and sign out
     */
    private fun navigateToLoginPage() {
        viewModel.deleteUserNotes(firebaseAuth.currentUser!!.uid)
        firebaseAuth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    // Run worker to backup notes
    private fun backupNotesInFirestore(){
        val constraint = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val workerRequest = PeriodicWorkRequest.Builder(NotesWorker::class.java, 24, TimeUnit.HOURS).setConstraints(constraint).build()
        WorkManager.getInstance(this).enqueue(workerRequest)
    }
}
