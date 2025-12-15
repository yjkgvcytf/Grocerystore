package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var loginLink: android.widget.TextView
    private lateinit var languageSpinner: Spinner

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupLanguageSpinner()
        setupRegisterButton()
        setupBackButton()
        setupLoginLink()
    }

    private fun initViews() {
        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        backButton = findViewById(R.id.backButton)
        loginLink = findViewById(R.id.loginLink)
        languageSpinner = findViewById(R.id.languageSpinner)
    }

    private fun setupLanguageSpinner() {
        val languages = arrayOf(
            getString(R.string.chinese),
            getString(R.string.english),
            getString(R.string.russian)
        )
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Set current language selection
        val currentLanguage = LocaleHelper.getLocale(this)
        val position = when (currentLanguage) {
            "zh" -> 0
            "en" -> 1
            "ru" -> 2
            else -> 0
        }
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    0 -> "zh"
                    1 -> "en"
                    2 -> "ru"
                    else -> "zh"
                }
                
                val currentLang = LocaleHelper.getLocale(this@RegisterActivity)
                if (languageCode != currentLang) {
                    LocaleHelper.setLocale(this@RegisterActivity, languageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupRegisterButton() {
        registerButton.setOnClickListener {
            val fullName = fullNameEditText.text?.toString() ?: ""
            val email = emailEditText.text?.toString() ?: ""
            val phone = phoneEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""
            val confirmPassword = confirmPasswordEditText.text?.toString() ?: ""

            if (validateInput(fullName, email, phone, password, confirmPassword)) {
                // Perform registration (here you would typically call your API)
                performRegistration(fullName, email, phone, password)
            }
        }
    }

    private fun validateInput(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Validate full name
        if (fullName.isEmpty()) {
            fullNameEditText.error = getString(R.string.full_name) + " " + getString(R.string.required)
            return false
        }

        if (fullName.length < 2) {
            fullNameEditText.error = getString(R.string.name_too_short)
            return false
        }

        // Validate email
        if (email.isEmpty()) {
            emailEditText.error = getString(R.string.email_address) + " " + getString(R.string.required)
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = getString(R.string.invalid_email)
            return false
        }

        // Validate phone
        if (phone.isEmpty()) {
            phoneEditText.error = getString(R.string.phone_number) + " " + getString(R.string.required)
            return false
        }

        if (!isValidPhoneNumber(phone)) {
            phoneEditText.error = getString(R.string.invalid_phone)
            return false
        }

        // Validate password
        if (password.isEmpty()) {
            passwordEditText.error = getString(R.string.password) + " " + getString(R.string.required)
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = getString(R.string.password_too_short)
            return false
        }

        // Validate confirm password
        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = getString(R.string.confirm_password) + " " + getString(R.string.required)
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = getString(R.string.passwords_not_match)
            return false
        }

        return true
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Remove spaces, dashes, and parentheses
        val cleanedPhone = phone.replace(Regex("[\\s\\-\\(\\)]"), "")
        // Check if it's a valid phone number (at least 10 digits, can start with +)
        return cleanedPhone.matches(Regex("^\\+?[1-9]\\d{9,14}$"))
    }

    private fun performRegistration(fullName: String, email: String, phone: String, password: String) {
        // TODO: Implement actual registration logic with your backend API
        Toast.makeText(
            this,
            getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
        
        // Auto login after registration
        UserManager.login(this, email, fullName, phone)
        
        // Navigate to home screen after successful registration
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun setupLoginLink() {
        loginLink.setOnClickListener {
            finish()
        }
    }
}

