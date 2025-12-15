package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductGridAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_grid, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productSold: TextView = itemView.findViewById(R.id.productSold)
        private val addToCartButton: ImageView = itemView.findViewById(R.id.addToCartButton)

        fun bind(product: Product) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            productName.text = when (currentLanguage) {
                "zh" -> product.name
                "en" -> product.nameEn
                "ru" -> product.nameRu
                else -> product.name
            }
            
            productDescription.text = when (currentLanguage) {
                "zh" -> product.description
                "en" -> product.descriptionEn
                "ru" -> product.descriptionRu
                else -> product.description
            }
            
            productPrice.text = "¥${product.price}"
            productSold.text = "${formatSoldCount(product.soldCount)} ${context.getString(R.string.sold)}"
            
            // Set placeholder image
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            
            itemView.setOnClickListener {
                onItemClick(product)
            }
            
            addToCartButton.setOnClickListener {
                onAddToCart(product)
            }
        }
        
        private fun formatSoldCount(count: Int): String {
            return when {
                count >= 1000 -> "${count / 1000.0}k"
                else -> count.toString()
            }
        }
    }
}




