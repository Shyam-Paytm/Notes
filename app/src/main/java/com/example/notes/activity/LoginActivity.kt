package com.example.notes.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.notes.R
import com.example.notes.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var submitButton: Button
    private lateinit var navigateText: TextView
    private var loginPage = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        email = binding.email
        password = binding.password
        submitButton = binding.submitButton
        navigateText = binding.navigateText

        // Handle Submit Button as per Login Page or Register Page
        submitButton.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                it.hideKeyboard()
                submitButton.text = getString(R.string.please_wait)
                submitButton.isEnabled = false
                submitButton.isClickable = false
                if (loginPage) {
                    login()
                } else {
                    register()

                }
            } else {
                Toast(this).apply {
                    setText("EmailID and Password is Required")
                    duration = Toast.LENGTH_LONG
                    show()
                }
            }
        }

        // Navigate between login page and register page
        navigateText.setOnClickListener {
            if (loginPage) {
                handleLoginText()
            } else {
                handleRegisterText()
            }
        }

    }

    // Sign a user in Firebase
    private fun login() {
        val email = email.text.toString()
        val password = password.text.toString()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    navigateToMainActivity()
                } else {
                    Toast(this).apply {
                        setText(it.exception?.message)
                        duration = Toast.LENGTH_LONG
                        show()
                    }
                }
                submitButton.text = getString(R.string.login)
                submitButton.isEnabled = true
                submitButton.isClickable = true
            }
    }

    // Create in a user with Firebase
    private fun register() {
        val email = email.text.toString()
        val password = password.text.toString()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    navigateToMainActivity()
                } else {
                    Toast(this).apply {
                        setText(it.exception?.message)
                        duration = Toast.LENGTH_LONG
                        show()
                    }
                }
                submitButton.text = getString(R.string.register)
                submitButton.isEnabled = true
                submitButton.isClickable = true
            }
    }

    // When user move to Login to Register
    private fun handleLoginText() {
        email.text.clear()
        password.text.clear()
        submitButton.text = getString(R.string.register)
        loginPage = false
        navigateText.text = getString(R.string.already_register)
    }

    // When user move to Register to Login
    private fun handleRegisterText() {
        email.text.clear()
        password.text.clear()
        submitButton.text = getString(R.string.login)
        loginPage = true
        navigateText.text = getString(R.string.not_register)
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    // Hide Keyboard
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            navigateToMainActivity()
        }
    }
}