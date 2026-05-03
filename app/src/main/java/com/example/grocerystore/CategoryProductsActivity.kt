package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.example.grocerystore.repository.AuthRepository
import com.example.grocerystore.repository.CartRepository
import kotlinx.coroutines.launch

class CategoryProductsActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var categoryTitle: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyLayout: View
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private lateinit var productAdapter: ProductGridAdapter
    private var categoryId: String? = null
    private var category: Category? = null
    
    private lateinit var cartRepository: CartRepository
    private lateinit var authRepository: AuthRepository

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_products)

        cartRepository = CartRepository(this)
        authRepository = AuthRepository(this)

        categoryId = intent.getStringExtra("category_id")
        category = categoryId?.let { CategoryData.getCategoryById(it) }

        initViews()
        setupRecyclerView()
        setupTitle()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        categoryTitle = findViewById(R.id.categoryTitle)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        emptyLayout = findViewById(R.id.emptyLayout)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
        
        setupBottomNavigation()
    }
    
    private fun setupBottomNavigation() {
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        navCategories.setOnClickListener {
            // Already on categories page
            finish()
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

    private fun setupTitle() {
        category?.let { cat ->
            val currentLanguage = LocaleHelper.getLocale(this)
            val categoryName = when (currentLanguage) {
                "zh" -> cat.name
                "en" -> cat.nameEn
                "ru" -> cat.nameRu
                else -> cat.name
            }
            categoryTitle.text = categoryName
        } ?: run {
            categoryTitle.text = getString(R.string.products_in_category)
        }
    }

    private fun setupRecyclerView() {
        val products = ProductData.getProductsByCategoryId(categoryId ?: "")
        
        if (products.isEmpty()) {
            productsRecyclerView.visibility = View.GONE
            emptyLayout.visibility = View.VISIBLE
        } else {
            productsRecyclerView.visibility = View.VISIBLE
            emptyLayout.visibility = View.GONE
            
            productAdapter = ProductGridAdapter(
                products,
                onItemClick = { product -> openProductDetails(product) },
                onAddToCart = { product -> addToCart(product) }
            )
            // Use GridLayoutManager with 2 columns
            productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
            productsRecyclerView.adapter = productAdapter
        }
        
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openProductDetails(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    private fun addToCart(product: Product) {
        if (!authRepository.isLoggedIn()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            return
        }
        lifecycleScope.launch {
            cartRepository.addToCart(product.id, 1).onSuccess {
                Toast.makeText(this@CategoryProductsActivity, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@CategoryProductsActivity, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh adapter to update language
        setupRecyclerView()
        setupTitle()
    }
}




