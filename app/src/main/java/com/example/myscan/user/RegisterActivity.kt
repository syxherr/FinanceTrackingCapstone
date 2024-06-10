package com.example.myscan.user

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myscan.R
import com.example.myscan.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        playAnimation()
        setupInput()

    }

    private fun setupInput() {
        binding.emailRegister.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val email = p0.toString()
                if (email.isNotEmpty() && isValidEmail(email)) {
                    binding.emailTextLayout.error = null
                } else {
                    binding.emailTextLayout.error = getString(R.string.invalid_email)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.passwordRegister.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val password = p0.toString()
                if (password.isNotEmpty() && password.length >= 5) {
                    binding.passwordEditTextLayout.error = null
                } else {
                    binding.passwordEditTextLayout.error = getString(R.string.pass_min)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnSignup.setOnClickListener {
            val email = binding.emailRegister.text.toString()
            val password = binding.passwordRegister.text.toString()
            val username = binding.nameRegister.text.toString()

            when {
                email.isEmpty() -> {
                    binding.emailTextLayout.error = getString(R.string.fill_email)
                }

                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.fill_password)

                }
                username.isEmpty() -> {
                    binding.nameTextLayout.error = getString(R.string.fill_name)
                }

                else -> {
                    signUp(email, password, username)
                }
            }
        }
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signUp(email: String, password: String, username: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { signup ->
                if (signup.isSuccessful) {
                    // Registrasi berhasil
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        saveUserInfoToDatabase(userId, username)
                    }
                } else {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle(getText(R.string.register_failed))
                        setPositiveButton(getText(R.string.tryagain)) { _, _ ->
                        }
                        show()
                        create()
                    }
                }
            }
    }

    private fun saveUserInfoToDatabase(userId: String, username: String) {
        // Menggunakan referensi database yang sesuai dengan struktur Anda
        val userRef = FirebaseDatabase.getInstance().getReference("scan/users/$userId")
        userRef.child("username").setValue(username)
            .addOnCompleteListener { usernameTask ->
                if (usernameTask.isSuccessful) {
                    // Simpan username berhasil, lanjutkan ke halaman beranda atau yang lain
                    showLoading(false)
                    Toast.makeText(this, getText(R.string.success_regis), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Gagal menyimpan username
                    showLoading(false)
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun playAnimation() {
        val title =
            ObjectAnimator.ofFloat(binding.txtSignup, View.ALPHA, 1f).setDuration(100)
        val title2 =
            ObjectAnimator.ofFloat(binding.regisTxt, View.ALPHA, 1f).setDuration(100)
        val name =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val name2 =
            ObjectAnimator.ofFloat(binding.nameRegister, View.ALPHA, 1f).setDuration(100)
        val name3 =
            ObjectAnimator.ofFloat(binding.nameTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val register =
            ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)
        val textSignUp =
            ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(100)
        val textSignUp2 =
            ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                name,
                name2,
                name3,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                register,
                textSignUp,
                textSignUp2
            )
            startDelay = 100
        }.start()
    }
}