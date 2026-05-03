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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerystore.repository.AuthRepository
import com.example.grocerystore.repository.CartRepository
import com.example.grocerystore.repository.OrderRepository
import com.example.grocerystore.api.Order as ApiOrder
import com.example.grocerystore.api.OrderItem as ApiOrderItem
import com.example.grocerystore.api.User
import kotlinx.coroutines.launch

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
    
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var authRepository: AuthRepository
    
    // Current order data for submission
    private var currentOrderItems: List<ApiOrderItem> = emptyList()
    private var currentOriginalPrice: Double = 0.0
    private var currentDiscount: Double = 0.0
    private var currentReduction: Double = 0.0
    private var currentFinalTotal: Double = 0.0

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Initialize repositories
        cartRepository = CartRepository(this)
        orderRepository = OrderRepository(this)
        authRepository = AuthRepository(this)

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
        // Check if logged in
        if (!authRepository.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }
        
        lifecycleScope.launch {
            cartRepository.getCart().onSuccess { cartResponse ->
                if (cartResponse.items.isEmpty()) {
                    Toast.makeText(this@OrderActivity, getString(R.string.empty_cart), Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }
                
                // Store order data
                currentOrderItems = cartResponse.items.map { item ->
                    ApiOrderItem(
                        id = item.id,
                        productId = item.product.id,
                        productName = item.product.name,
                        productNameEn = item.product.nameEn,
                        imageUrl = item.product.imageUrl,
                        unitPrice = item.product.price,
                        quantity = item.quantity,
                        subtotal = item.subtotal
                    )
                }
                currentOriginalPrice = cartResponse.originalPrice
                currentDiscount = cartResponse.discount
                currentReduction = cartResponse.reduction
                currentFinalTotal = cartResponse.finalTotal
                
                displayOrderPreview()
                submitOrderButton.text = getString(R.string.submit_order)
                submitOrderButton.isEnabled = true
            }.onFailure { e ->
                Toast.makeText(this@OrderActivity, "Failed to load cart: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun loadExistingOrder() {
        orderId?.let { id ->
            lifecycleScope.launch {
                orderRepository.getOrderById(id).onSuccess { order ->
                    displayOrder(order)
                    submitOrderButton.text = getString(R.string.view_order)
                    submitOrderButton.isEnabled = false
                }.onFailure { e ->
                    Toast.makeText(this@OrderActivity, "订单不存在: ${e.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        } ?: run {
            Toast.makeText(this, "订单ID无效", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayOrderPreview() {
        // Display order items
        val adapter = OrderProductAdapter(currentOrderItems)
        orderItemsRecyclerView.adapter = adapter
        
        // Display price details
        originalPrice.text = String.format("¥%.2f", currentOriginalPrice)
        deliveryPrice.text = getString(R.string.free)
        finalTotalPrice.text = String.format("¥%.2f", currentFinalTotal)
    }

    private fun displayOrder(order: ApiOrder) {
        // Display order items
        val adapter = OrderProductAdapter(order.items)
        orderItemsRecyclerView.adapter = adapter
        
        // Display price details
        originalPrice.text = String.format("¥%.2f", order.originalPrice)
        deliveryPrice.text = getString(R.string.free)
        finalTotalPrice.text = String.format("¥%.2f", order.finalTotal)
    }

    private fun submitOrder() {
        // Get user info
        val user = authRepository.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "用户信息丢失，请重新登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
            return
        }
        
        lifecycleScope.launch {
            val recipientName = user.fullName ?: "Unknown"
            val recipientPhone = user.phone ?: "Unknown"
            val shippingAddress = user.shippingAddress ?: "未设置地址"
            
            orderRepository.createOrder(recipientName, recipientPhone, shippingAddress).onSuccess {
                // Order created successfully, clear cart
                cartRepository.clearCart()
                
                AlertDialog.Builder(this@OrderActivity)
                    .setTitle(getString(R.string.order_success))
                    .setMessage(getString(R.string.order_success_message))
                    .setPositiveButton(getString(R.string.ok)) { _, _ ->
                        // Navigate to orders page
                        val intent = Intent(this@OrderActivity, OrdersActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                    .setCancelable(false)
                    .show()
            }.onFailure { e ->
                Toast.makeText(this@OrderActivity, "提交订单失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh order preview if it's a new order
        if (isNewOrder) {
            loadOrderPreview()
        }
    }
}
