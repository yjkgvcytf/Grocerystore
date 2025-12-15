package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProductDetailActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageButton
    private lateinit var productImage: ImageView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productSold: TextView
    private lateinit var productStock: TextView
    private lateinit var productDescription: TextView
    private lateinit var addToCartButton: Button
    private lateinit var buyNowButton: Button
    
    private var product: Product? = null

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_detail)
        
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productId = intent.getStringExtra("product_id")
        product = ProductData.getSampleProducts().find { it.id == productId }

        initViews()
        setupProductInfo()
        setupButtons()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        productImage = findViewById(R.id.productImage)
        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productSold = findViewById(R.id.productSold)
        productStock = findViewById(R.id.productStock)
        productDescription = findViewById(R.id.productDescription)
        addToCartButton = findViewById(R.id.addToCartButton)
        buyNowButton = findViewById(R.id.buyNowButton)
    }

    private fun setupProductInfo() {
        product?.let { p ->
            val currentLanguage = LocaleHelper.getLocale(this)
            
            productName.text = when (currentLanguage) {
                "zh" -> p.name
                "en" -> p.nameEn
                "ru" -> p.nameRu
                else -> p.name
            }
            
            productDescription.text = when (currentLanguage) {
                "zh" -> p.description
                "en" -> p.descriptionEn
                "ru" -> p.descriptionRu
                else -> p.description
            }
            
            productPrice.text = "¥${p.price}"
            productSold.text = "${formatSoldCount(p.soldCount)} ${getString(R.string.sold)}"
            productStock.text = "${getString(R.string.stock)}: ${p.stock}"
            
            // Set placeholder image
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }
        
        addToCartButton.setOnClickListener {
            product?.let { p ->
                CartManager.addToCart(p, 1)
                android.widget.Toast.makeText(
                    this,
                    getString(R.string.added_to_cart),
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
        
        buyNowButton.setOnClickListener {
            product?.let { p ->
                // Add to cart first
                CartManager.addToCart(p, 1)
                
                // Check login status
                val user = UserManager.getCurrentUser(this)
                if (user == null) {
                    android.widget.Toast.makeText(
                        this,
                        "请先登录",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                } else {
                    // Navigate to order preview page
                    val intent = Intent(this, OrderActivity::class.java)
                    intent.putExtra("is_new_order", true)
                    startActivity(intent)
                }
            }
        }
    }

    private fun formatSoldCount(count: Int): String {
        return when {
            count >= 1000 -> "${count / 1000.0}k"
            else -> count.toString()
        }
    }
}

