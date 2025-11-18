package com.atila.notesjournal.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atila.notesjournal.data.dao.NoteDao
import com.atila.notesjournal.data.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
