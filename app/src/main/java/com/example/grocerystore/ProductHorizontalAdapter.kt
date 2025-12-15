package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductHorizontalAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductHorizontalAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_horizontal, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)

        fun bind(product: Product) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            productName.text = when (currentLanguage) {
                "zh" -> product.name
                "en" -> product.nameEn
                "ru" -> product.nameRu
                else -> product.name
            }
            
            productPrice.text = "¥${product.price}"
            
            // Set placeholder image
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            
            itemView.setOnClickListener {
                onItemClick(product)
            }
            
            addToCartButton.setOnClickListener {
                onAddToCart(product)
            }
        }
    }
}




