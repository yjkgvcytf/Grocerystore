package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.EditText
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.grocerystore.repository.AuthRepository
import com.example.grocerystore.repository.CartRepository
import com.example.grocerystore.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    
    private lateinit var languageSpinner: Spinner
    private lateinit var cartIcon: ImageView
    private lateinit var bannerViewPager: ViewPager2
    private lateinit var searchEditText: EditText
    private lateinit var popularProductsRecyclerView: RecyclerView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    private lateinit var loadingIndicator: ProgressBar
    
    private lateinit var productAdapter: ProductAdapter
    private lateinit var popularProductAdapter: ProductHorizontalAdapter
    private lateinit var bannerAdapter: BannerAdapter
    
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var hasSpinnerInitialized = false

    private lateinit var productRepository: ProductRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var authRepository: AuthRepository

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Debug: Check login status on entry
        productRepository = ProductRepository(this)
        cartRepository = CartRepository(this)
        authRepository = AuthRepository(this)

        // Debug log for login state
        android.util.Log.d("HomeActivity", "isLoggedIn: ${authRepository.isLoggedIn()}")
        android.util.Log.d("HomeActivity", "hasAuthToken: ${authRepository.hasAuthToken()}")

        initViews()
        setupLanguageSpinner()
        setupBanner()
        setupRecyclerViews()
        setupBottomNavigation()
        setupSearch()
        updateCartBadge()

        loadProductsFromApi()
    }

    override fun onResume() {
        super.onResume()
        // Debug: Check login status on resume
        android.util.Log.d("HomeActivity", "onResume - isLoggedIn: ${authRepository.isLoggedIn()}")

        updateCartBadge()
        // Refresh adapters to update language
        if (allProducts.isNotEmpty()) {
            updateProductsList()
        }
    }

    private fun initViews() {
        languageSpinner = findViewById(R.id.languageSpinner)
        cartIcon = findViewById(R.id.cartIcon)
        bannerViewPager = findViewById(R.id.bannerViewPager)
        searchEditText = findViewById(R.id.searchEditText)
        popularProductsRecyclerView = findViewById(R.id.popularProductsRecyclerView)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        navHome = findViewById(R.id.navHome)
        navCategories = findViewById(R.id.navCategories)
        navCart = findViewById(R.id.navCart)
        navOrders = findViewById(R.id.navOrders)
        navProfile = findViewById(R.id.navProfile)
        loadingIndicator = findViewById(R.id.loadingIndicator)
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
        hasSpinnerInitialized = false

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!hasSpinnerInitialized) {
                    hasSpinnerInitialized = true
                    return
                }
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

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = searchEditText.text?.toString()?.trim() ?: ""
                if (query.isNotEmpty()) {
                    searchProducts(query)
                } else {
                    Toast.makeText(this, getString(R.string.search), Toast.LENGTH_SHORT).show()
                }
                true
            } else {
                false
            }
        }
    }

    private fun searchProducts(query: String) {
        loadingIndicator.visibility = View.VISIBLE
        lifecycleScope.launch {
            productRepository.searchProducts(query, 0, 50).onSuccess { products ->
                loadingIndicator.visibility = View.GONE
                filteredProducts = products.map { apiProduct ->
                    Product(
                        id = apiProduct.id,
                        name = apiProduct.name,
                        nameEn = apiProduct.nameEn,
                        nameRu = apiProduct.nameRu,
                        description = apiProduct.description,
                        descriptionEn = apiProduct.descriptionEn,
                        descriptionRu = apiProduct.descriptionRu,
                        price = apiProduct.price,
                        imageUrl = apiProduct.imageUrl,
                        category = apiProduct.category,
                        categoryEn = apiProduct.categoryEn,
                        categoryRu = apiProduct.categoryRu,
                        soldCount = apiProduct.soldCount,
                        stock = apiProduct.stock,
                        featured = apiProduct.featured
                    )
                }
                updateProductsList()
                if (filteredProducts.isEmpty()) {
                    Toast.makeText(this@HomeActivity, "未找到相关商品", Toast.LENGTH_SHORT).show()
                }
            }.onFailure {
                loadingIndicator.visibility = View.GONE
                Toast.makeText(this@HomeActivity, "搜索失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProductsFromApi() {
        loadingIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            // Load all products
            val productsResult = productRepository.getProducts(0, 50)
            productsResult.onSuccess { products ->
                allProducts = products.map { apiProduct ->
                    Product(
                        id = apiProduct.id,
                        name = apiProduct.name,
                        nameEn = apiProduct.nameEn,
                        nameRu = apiProduct.nameRu,
                        description = apiProduct.description,
                        descriptionEn = apiProduct.descriptionEn,
                        descriptionRu = apiProduct.descriptionRu,
                        price = apiProduct.price,
                        imageUrl = apiProduct.imageUrl,
                        category = apiProduct.category,
                        categoryEn = apiProduct.categoryEn,
                        categoryRu = apiProduct.categoryRu,
                        soldCount = apiProduct.soldCount,
                        stock = apiProduct.stock,
                        featured = apiProduct.featured
                    )
                }
                filteredProducts = allProducts
                updateProductsList()
            }.onFailure {
                Toast.makeText(this@HomeActivity, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
            
            // Load featured products for banner
            val featuredResult = productRepository.getFeaturedProducts(5)
            featuredResult.onSuccess { featuredProducts ->
                val bannerProducts = featuredProducts.map { apiProduct ->
                    Product(
                        id = apiProduct.id,
                        name = apiProduct.name,
                        nameEn = apiProduct.nameEn,
                        nameRu = apiProduct.nameRu,
                        description = apiProduct.description,
                        descriptionEn = apiProduct.descriptionEn,
                        descriptionRu = apiProduct.descriptionRu,
                        price = apiProduct.price,
                        imageUrl = apiProduct.imageUrl,
                        category = apiProduct.category,
                        categoryEn = apiProduct.categoryEn,
                        categoryRu = apiProduct.categoryRu,
                        soldCount = apiProduct.soldCount,
                        stock = apiProduct.stock,
                        featured = apiProduct.featured
                    )
                }
                if (bannerProducts.isNotEmpty()) {
                    setupBannerWithProducts(bannerProducts)
                }
            }
            
            // Load popular products (featured ones)
            val popularResult = productRepository.getFeaturedProducts(10)
            popularResult.onSuccess { popularApiProducts ->
                val popularProducts = popularApiProducts.map { apiProduct ->
                    Product(
                        id = apiProduct.id,
                        name = apiProduct.name,
                        nameEn = apiProduct.nameEn,
                        nameRu = apiProduct.nameRu,
                        description = apiProduct.description,
                        descriptionEn = apiProduct.descriptionEn,
                        descriptionRu = apiProduct.descriptionRu,
                        price = apiProduct.price,
                        imageUrl = apiProduct.imageUrl,
                        category = apiProduct.category,
                        categoryEn = apiProduct.categoryEn,
                        categoryRu = apiProduct.categoryRu,
                        soldCount = apiProduct.soldCount,
                        stock = apiProduct.stock,
                        featured = apiProduct.featured
                    )
                }
                setupPopularProducts(popularProducts)
            }
            
            loadingIndicator.visibility = View.GONE
        }
    }
    
    private fun setupBannerWithProducts(bannerProducts: List<Product>) {
        bannerAdapter = BannerAdapter(bannerProducts) { product ->
            openProductDetails(product)
        }
        bannerViewPager.adapter = bannerAdapter
    }
    
    private fun setupPopularProducts(popularProducts: List<Product>) {
        popularProductAdapter = ProductHorizontalAdapter(
            popularProducts,
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        popularProductsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        popularProductsRecyclerView.adapter = popularProductAdapter
    }
    
    private fun updateProductsList() {
        productAdapter = ProductAdapter(
            filteredProducts,
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = productAdapter
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
                updateCartBadge()
                Toast.makeText(this@HomeActivity, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@HomeActivity, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCartBadge() {
        lifecycleScope.launch {
            cartRepository.getCart().onSuccess {
                // Future: show item count on cart icon
            }
        }
    }
}

