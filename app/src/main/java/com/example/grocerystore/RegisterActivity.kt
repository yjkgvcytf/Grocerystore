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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.grocerystore.api.AuthResponse
import com.example.grocerystore.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

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
    private lateinit var loadingOverlay: android.widget.FrameLayout
    private lateinit var loadingIndicator: android.widget.ProgressBar

    private lateinit var authRepository: AuthRepository
    private var hasSpinnerInitialized = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupLanguageSpinner()
        setupRegisterButton()
        setupBackButton()
        setupLoginLink()

        authRepository = AuthRepository(this)
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
        loadingOverlay = findViewById(R.id.loadingOverlay)
        loadingIndicator = findViewById(R.id.loadingIndicator)
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
        hasSpinnerInitialized = false

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!hasSpinnerInitialized) {
                    hasSpinnerInitialized = true
                    return
                }
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
        showLoading()
        disableInputs()

        lifecycleScope.launch {
            try {
                val result = authRepository.register(email, password, fullName, phone)
                processRegistrationResult(result)
            } catch (e: Exception) {
                hideLoading()
                enableInputs()
                Toast.makeText(
                    this@RegisterActivity,
                    "注册异常: ${e.message ?: getString(R.string.registration_failed)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun processRegistrationResult(result: Result<AuthResponse>) {
        hideLoading()
        enableInputs()

        try {
            result.onSuccess {
                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { e ->
                val raw = e.message
                val errorMessage = when {
                    raw == null || raw.isEmpty() -> getString(R.string.registration_failed)
                    raw.contains("Unable to resolve host", ignoreCase = true) ->
                        "无法连接到服务器，请检查网络连接"
                    raw.contains("timeout", ignoreCase = true) ->
                        "连接超时，请稍后重试"
                    raw.contains("connection", ignoreCase = true) ->
                        "连接失败，请检查服务器是否运行"
                    raw.contains("409", ignoreCase = true) ->
                        "该邮箱已被注册"
                    raw.contains("400", ignoreCase = true) ->
                        "注册信息不完整或格式错误"
                    raw.contains("邮箱") && raw.contains("已注册") -> raw
                    raw.contains("Invalid email") -> "邮箱格式不正确"
                    else -> raw
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.registration_failed), Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading() {
        loadingOverlay.visibility = android.view.View.VISIBLE
        loadingIndicator.visibility = android.view.View.VISIBLE
    }

    private fun hideLoading() {
        loadingOverlay.visibility = android.view.View.GONE
        loadingIndicator.visibility = android.view.View.GONE
    }

    private fun disableInputs() {
        fullNameEditText.isEnabled = false
        emailEditText.isEnabled = false
        phoneEditText.isEnabled = false
        passwordEditText.isEnabled = false
        confirmPasswordEditText.isEnabled = false
        registerButton.isEnabled = false
    }

    private fun enableInputs() {
        fullNameEditText.isEnabled = true
        emailEditText.isEnabled = true
        phoneEditText.isEnabled = true
        passwordEditText.isEnabled = true
        confirmPasswordEditText.isEnabled = true
        registerButton.isEnabled = true
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

