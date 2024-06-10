package com.example.myscan.ui

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myscan.R
import com.example.myscan.data.ScanModel
import com.example.myscan.databinding.ActivityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DetailActivity :AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = getString(R.string.blank)
            setDisplayHomeAsUpEnabled(true)
        }

        val scanModel: ScanModel? = intent.getParcelableExtra("item_list")

        if (scanModel != null) {
            binding.jumlah.text = scanModel.resultScan
            val timestamp = scanModel.timestamp
            val date = Date(timestamp)
            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            binding.tanggal2.text = formattedDate

            // Load image using Glide (or any other image loading library you prefer)
            Glide.with(this)
                .load(scanModel.image)
                .into(binding.imgScan)
            
            
            binding.deleteBtn.setOnClickListener {
                showDeleteConfirmationDialog(scanModel)
            }
        }
    }

    private fun showDeleteConfirmationDialog(scanModel: ScanModel) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure you want to delete this data?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            deleteData(scanModel)
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    // Fungsi untuk menghapus data dari Firebase
    private fun deleteData(scanModel: ScanModel) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            val dataRef = FirebaseDatabase.getInstance().getReference("scan")
                .child("users")
                .child(userId)
                .child("data")
                .child(scanModel.id)

            // Hapus data dari Firebase
            dataRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
                    finish() // Tutup aktivitas setelah penghapusan berhasil
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error deleting data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}