package com.atila.notesjournal

import com.atila.notesjournal.data.dao.NoteDao
import com.atila.notesjournal.data.model.Note
import com.atila.notesjournal.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositoryTest {

    private lateinit var noteDao: NoteDao
    private lateinit var repository: NoteRepository

    @Before
    fun setup() {
        noteDao = mock()
        repository = NoteRepository(noteDao)
    }

    @Test
    fun `getAllNotes returns flow from dao`() = runTest {
        // Given
        val notes = listOf(
            Note(id = 1, title = "Note 1", content = "Content 1"),
            Note(id = 2, title = "Note 2", content = "Content 2")
        )
        whenever(noteDao.getAllNotes()).thenReturn(flowOf(notes))

        // When
        val result = repository.getAllNotes().first()

        // Then
        assertEquals(notes, result)
    }

    @Test
    fun `getNoteById calls dao getNoteById`() = runTest {
        // Given
        val note = Note(id = 1, title = "Test", content = "Content")
        whenever(noteDao.getNoteById(1)).thenReturn(note)

        // When
        val result = repository.getNoteById(1)

        // Then
        assertEquals(note, result)
        verify(noteDao).getNoteById(1)
    }

    @Test
    fun `insertNote calls dao insertNote`() = runTest {
        // Given
        val note = Note(title = "New Note", content = "New Content")
        whenever(noteDao.insertNote(note)).thenReturn(1L)

        // When
        val result = repository.insertNote(note)

        // Then
        assertEquals(1L, result)
        verify(noteDao).insertNote(note)
    }

    @Test
    fun `updateNote calls dao updateNote`() = runTest {
        // Given
        val note = Note(id = 1, title = "Updated", content = "Updated Content")

        // When
        repository.updateNote(note)

        // Then
        verify(noteDao).updateNote(note)
    }

    @Test
    fun `deleteNote calls dao deleteNote`() = runTest {
        // Given
        val note = Note(id = 1, title = "Test", content = "Content")

        // When
        repository.deleteNote(note)

        // Then
        verify(noteDao).deleteNote(note)
    }

    @Test
    fun `deleteNoteById calls dao deleteNoteById`() = runTest {
        // When
        repository.deleteNoteById(1)

        // Then
        verify(noteDao).deleteNoteById(1)
    }

    @Test
    fun `getNoteById returns null when note not found`() = runTest {
        // Given
        whenever(noteDao.getNoteById(999)).thenReturn(null)

        // When
        val result = repository.getNoteById(999)

        // Then
        assertEquals(null, result)
    }
}
