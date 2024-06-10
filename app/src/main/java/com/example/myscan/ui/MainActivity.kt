package com.example.myscan.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myscan.R
import com.example.myscan.data.ScanAdapter
import com.example.myscan.data.ScanModel
import com.example.myscan.databinding.ActivityHomeBinding
import com.example.myscan.user.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var databaseRef: DatabaseReference
    private lateinit var scanAdapter: ScanAdapter
    private val scanList: ArrayList<ScanModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            // Pengguna belum login, arahkan ke LoginActivity atau tindakan yang sesuai.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Pengguna sudah login, lanjutkan dengan inisialisasi dan setup lainnya.

            binding.btnScan.setOnClickListener {
                startActivity(Intent(this, InputActivity::class.java))
            }
            supportActionBar?.apply {
                title = getString(R.string.finfix)
                setDisplayHomeAsUpEnabled(false)
            }

            val userId = currentUser.uid
            databaseRef = FirebaseDatabase.getInstance().getReference("scan/users/$userId/data")

            val usernameRef = FirebaseDatabase.getInstance().getReference("scan/users/$userId/username")
            usernameRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.getValue(String::class.java)
                    binding.namauser.text = username
                }

                override fun onCancelled(error: DatabaseError) {
                    val errorMessage = "Error fetching data: ${error.message}"
                    Log.e("Firebase", errorMessage)
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            })

            scanAdapter = ScanAdapter(scanList)
            binding.rvList.layoutManager = LinearLayoutManager(this)
            binding.rvList.adapter = scanAdapter

            setupScanList()
        }
    }

    private fun setupScanList() {
        showLoading(true)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scanList.clear()

                for (dataSnapshot in snapshot.children) {
                    val scanModel = dataSnapshot.getValue(ScanModel::class.java)
                    if (scanModel != null) {
                        scanList.add(scanModel)
                    }
                }

                scanAdapter.notifyDataSetChanged()
                showLoading(false)
                checkEmptyList()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
                showLoading(false)
                checkEmptyList()
            }
        })
    }

    private fun checkEmptyList() {
        if (scanList.isEmpty()) {
            binding.noneTxt.visibility = View.VISIBLE
        } else {
            binding.noneTxt.visibility = View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutButton -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}