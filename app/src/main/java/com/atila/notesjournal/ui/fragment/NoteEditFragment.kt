package com.atila.notesjournal.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.atila.notesjournal.NotesJournalApp
import com.atila.notesjournal.R
import com.atila.notesjournal.data.model.Note
import com.atila.notesjournal.ui.viewmodel.NoteViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class NoteEditFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        val app = requireActivity().application as NotesJournalApp
        NoteViewModel.Factory(app.repository)
    }
    private val args: NoteEditFragmentArgs by navArgs()

    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextContent: TextInputEditText
    private lateinit var buttonSave: MaterialButton

    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_note_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextContent = view.findViewById(R.id.editTextContent)
        buttonSave = view.findViewById(R.id.buttonSave)

        if (args.noteId != -1L) {
            loadNote()
        }

        buttonSave.setOnClickListener {
            saveNote()
        }
    }

    private fun loadNote() {
        viewModel.getNoteById(args.noteId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedNote.collect { note ->
                    note?.let {
                        currentNote = it
                        editTextTitle.setText(it.title)
                        editTextContent.setText(it.content)
                    }
                }
            }
        }
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString().trim()
        val content = editTextContent.text.toString().trim()

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(requireContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentNote != null) {
            // Update existing note
            val updatedNote = currentNote!!.copy(
                title = title,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            viewModel.updateNote(updatedNote)
        } else {
            // Create new note
            viewModel.insertNote(title, content)
        }

        viewModel.clearSelectedNote()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSelectedNote()
    }
}
