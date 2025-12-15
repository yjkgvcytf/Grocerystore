package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryProductsActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var categoryTitle: TextView
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyLayout: View
    
    private lateinit var productAdapter: ProductGridAdapter
    private var categoryId: String? = null
    private var category: Category? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category_products)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        val products = categoryId?.let { CategoryData.getProductsByCategoryId(it) } ?: emptyList()
        
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
        CartManager.addToCart(product, 1)
        Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh adapter to update language
        setupRecyclerView()
        setupTitle()
    }
}




