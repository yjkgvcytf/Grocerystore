package com.example.grocerystore

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var emailText: TextView
    private lateinit var fullNameText: TextView
    private lateinit var editProfileButton: Button
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyOrdersLayout: View
    private lateinit var logoutButton: Button
    private lateinit var viewAllOrders: TextView
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private lateinit var orderAdapter: OrderAdapter

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupButtons()
        loadUserInfo()
        loadOrders()
    }

    private fun initViews() {
        emailText = findViewById(R.id.emailText)
        fullNameText = findViewById(R.id.fullNameText)
        editProfileButton = findViewById(R.id.editProfileButton)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        emptyOrdersLayout = findViewById(R.id.emptyOrdersLayout)
        logoutButton = findViewById(R.id.logoutButton)
        viewAllOrders = findViewById(R.id.viewAllOrders)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(OrderManager.getOrders()) { order ->
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("order_id", order.id)
            intent.putExtra("is_new_order", false)
            startActivity(intent)
        }
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersRecyclerView.adapter = orderAdapter
    }

    private fun setupButtons() {
        editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }
        
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }
        
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        navCategories.setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            startActivity(intent)
        }
        
        navCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        
        navOrders.setOnClickListener {
            // Navigate to orders page
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        
        navProfile.setOnClickListener {
            // Already on profile page
        }
        
        viewAllOrders.setOnClickListener {
            // Navigate to orders page
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserInfo() {
        val user = UserManager.getCurrentUser(this)
        if (user != null) {
            emailText.text = user.email
            fullNameText.text = user.fullName
        } else {
            // Not logged in, redirect to login
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadOrders() {
        // Show all orders (all statuses)
        val allOrders = OrderManager.getOrders()
        
        if (allOrders.isEmpty()) {
            ordersRecyclerView.visibility = View.GONE
            emptyOrdersLayout.visibility = View.VISIBLE
        } else {
            ordersRecyclerView.visibility = View.VISIBLE
            emptyOrdersLayout.visibility = View.GONE
            orderAdapter = OrderAdapter(allOrders) { order ->
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("order_id", order.id)
                intent.putExtra("is_new_order", false)
                startActivity(intent)
            }
            ordersRecyclerView.adapter = orderAdapter
        }
    }

    private fun showEditProfileDialog() {
        val user = UserManager.getCurrentUser(this) ?: return
        
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val fullNameEditText: TextInputEditText = dialogView.findViewById(R.id.fullNameEditText)
        val phoneEditText: TextInputEditText = dialogView.findViewById(R.id.phoneEditText)
        val shippingAddressEditText: TextInputEditText = dialogView.findViewById(R.id.shippingAddressEditText)
        val saveButton: Button = dialogView.findViewById(R.id.saveButton)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)
        
        fullNameEditText.setText(user.fullName)
        phoneEditText.setText(user.phone)
        shippingAddressEditText.setText(user.shippingAddress)
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        
        saveButton.setOnClickListener {
            val fullName = fullNameEditText.text?.toString() ?: ""
            val phone = phoneEditText.text?.toString() ?: ""
            val shippingAddress = shippingAddressEditText.text?.toString() ?: ""
            
            if (fullName.isEmpty()) {
                fullNameEditText.error = getString(R.string.required)
                return@setOnClickListener
            }
            
            if (phone.isEmpty()) {
                phoneEditText.error = getString(R.string.required)
                return@setOnClickListener
            }
            
            UserManager.updateUser(this, fullName, phone, shippingAddress)
            loadUserInfo()
            dialog.dismiss()
            Toast.makeText(this, getString(R.string.save), Toast.LENGTH_SHORT).show()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirm))
            .setPositiveButton(getString(R.string.logout)) { _, _ ->
                UserManager.logout(this)
                CartManager.clearCart()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadUserInfo()
        loadOrders()
    }
}


