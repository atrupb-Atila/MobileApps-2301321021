package com.atila.notesjournal

import android.app.Application
import androidx.room.Room
import com.atila.notesjournal.data.NoteDatabase
import com.atila.notesjournal.data.repository.NoteRepository

class NotesJournalApp : Application() {

    lateinit var database: NoteDatabase
        private set

    lateinit var repository: NoteRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes_database"
        ).build()

        repository = NoteRepository(database.noteDao())
    }
}
