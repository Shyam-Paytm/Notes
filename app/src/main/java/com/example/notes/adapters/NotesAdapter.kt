package com.example.notes.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.activity.AddNoteActivity
import com.example.notes.roomdb.NoteEntity
import com.example.notes.viewModels.MainViewModel

class NotesAdapter(
    private val context: Context,
    private var dataSet: List<NoteEntity>,
    private val viewModel: MainViewModel
) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.note_title)
        val bodyTextView: TextView = view.findViewById(R.id.note_body)
        val deleteButton: ImageButton = view.findViewById(R.id.delete_note_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_note_layout, parent, false)
        return NoteViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = dataSet[position]
        holder.titleTextView.text = position.plus(1).toString().plus(". ").plus(item.title)
        holder.bodyTextView.text = item.body

        // Delete Note on button click
        holder.deleteButton.setOnClickListener {
            viewModel.deleteNote(item)
        }

        // Go to Edit page
        holder.titleTextView.setOnClickListener {
            navigateToAddNoteActivity(item)
        }
        holder.bodyTextView.setOnClickListener {
            navigateToAddNoteActivity(item)
        }
    }

    private fun navigateToAddNoteActivity(data:NoteEntity){
        val intent = Intent(context, AddNoteActivity::class.java).apply {
            putExtra("data", data)
        }
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeList(newList: List<NoteEntity>) {
        dataSet = newList
        notifyDataSetChanged()
    }
}