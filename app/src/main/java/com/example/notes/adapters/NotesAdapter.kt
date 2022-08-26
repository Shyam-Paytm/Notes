package com.example.notes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.roomdb.NoteEntity

class NotesAdapter(private val dataSet: List<NoteEntity>) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {
    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.note_title)
        val bodyTextView: TextView = view.findViewById(R.id.note_body)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_note_layout, parent, false)
        return NoteViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = dataSet[position]
        holder.titleTextView.text = item.title
        holder.bodyTextView.text = item.body
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}