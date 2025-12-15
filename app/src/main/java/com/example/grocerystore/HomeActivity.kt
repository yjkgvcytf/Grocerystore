package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeActivity : AppCompatActivity() {
    
    private lateinit var searchEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var cartIcon: ImageView
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var popularProductsRecyclerView: RecyclerView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private lateinit var productAdapter: ProductAdapter
    private lateinit var popularProductAdapter: ProductHorizontalAdapter
    private lateinit var bannerAdapter: BannerAdapter
    
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupLanguageSpinner()
        setupBanner()
        setupRecyclerViews()
        setupBottomNavigation()
        setupSearch()
        updateCartBadge()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        languageSpinner = findViewById(R.id.languageSpinner)
        cartIcon = findViewById(R.id.cartIcon)
        bannerViewPager = findViewById(R.id.bannerViewPager)
        popularProductsRecyclerView = findViewById(R.id.popularProductsRecyclerView)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
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

        val currentLanguage = LocaleHelper.getLocale(this)
        val position = when (currentLanguage) {
            "zh" -> 0
            "en" -> 1
            "ru" -> 2
            else -> 0
        }
        languageSpinner.setSelection(position)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    0 -> "zh"
                    1 -> "en"
                    2 -> "ru"
                    else -> "zh"
                }
                
                val currentLang = LocaleHelper.getLocale(this@HomeActivity)
                if (languageCode != currentLang) {
                    LocaleHelper.setLocale(this@HomeActivity, languageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupBanner() {
        val bannerProducts = ProductData.getBannerProducts()
        bannerAdapter = BannerAdapter(bannerProducts) { product ->
            openProductDetails(product)
        }
        bannerViewPager.adapter = bannerAdapter
        
        // Auto-scroll banner
        bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Auto-scroll logic can be added here
            }
        })
    }

    private fun setupRecyclerViews() {
        allProducts = ProductData.getSampleProducts()
        filteredProducts = allProducts
        
        // Popular Products (Horizontal)
        popularProductAdapter = ProductHorizontalAdapter(
            ProductData.getPopularProducts(),
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        popularProductsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularProductsRecyclerView.adapter = popularProductAdapter
        
        // All Products (Vertical)
        productAdapter = ProductAdapter(
            filteredProducts,
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = productAdapter
    }

    private fun setupBottomNavigation() {
        navHome.setOnClickListener {
            // Already on home page
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

    private fun setupSearch() {
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
        
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterProducts(query: String) {
        if (query.isEmpty()) {
            filteredProducts = allProducts
        } else {
            val currentLanguage = LocaleHelper.getLocale(this)
            filteredProducts = allProducts.filter { product ->
                val name = when (currentLanguage) {
                    "zh" -> product.name
                    "en" -> product.nameEn
                    "ru" -> product.nameRu
                    else -> product.name
                }
                val description = when (currentLanguage) {
                    "zh" -> product.description
                    "en" -> product.descriptionEn
                    "ru" -> product.descriptionRu
                    else -> product.description
                }
                name.contains(query, ignoreCase = true) || 
                description.contains(query, ignoreCase = true)
            }
        }
        productAdapter = ProductAdapter(
            filteredProducts,
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        productsRecyclerView.adapter = productAdapter
    }

    private fun openProductDetails(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    private fun addToCart(product: Product) {
        CartManager.addToCart(product, 1)
        updateCartBadge()
        Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
    }

    private fun updateCartBadge() {
        val cartCount = CartManager.getTotalCount()
        // You can add a badge to cart icon here if needed
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        // Refresh adapters to update language
        setupRecyclerViews()
        setupBanner()
    }
}

