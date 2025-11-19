package com.atila.notesjournal.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.atila.notesjournal.R
import com.atila.notesjournal.data.model.Note
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Boolean
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)

        fun bind(note: Note) {
            textViewTitle.text = note.title.ifEmpty { "Untitled" }
            textViewContent.text = note.content
            textViewDate.text = formatDate(note.timestamp)

            itemView.setOnClickListener { onNoteClick(note) }
            itemView.setOnLongClickListener { onNoteLongClick(note) }
        }

        private fun formatDate(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date(timestamp))
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
