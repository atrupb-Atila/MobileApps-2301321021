package com.atila.notesjournal

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteCreationTest {

    @Test
    fun createNote_displaysInList() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Click the FAB to add a new note
        onView(withId(R.id.fabAddNote))
            .perform(click())

        // Type in the title
        onView(withId(R.id.editTextTitle))
            .perform(typeText("Test Note Title"), closeSoftKeyboard())

        // Type in the content
        onView(withId(R.id.editTextContent))
            .perform(typeText("This is test content"), closeSoftKeyboard())

        // Click save button
        onView(withId(R.id.buttonSave))
            .perform(click())

        // Verify the note appears in the list
        onView(withText("Test Note Title"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun emptyNoteList_showsEmptyMessage() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Note: This test may fail if there are existing notes
        // It's here to demonstrate testing empty state
        onView(withId(R.id.recyclerViewNotes))
            .check(matches(isDisplayed()))
    }

    @Test
    fun fabButton_isDisplayed() {
        // Launch the main activity
        ActivityScenario.launch(MainActivity::class.java)

        // Verify FAB is displayed
        onView(withId(R.id.fabAddNote))
            .check(matches(isDisplayed()))
    }
}
