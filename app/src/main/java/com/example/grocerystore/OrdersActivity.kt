package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.example.grocerystore.repository.OrderRepository
import com.example.grocerystore.api.Order as ApiOrder
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyOrdersLayout: View
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderRepository: OrderRepository
    
    private var orders: List<ApiOrder> = emptyList()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        orderRepository = OrderRepository(this)

        initViews()
        setupRecyclerView()
        setupButtons()
        loadOrders()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        emptyOrdersLayout = findViewById(R.id.emptyOrdersLayout)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(orders) { order ->
            // Navigate to order detail page
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra("order_id", order.id)
            intent.putExtra("is_new_order", false)
            startActivity(intent)
        }
        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersRecyclerView.adapter = orderAdapter
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
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
            // Already on orders page
        }
        
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadOrders() {
        loadingIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            orderRepository.getOrders(0, 50).onSuccess { loadedOrders ->
                loadingIndicator.visibility = View.GONE
                orders = loadedOrders
                updateUI()
            }.onFailure { e ->
                loadingIndicator.visibility = View.GONE
                Toast.makeText(this@OrdersActivity, "Failed to load orders: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateUI() {
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

    override fun onResume() {
        super.onResume()
        loadOrders()
    }
}
