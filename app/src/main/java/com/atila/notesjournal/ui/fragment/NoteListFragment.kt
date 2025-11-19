package com.atila.notesjournal.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atila.notesjournal.NotesJournalApp
import com.atila.notesjournal.R
import com.atila.notesjournal.ui.adapter.NoteAdapter
import com.atila.notesjournal.ui.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class NoteListFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        val app = requireActivity().application as NotesJournalApp
        NoteViewModel.Factory(app.repository)
    }
    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setupFab(view)
        observeNotes(view)
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewNotes)

        adapter = NoteAdapter(
            onNoteClick = { note ->
                val action = NoteListFragmentDirections
                    .actionNoteListToNoteEdit(note.id)
                findNavController().navigate(action)
            },
            onNoteLongClick = { note ->
                showDeleteDialog(note)
                true
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupFab(view: View) {
        view.findViewById<FloatingActionButton>(R.id.fabAddNote).setOnClickListener {
            val action = NoteListFragmentDirections.actionNoteListToNoteEdit(-1L)
            findNavController().navigate(action)
        }
    }

    private fun observeNotes(view: View) {
        val emptyView = view.findViewById<View>(R.id.textViewEmpty)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allNotes.collect { notes ->
                    adapter.submitList(notes)
                    emptyView.visibility = if (notes.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun showDeleteDialog(note: com.atila.notesjournal.data.model.Note) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteNote(note)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
