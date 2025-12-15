package com.example.grocerystore

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var orderItemsRecyclerView: RecyclerView
    private lateinit var originalPrice: TextView
    private lateinit var deliveryPrice: TextView
    private lateinit var finalTotalPrice: TextView
    private lateinit var submitOrderButton: Button
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private var orderId: String? = null
    private var isNewOrder: Boolean = false
    private var currentUser: User? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get intent data
        orderId = intent.getStringExtra("order_id")
        isNewOrder = intent.getBooleanExtra("is_new_order", false)

        initViews()
        setupRecyclerView()
        setupButtons()
        loadOrder()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        originalPrice = findViewById(R.id.originalPrice)
        deliveryPrice = findViewById(R.id.deliveryPrice)
        finalTotalPrice = findViewById(R.id.finalTotalPrice)
        submitOrderButton = findViewById(R.id.submitOrderButton)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupRecyclerView() {
        orderItemsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }
        
        submitOrderButton.setOnClickListener {
            if (isNewOrder) {
                // Show confirmation dialog
                showConfirmDialog()
            } else {
                // View existing order, just go back
                finish()
            }
        }
        
        // Bottom navigation
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
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }
        
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_order))
            .setMessage(getString(R.string.confirm_order_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                submitOrder()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun loadOrder() {
        if (isNewOrder) {
            // Load order preview from cart
            loadOrderPreview()
        } else {
            // Load existing order
            loadExistingOrder()
        }
    }

    private fun loadOrderPreview() {
        // Step 1: Get cart items
        val cartItems = CartManager.getCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_cart), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Step 2: Get current user
        currentUser = UserManager.getCurrentUser(this)
        if (currentUser == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }
        
        // Step 3: Display order preview
        displayOrderPreview(cartItems)
        submitOrderButton.text = getString(R.string.submit_order)
        submitOrderButton.isEnabled = true
    }

    private fun loadExistingOrder() {
        orderId?.let { id ->
            val order = OrderManager.getOrderById(id)
            if (order != null) {
                displayOrder(order)
                submitOrderButton.text = getString(R.string.view_order)
                submitOrderButton.isEnabled = false
            } else {
                Toast.makeText(this, "订单不存在", Toast.LENGTH_SHORT).show()
                finish()
            }
        } ?: run {
            Toast.makeText(this, "订单ID无效", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayOrderPreview(items: List<CartItem>) {
        // Calculate prices
        val originalTotal = CartManager.getOriginalTotalPrice()
        val discount = CartManager.getDiscount()
        val reduction = CartManager.getReduction()
        val finalTotal = CartManager.getFinalTotal()
        
        // Display order items
        val adapter = OrderProductAdapter(items)
        orderItemsRecyclerView.adapter = adapter
        
        // Display price details
        originalPrice.text = String.format("¥%.2f", originalTotal)
        deliveryPrice.text = getString(R.string.free)
        finalTotalPrice.text = String.format("¥%.2f", finalTotal)
    }

    private fun displayOrder(order: Order) {
        // Display order items
        val adapter = OrderProductAdapter(order.items)
        orderItemsRecyclerView.adapter = adapter
        
        // Display price details
        originalPrice.text = String.format("¥%.2f", order.originalPrice)
        deliveryPrice.text = getString(R.string.free)
        finalTotalPrice.text = String.format("¥%.2f", order.finalTotal)
    }

    private fun submitOrder() {
        // Step 1: Validate cart items
        val cartItems = CartManager.getCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_cart), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Step 2: Validate user
        val user = currentUser ?: UserManager.getCurrentUser(this)
        if (user == null) {
            Toast.makeText(this, "用户信息丢失，请重新登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }
        
        // Step 3: Create order
        val order = OrderManager.createOrder(
            items = cartItems,
            shippingAddress = user.shippingAddress.ifEmpty { "未设置地址" },
            recipientName = user.fullName,
            recipientPhone = user.phone
        )
        
        // Step 4: Clear cart
        CartManager.clearCart()
        
        // Step 5: Show success message
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.order_success))
            .setMessage(getString(R.string.order_success_message))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // Clear the order preview and go back
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh order preview if it's a new order
        if (isNewOrder) {
            loadOrderPreview()
        }
    }
}
