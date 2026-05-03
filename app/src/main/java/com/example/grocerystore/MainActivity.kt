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
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.grocerystore.api.AuthResponse
import com.example.grocerystore.repository.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var rememberMeCheckBox: CheckBox
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var languageSpinner: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var prefs: SharedPreferences
    
    private lateinit var authRepository: AuthRepository
    private var hasSpinnerInitialized = false

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Check if already logged in
        authRepository = AuthRepository(this)
        if (authRepository.isLoggedIn()) {
            navigateToHome()
            return
        }
        
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
        progressBar = findViewById(R.id.progressBar)
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
        showLoading(true)

        lifecycleScope.launch {
            try {
                val result = authRepository.login(email, password)

                // Process result OUTSIDE the coroutine so try-catch covers it
                processLoginResult(result)
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(
                    this@MainActivity,
                    "登录异常: ${e.message ?: getString(R.string.login_failed)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun processLoginResult(result: Result<AuthResponse>) {
        showLoading(false)

        try {
            result.onSuccess { _ ->
                if (authRepository.isLoggedIn()) {
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(this, "登录状态异常，请重试", Toast.LENGTH_SHORT).show()
                }
            }.onFailure { error ->
                val raw = error.message
                val errorMessage = when {
                    raw == null || raw.isEmpty() -> getString(R.string.login_failed)
                    raw.contains("Unable to resolve host", ignoreCase = true) ->
                        "无法连接到服务器，请检查网络连接"
                    raw.contains("timeout", ignoreCase = true) ->
                        "连接超时，请稍后重试"
                    raw.contains("connection", ignoreCase = true) ->
                        "连接失败，请检查服务器是否运行"
                    raw.contains("401", ignoreCase = true) ->
                        "邮箱或密码错误"
                    raw.contains("404", ignoreCase = true) ->
                        "服务器地址错误"
                    raw.contains("邮箱或密码错误") -> raw
                    raw.contains("Invalid email") -> "邮箱或密码错误"
                    raw.contains("Bad credentials") -> "邮箱或密码错误"
                    else -> raw
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // Failsafe: if Result processing itself throws (e.g. Gson error), show generic message
            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun showLoading(show: Boolean) {
        val loadingOverlay = findViewById<View>(R.id.loadingOverlay)
        loadingOverlay?.visibility = if (show) View.VISIBLE else View.GONE
        loginButton.isEnabled = !show
        emailEditText.isEnabled = !show
        passwordEditText.isEnabled = !show
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
