package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoriesActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var languageSpinner: Spinner
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var popularProductsRecyclerView: RecyclerView
    private lateinit var navHome: View
    private lateinit var navCategories: View
    private lateinit var navCart: View
    private lateinit var navOrders: View
    private lateinit var navProfile: View
    
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var popularProductAdapter: ProductGridAdapter

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categories)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupLanguageSpinner()
        setupCategoriesRecyclerView()
        setupPopularProductsRecyclerView()
        setupBottomNavigation()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        languageSpinner = findViewById(R.id.languageSpinner)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        popularProductsRecyclerView = findViewById(R.id.popularProductsRecyclerView)
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
                
                val currentLang = LocaleHelper.getLocale(this@CategoriesActivity)
                if (languageCode != currentLang) {
                    LocaleHelper.setLocale(this@CategoriesActivity, languageCode)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupCategoriesRecyclerView() {
        val categories = CategoryData.getCategories()
        categoryAdapter = CategoryAdapter(categories) { category ->
            // Navigate to category products page
            val intent = Intent(this, CategoryProductsActivity::class.java)
            intent.putExtra("category_id", category.id)
            startActivity(intent)
        }
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = categoryAdapter
    }

    private fun setupPopularProductsRecyclerView() {
        val popularProducts = ProductData.getPopularProducts()
        popularProductAdapter = ProductGridAdapter(
            popularProducts,
            onItemClick = { product -> openProductDetails(product) },
            onAddToCart = { product -> addToCart(product) }
        )
        // Use GridLayoutManager with 2 columns
        popularProductsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        popularProductsRecyclerView.adapter = popularProductAdapter
    }

    private fun setupBottomNavigation() {
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        navCategories.setOnClickListener {
            // Already on categories page
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

    private fun openProductDetails(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product_id", product.id)
        startActivity(intent)
    }

    private fun addToCart(product: Product) {
        CartManager.addToCart(product, 1)
        Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh adapters to update language
        setupCategoriesRecyclerView()
        setupPopularProductsRecyclerView()
    }
}

