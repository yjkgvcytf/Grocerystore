package com.example.grocerystore

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

class CartActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var emptyCartLayout: View
    private lateinit var originalPrice: TextView
    private lateinit var discountAmount: TextView
    private lateinit var discountLayout: View
    private lateinit var reductionAmount: TextView
    private lateinit var reductionLayout: View
    private lateinit var finalTotalPrice: TextView
    private lateinit var youSaveText: TextView
    private lateinit var continueShoppingButton: Button
    private lateinit var checkoutButton: Button
    
    private lateinit var cartAdapter: CartAdapter

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupButtons()
        updateCart()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        originalPrice = findViewById(R.id.originalPrice)
        discountAmount = findViewById(R.id.discountAmount)
        discountLayout = findViewById(R.id.discountLayout)
        reductionAmount = findViewById(R.id.reductionAmount)
        reductionLayout = findViewById(R.id.reductionLayout)
        finalTotalPrice = findViewById(R.id.finalTotalPrice)
        youSaveText = findViewById(R.id.youSaveText)
        continueShoppingButton = findViewById(R.id.continueShoppingButton)
        checkoutButton = findViewById(R.id.checkoutButton)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(CartManager.getCartItems()) {
            updateCart()
        }
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }
        
        continueShoppingButton.setOnClickListener {
            // Return to home page
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        
        // Ensure button is clickable
        continueShoppingButton.isClickable = true
        continueShoppingButton.isEnabled = true
        
        checkoutButton.setOnClickListener {
            handleCheckout()
        }
        
        // Ensure button is clickable
        checkoutButton.isClickable = true
        checkoutButton.isEnabled = true
    }

    private fun handleCheckout() {
        // Step 1: Check if cart is empty
        val cartItems = CartManager.getCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.empty_cart),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        // Step 2: Check login status
        val user = UserManager.getCurrentUser(this)
        if (user == null) {
            // Not logged in, redirect to login page
            Toast.makeText(
                this,
                "请先登录",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            return
        }
        
        // Step 3: Logged in, navigate to order preview page
        android.util.Log.d("CartActivity", "Starting OrderActivity with is_new_order=true")
        val intent = Intent(this, OrderActivity::class.java).apply {
            putExtra("is_new_order", true)
            putExtra("order_id", null as String?)
        }
        startActivity(intent)
    }

    private fun updateCart() {
        val cartItems = CartManager.getCartItems()
        
        if (cartItems.isEmpty()) {
            cartRecyclerView.visibility = View.GONE
            emptyCartLayout.visibility = View.VISIBLE
            checkoutButton.isEnabled = false
            continueShoppingButton.isEnabled = false
            updatePriceDetails(0.0, 0.0, 0.0, 0.0)
        } else {
            cartRecyclerView.visibility = View.VISIBLE
            emptyCartLayout.visibility = View.GONE
            checkoutButton.isEnabled = true
            continueShoppingButton.isEnabled = true
            
            val originalTotal = CartManager.getOriginalTotalPrice()
            val discount = CartManager.getDiscount()
            val reduction = CartManager.getReduction()
            val finalTotal = CartManager.getFinalTotal()
            
            updatePriceDetails(originalTotal, discount, reduction, finalTotal)
            
            cartAdapter = CartAdapter(cartItems) {
                updateCart()
            }
            cartRecyclerView.adapter = cartAdapter
        }
    }

    private fun updatePriceDetails(
        originalTotal: Double,
        discount: Double,
        reduction: Double,
        finalTotal: Double
    ) {
        originalPrice.text = String.format("¥%.2f", originalTotal)
        finalTotalPrice.text = String.format("¥%.2f", finalTotal)
        
        // Show/hide discount
        if (discount > 0) {
            discountLayout.visibility = View.VISIBLE
            discountAmount.text = String.format("-¥%.2f", discount)
        } else {
            discountLayout.visibility = View.GONE
        }
        
        // Show/hide reduction
        if (reduction > 0) {
            reductionLayout.visibility = View.VISIBLE
            reductionAmount.text = String.format("-¥%.2f", reduction)
        } else {
            reductionLayout.visibility = View.GONE
        }
        
        // Show total savings
        val totalSavings = discount + reduction
        if (totalSavings > 0) {
            youSaveText.text = String.format(
                "%s: ¥%.2f",
                getString(R.string.you_save),
                totalSavings
            )
            youSaveText.visibility = View.VISIBLE
        } else {
            youSaveText.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCart()
    }
}
