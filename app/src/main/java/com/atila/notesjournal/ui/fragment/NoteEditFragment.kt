package com.atila.notesjournal.ui.fragment

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteEditFragment : Fragment() {

    private val viewModel: NoteViewModel by activityViewModels {
        val app = requireActivity().application as NotesJournalApp
        NoteViewModel.Factory(app.repository)
    }
    private val args: NoteEditFragmentArgs by navArgs()

    private lateinit var editTextTitle: TextInputEditText
    private lateinit var editTextContent: TextInputEditText
    private lateinit var buttonSave: MaterialButton
    private lateinit var buttonCamera: MaterialButton
    private lateinit var imageViewNote: ImageView

    private var currentNote: Note? = null
    private var currentPhotoPath: String? = null
    private var hasLoadedNote = false

    // Camera permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera result launcher
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentPhotoPath?.let { path ->
                displayImage(path)
            }
        }
    }

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
        buttonCamera = view.findViewById(R.id.buttonCamera)
        imageViewNote = view.findViewById(R.id.imageViewNote)

        if (args.noteId != -1L) {
            loadNote()
        }

        buttonSave.setOnClickListener {
            saveNote()
        }

        buttonCamera.setOnClickListener {
            checkCameraPermissionAndLaunch()
        }

        imageViewNote.setOnClickListener {
            currentPhotoPath?.let { path ->
                showFullImageDialog(path)
            }
        }
    }

    private fun loadNote() {
        viewModel.getNoteById(args.noteId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedNote.collect { note ->
                    note?.let {
                        // Only load values once to prevent overwriting user changes
                        if (!hasLoadedNote) {
                            hasLoadedNote = true
                            currentNote = it
                            editTextTitle.setText(it.title)
                            editTextContent.setText(it.content)
                            it.imagePath?.let { path ->
                                currentPhotoPath = path
                                displayImage(path)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera() {
        val photoFile = createImageFile()
        photoFile?.let { file ->
            val photoUri: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )
            currentPhotoPath = file.absolutePath
            takePictureLauncher.launch(photoUri)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error creating image file", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun displayImage(imagePath: String) {
        val file = File(imagePath)
        if (file.exists()) {
            // Use BitmapFactory to avoid caching issues when updating photo
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageViewNote.setImageBitmap(bitmap)
            imageViewNote.visibility = View.VISIBLE
        }
    }

    private fun showFullImageDialog(imagePath: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val imageView = ImageView(requireContext())
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(imageView)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.show()
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
                timestamp = System.currentTimeMillis(),
                imagePath = currentPhotoPath
            )
            viewModel.updateNote(updatedNote)
        } else {
            // Create new note
            viewModel.insertNote(title, content, currentPhotoPath)
        }

        viewModel.clearSelectedNote()
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSelectedNote()
    }
}
