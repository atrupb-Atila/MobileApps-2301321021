package com.atila.notesjournal.data.repository

import com.atila.notesjournal.data.dao.NoteDao
import com.atila.notesjournal.data.model.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao
) {

    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)
}
