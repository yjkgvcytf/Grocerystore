package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var languageSpinner: Spinner
    private lateinit var prefs: SharedPreferences

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        initViews()
        setupLanguageSpinner()
        setupLoginButton()
        setupRegisterLink()
        loadSavedCredentials()
        
        // Check if coming from registration
        val registeredEmail = intent.getStringExtra("registered_email")
        if (!registeredEmail.isNullOrEmpty()) {
            emailEditText.setText(registeredEmail)
        }
    }

    private fun initViews() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
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
                
                val currentLang = LocaleHelper.getLocale(this@MainActivity)
                if (languageCode != currentLang) {
                    LocaleHelper.setLocale(this@MainActivity, languageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupLoginButton() {
        loginButton.setOnClickListener {
            val email = emailEditText.text?.toString() ?: ""
            val password = passwordEditText.text?.toString() ?: ""

            if (validateInput(email, password)) {
                // Save credentials if "Remember me" is checked
                if (rememberMeCheckBox.isChecked) {
                    saveCredentials(email, password)
                } else {
                    clearCredentials()
                }

                // Perform login (here you would typically call your API)
                performLogin(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            emailEditText.error = getString(R.string.email_address) + " " + getString(R.string.required)
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = getString(R.string.invalid_email)
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = getString(R.string.password) + " " + getString(R.string.required)
            return false
        }

        if (password.length < 6) {
            passwordEditText.error = getString(R.string.password_too_short)
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        // TODO: Implement actual login logic with your backend API
        Toast.makeText(
            this,
            getString(R.string.login_success),
            Toast.LENGTH_SHORT
        ).show()
        
        // Save user info (for demo, use email as name)
        val fullName = email.split("@")[0] // Simple name extraction
        val phone = prefs.getString("saved_phone", "") ?: ""
        UserManager.login(this, email, fullName, phone)
        
        // Navigate to home screen after successful login
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveCredentials(email: String, password: String) {
        val editor = prefs.edit()
        editor.putString("saved_email", email)
        editor.putString("saved_password", password)
        editor.putBoolean("remember_me", true)
        editor.apply()
    }

    private fun loadSavedCredentials() {
        val rememberMe = prefs.getBoolean("remember_me", false)
        if (rememberMe) {
            val savedEmail = prefs.getString("saved_email", "")
            val savedPassword = prefs.getString("saved_password", "")
            emailEditText.setText(savedEmail)
            passwordEditText.setText(savedPassword)
            rememberMeCheckBox.isChecked = true
        }
    }

    private fun clearCredentials() {
        val editor = prefs.edit()
        editor.remove("saved_email")
        editor.remove("saved_password")
        editor.putBoolean("remember_me", false)
        editor.apply()
    }

    private fun setupRegisterLink() {
        registerLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
