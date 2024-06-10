package com.example.myscan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import android.app.Activity
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import com.example.myscan.R
import com.example.myscan.data.ScanModel
import com.example.myscan.databinding.ActivityInputBinding
import com.google.firebase.storage.StorageReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import java.util.Locale

class InputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputBinding
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Firebase.initialize(this)
        firebaseRef = FirebaseDatabase.getInstance().getReference("scan")
        storageRef = FirebaseStorage.getInstance().getReference("Images")

        supportActionBar?.apply {
            title = getString(R.string.blank)
            setDisplayHomeAsUpEnabled(true)
        }

        binding.btnSave.setOnClickListener {
            saveData()
        }

        binding.btnScanner.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                dispatchTakePictureIntent()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, getText(R.string.camera_permission_required), Toast.LENGTH_SHORT).show()
            }
        }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    uri = FileProvider.getUriForFile(
                        this,
                        "com.example.myscan.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    takePictureLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                binding.imgResultScan.setImageURI(uri)
            } else {
                Toast.makeText(this, "Image capture failed.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun saveData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val resultScan = binding.jumlah.text.toString()

        if (currentUser != null) {
            val userId = currentUser.uid

            if (uri != null) {
                val id = firebaseRef.push().key!!
                val imageRef =
                    storageRef.child("users").child(userId).child("Images").child(id)

                showLoading(true)
                binding.btnSave.isEnabled = false

                imageRef.putFile(uri!!)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { url ->
                                val image = url.toString()
                                val timestamp = System.currentTimeMillis()
                                val scanModel = ScanModel(id, resultScan, image, timestamp)

                                firebaseRef.child("users").child(userId).child("data").child(id)
                                    .setValue(scanModel)
                                    .addOnCompleteListener {
                                        Toast.makeText(
                                            this,
                                            getText(R.string.success_stored),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showLoading(false)
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { error ->
                                        Toast.makeText(
                                            this,
                                            "Error ${error.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                    }
                    .addOnCompleteListener {
                        binding.btnSave.isEnabled = true
                    }
            } else {
                showLoading(false)
                binding.btnSave.isEnabled = true
                Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}


//    private lateinit var binding: ActivityInputBinding
//    private lateinit var firebaseRef: DatabaseReference
//    private lateinit var storageRef: StorageReference
//    private var uri: Uri? = null
//    private val requestCameraPermission = 101
//    private val requestImageCapture = 102
//    private var currentPhotoPath: String? = null
//
//    private companion object {
//        private const val CAMERA_PERMISSION_REQUEST_CODE = 102
//        private const val CAMERA_REQUEST_CODE = 103
//        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityInputBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.apply {
//            title = getString(R.string.blank)
//            setDisplayHomeAsUpEnabled(true)
//        }
//        Firebase.initialize(this)
//        firebaseRef = FirebaseDatabase.getInstance().getReference("scan")
//        storageRef = FirebaseStorage.getInstance().getReference("Images")
//
//        binding.btnSave.setOnClickListener {
//                saveData()
//            }
//
//            val pickImage =
//                registerForActivityResult(ActivityResultContracts.GetContent()) {
//                    binding.imgResultScan.setImageURI(it)
//                    if (it != null) {
//                        uri = it
//                    }
//                }
//
//            binding.btnScanner.setOnClickListener {
//                pickImage.launch("image/*")
//            }
//        }
//
//        private fun saveData() {
//            val currentUser = FirebaseAuth.getInstance().currentUser
//            val resultScan = binding.jumlah.text.toString()
//
//            if (currentUser != null) {
//                val userId = currentUser.uid
//
//                if (uri != null) {
//                    val id = firebaseRef.push().key!!
//                    val imageRef =
//                        storageRef.child("users").child(userId).child("Images").child(id)
//
//                    showLoading(true)
//                    binding.btnSave.isEnabled = false // Menonaktifkan tombol upload
//
//                    imageRef.putFile(uri!!)
//                        .addOnSuccessListener { task ->
//                            task.metadata!!.reference!!.downloadUrl
//                                .addOnSuccessListener { url ->
//                                    Toast.makeText(
//                                        this,
//                                        "Image stored successfully",
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    val image = url.toString()
//                                    val timestamp = System.currentTimeMillis()
//                                    val scanModel = ScanModel(id, resultScan, image, timestamp)
//
//                                    // Simpan data ke Firebase
//                                    firebaseRef.child("users").child(userId).child("data").child(id)
//                                        .setValue(scanModel)
//                                        .addOnCompleteListener {
//                                            Toast.makeText(
//                                                this,
//                                                "Data stored successfully",
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                            showLoading(false)
//                                            val intent = Intent(this, MainActivity::class.java)
//                                            startActivity(intent)
//                                            finish()
//                                    }
//                                    .addOnFailureListener { error ->
//                                        Toast.makeText(this, "Error ${error.message}", Toast.LENGTH_SHORT)
//                                            .show()
//                                    }
//                            }
//                    }.addOnCompleteListener {
//                        binding.btnSave.isEnabled = true
//                    }
//            } else {
//                showLoading(false)
//                binding.btnSave.isEnabled = true
//                Toast.makeText(this, getText(R.string.selectimage), Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        onBackPressed()
//        return super.onSupportNavigateUp()
//    }
//}
