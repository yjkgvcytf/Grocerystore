package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrdersActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyOrdersLayout: View
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
        setContentView(R.layout.activity_orders)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupButtons()
        loadOrders()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView)
        emptyOrdersLayout = findViewById(R.id.emptyOrdersLayout)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(OrderManager.getOrders()) { order ->
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
        val orders = OrderManager.getOrders()
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
