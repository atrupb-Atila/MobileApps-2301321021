package com.atila.notesjournal

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.atila.notesjournal.data.model.Note
import com.atila.notesjournal.data.repository.NoteRepository
import com.atila.notesjournal.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: NoteRepository
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        whenever(repository.getAllNotes()).thenReturn(flowOf(emptyList()))
        viewModel = NoteViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertNote calls repository insertNote`() = runTest {
        // When
        viewModel.insertNote("Test Title", "Test Content", null)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).insertNote(
            org.mockito.kotlin.argThat { note ->
                note.title == "Test Title" && note.content == "Test Content"
            }
        )
    }

    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        // Given
        val note = Note(id = 1, title = "Test", content = "Content")

        // When
        viewModel.deleteNote(note)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).deleteNote(note)
    }

    @Test
    fun `updateNote calls repository updateNote`() = runTest {
        // Given
        val note = Note(id = 1, title = "Updated", content = "Updated Content")

        // When
        viewModel.updateNote(note)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).updateNote(note)
    }

    @Test
    fun `getNoteById updates selectedNote`() = runTest {
        // Given
        val note = Note(id = 1, title = "Test", content = "Content")
        whenever(repository.getNoteById(1)).thenReturn(note)

        // When
        viewModel.getNoteById(1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(note, viewModel.selectedNote.value)
    }

    @Test
    fun `clearSelectedNote sets selectedNote to null`() {
        // When
        viewModel.clearSelectedNote()

        // Then
        assertNull(viewModel.selectedNote.value)
    }

    @Test
    fun `insertNote with imagePath includes imagePath`() = runTest {
        // When
        viewModel.insertNote("Title", "Content", "/path/to/image.jpg")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).insertNote(
            org.mockito.kotlin.argThat { note ->
                note.imagePath == "/path/to/image.jpg"
            }
        )
    }
}
