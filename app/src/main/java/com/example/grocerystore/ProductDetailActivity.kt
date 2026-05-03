package com.example.grocerystore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.grocerystore.repository.AuthRepository
import com.example.grocerystore.repository.CartRepository
import com.example.grocerystore.repository.ProductRepository
import kotlinx.coroutines.launch

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
    private lateinit var loadingIndicator: ProgressBar
    
    private var product: Product? = null
    private lateinit var cartRepository: CartRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var productRepository: ProductRepository

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        cartRepository = CartRepository(this)
        authRepository = AuthRepository(this)
        productRepository = ProductRepository(this)

        initViews()
        setupButtons()
        loadProductFromApi()
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
        loadingIndicator = findViewById(R.id.loadingIndicator)
    }

    private fun loadProductFromApi() {
        val productId = intent.getStringExtra("product_id") ?: return
        
        loadingIndicator.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            productRepository.getProductById(productId).onSuccess { apiProduct ->
                loadingIndicator.visibility = View.GONE
                product = Product(
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
                setupProductInfo()
            }.onFailure { e ->
                loadingIndicator.visibility = View.GONE
                Toast.makeText(this@ProductDetailActivity, "Failed to load product: ${e.message}", Toast.LENGTH_SHORT).show()
                // Fallback to local data
                product = ProductData.getSampleProducts().find { it.id == productId }
                if (product != null) {
                    setupProductInfo()
                }
            }
        }
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
            
            // Load product image
            ImageHelper.loadProductImage(this, productImage, p.imageUrl)
        }
    }

    private fun setupButtons() {
        backButton.setOnClickListener {
            finish()
        }
        
        addToCartButton.setOnClickListener {
            product?.let { p ->
                lifecycleScope.launch {
                    cartRepository.addToCart(p.id, 1).onSuccess {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            getString(R.string.added_to_cart),
                            Toast.LENGTH_SHORT
                        ).show()
                    }.onFailure {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Failed to add to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        
        buyNowButton.setOnClickListener {
            product?.let { p ->
                lifecycleScope.launch {
                    cartRepository.addToCart(p.id, 1).onSuccess {
                        if (!authRepository.isLoggedIn()) {
                            Toast.makeText(
                                this@ProductDetailActivity,
                                "请先登录",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@ProductDetailActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@ProductDetailActivity, OrderActivity::class.java)
                            intent.putExtra("is_new_order", true)
                            startActivity(intent)
                        }
                    }.onFailure {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Failed to add to cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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

