package com.example.myscan.user

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myscan.ui.MainActivity
import com.example.myscan.R
import com.example.myscan.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        playAnimation()
        setupInput()

    }

    private fun setupInput() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val password = binding.passwordLogin.text.toString()
            when {
                email.isEmpty() -> {
                    binding.emailTextLayout.error = getString(R.string.fill_email)
                }
                password.isEmpty()-> {
                    binding.passwordTextLayout.error = getString(R.string.fill_password)

                }
                else -> {
                    logIn(email, password)
                }
            }
        }
        binding.tvSignup.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logIn(email: String, password: String) {
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { login ->
                showLoading(false)
                if (login.isSuccessful) {
                    // Login berhasil
                    showLoading(true)
                    Toast.makeText(this, getText(R.string.success_login), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showLoading(false)
                    AlertDialog.Builder(this).apply {
                        setTitle(getText(R.string.login_failed))
                        setMessage(getText(R.string.invalid_login))
                        setPositiveButton(getText(R.string.tryagain)) { _, _ ->
                        }
                        show()
                        create()
                    }
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {

        val title =
            ObjectAnimator.ofFloat(binding.titleLogin, View.ALPHA, 1f).setDuration(100)
        val title2 =
            ObjectAnimator.ofFloat(binding.txtLogin, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailtextview, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordTextLayout, View.ALPHA, 1f).setDuration(100)
        val login =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)
        val textSignUp =
            ObjectAnimator.ofFloat(binding.textView, View.ALPHA, 1f).setDuration(100)
        val txtSignUp =
            ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login,
                textSignUp,
                txtSignUp
            )
            startDelay = 100
        }.start()
    }
}