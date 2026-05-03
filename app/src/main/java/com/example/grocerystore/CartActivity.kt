package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerystore.repository.CartRepository
import com.example.grocerystore.repository.AuthRepository
import kotlinx.coroutines.launch

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
    private lateinit var loadingIndicator: ProgressBar
    
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartRepository: CartRepository
    private lateinit var authRepository: AuthRepository
    
    private var cartItems: List<CartItem> = emptyList()
    private var currentOriginalPrice: Double = 0.0
    private var currentDiscount: Double = 0.0
    private var currentReduction: Double = 0.0
    private var currentFinalTotal: Double = 0.0

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRepository = CartRepository(this)
        authRepository = AuthRepository(this)

        initViews()
        setupRecyclerView()
        setupButtons()
        loadCartFromApi()
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
        loadingIndicator = findViewById(R.id.loadingIndicator)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems, {
            // Refresh cart when item is removed
            loadCartFromApi()
        }, this)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }
        
        continueShoppingButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        
        continueShoppingButton.isClickable = true
        continueShoppingButton.isEnabled = true
        
        checkoutButton.setOnClickListener {
            handleCheckout()
        }
        
        checkoutButton.isClickable = true
        checkoutButton.isEnabled = true
    }

    private fun handleCheckout() {
        if (cartItems.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.empty_cart),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        
        if (!authRepository.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
            return
        }
        
        val intent = Intent(this, OrderActivity::class.java).apply {
            putExtra("is_new_order", true)
        }
        startActivity(intent)
    }

    private fun loadCartFromApi() {
        if (!authRepository.isLoggedIn()) {
            showEmptyCart()
            return
        }

        loadingIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            cartRepository.getCart().onSuccess { cartResponse ->
                loadingIndicator.visibility = View.GONE
                
                // Convert API cart items to local CartItem
                cartItems = cartResponse.items.map { apiItem ->
                    CartItem(
                        product = Product(
                            id = apiItem.product.id,
                            name = apiItem.product.name,
                            nameEn = apiItem.product.nameEn,
                            nameRu = apiItem.product.nameRu,
                            description = apiItem.product.description,
                            descriptionEn = apiItem.product.descriptionEn,
                            descriptionRu = apiItem.product.descriptionRu,
                            price = apiItem.product.price,
                            imageUrl = apiItem.product.imageUrl,
                            category = apiItem.product.category,
                            categoryEn = apiItem.product.categoryEn,
                            categoryRu = apiItem.product.categoryRu,
                            soldCount = apiItem.product.soldCount,
                            stock = apiItem.product.stock,
                            featured = apiItem.product.featured
                        ),
                        quantity = apiItem.quantity,
                        id = apiItem.id
                    )
                }
                
                currentOriginalPrice = cartResponse.originalPrice
                currentDiscount = cartResponse.discount
                currentReduction = cartResponse.reduction
                currentFinalTotal = cartResponse.finalTotal
                
                updateUI()
            }.onFailure {
                loadingIndicator.visibility = View.GONE
                Toast.makeText(this@CartActivity, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            showEmptyCart()
        } else {
            showCartItems()
        }
    }

    private fun showEmptyCart() {
        cartRecyclerView.visibility = View.GONE
        emptyCartLayout.visibility = View.VISIBLE
        checkoutButton.isEnabled = false
        continueShoppingButton.isEnabled = false
        updatePriceDetails(0.0, 0.0, 0.0, 0.0)
    }

    private fun showCartItems() {
        cartRecyclerView.visibility = View.VISIBLE
        emptyCartLayout.visibility = View.GONE
        checkoutButton.isEnabled = true
        continueShoppingButton.isEnabled = true
        
        updatePriceDetails(currentOriginalPrice, currentDiscount, currentReduction, currentFinalTotal)

        cartAdapter = CartAdapter(cartItems, {
            loadCartFromApi()
        }, this)
        cartRecyclerView.adapter = cartAdapter
    }

    private fun updatePriceDetails(
        originalTotal: Double,
        discount: Double,
        reduction: Double,
        finalTotal: Double
    ) {
        originalPrice.text = String.format("¥%.2f", originalTotal)
        finalTotalPrice.text = String.format("¥%.2f", finalTotal)
        
        if (discount > 0) {
            discountLayout.visibility = View.VISIBLE
            discountAmount.text = String.format("-¥%.2f", discount)
        } else {
            discountLayout.visibility = View.GONE
        }
        
        if (reduction > 0) {
            reductionLayout.visibility = View.VISIBLE
            reductionAmount.text = String.format("-¥%.2f", reduction)
        } else {
            reductionLayout.visibility = View.GONE
        }
        
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
        loadCartFromApi()
    }
}
