package com.atila.notesjournal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atila.notesjournal.data.model.Note
import com.atila.notesjournal.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    val allNotes = repository.getAllNotes()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    fun getNoteById(id: Long) {
        viewModelScope.launch {
            _selectedNote.value = repository.getNoteById(id)
        }
    }

    fun insertNote(title: String, content: String, imagePath: String? = null) {
        viewModelScope.launch {
            val note = Note(
                title = title,
                content = content,
                imagePath = imagePath
            )
            repository.insertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun clearSelectedNote() {
        _selectedNote.value = null
    }
}
