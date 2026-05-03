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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.example.grocerystore.repository.AuthRepository
import com.example.grocerystore.repository.OrderRepository
import com.example.grocerystore.api.Order as ApiOrder
import com.example.grocerystore.api.User
import kotlinx.coroutines.launch

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
    private lateinit var authRepository: AuthRepository
    private lateinit var orderRepository: OrderRepository
    
    private var orders: List<ApiOrder> = emptyList()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        authRepository = AuthRepository(this)
        orderRepository = OrderRepository(this)

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
        orderAdapter = OrderAdapter(orders) { order ->
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
        val user = authRepository.getCurrentUser()
        if (user != null) {
            emailText.text = user.email
            fullNameText.text = user.fullName ?: ""
        } else {
            // Not logged in, redirect to login
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadOrders() {
        lifecycleScope.launch {
            orderRepository.getOrders(0, 3).onSuccess { loadedOrders ->
                orders = loadedOrders
                updateOrdersUI()
            }.onFailure {
                // Silently fail for profile - just show empty orders
            }
        }
    }
    
    private fun updateOrdersUI() {
        if (orders.isEmpty()) {
            ordersRecyclerView.visibility = View.GONE
            emptyOrdersLayout.visibility = View.VISIBLE
        } else {
            ordersRecyclerView.visibility = View.VISIBLE
            emptyOrdersLayout.visibility = View.GONE
            orderAdapter = OrderAdapter(orders) { order ->
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra("order_id", order.id)
                intent.putExtra("is_new_order", false)
                startActivity(intent)
            }
            ordersRecyclerView.adapter = orderAdapter
        }
    }

    private fun showEditProfileDialog() {
        val user = authRepository.getCurrentUser() ?: return
        
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
            
            // Update profile via API
            lifecycleScope.launch {
                authRepository.updateProfile(fullName, phone, shippingAddress).onSuccess {
                    loadUserInfo()
                    Toast.makeText(this@ProfileActivity, getString(R.string.save), Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(this@ProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
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
                authRepository.logout()
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


