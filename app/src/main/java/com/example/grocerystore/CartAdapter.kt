package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
        private val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
        private val removeButton: TextView = itemView.findViewById(R.id.removeButton)

        fun bind(cartItem: CartItem) {
            val context = itemView.context
            val product = cartItem.product
            val currentLanguage = LocaleHelper.getLocale(context)
            
            productName.text = when (currentLanguage) {
                "zh" -> product.name
                "en" -> product.nameEn
                "ru" -> product.nameRu
                else -> product.name
            }
            
            productPrice.text = "¥${cartItem.getTotalPrice()}"
            quantityText.text = cartItem.quantity.toString()
            
            // Set placeholder image
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
            
            decreaseButton.setOnClickListener {
                if (cartItem.quantity > 1) {
                    CartManager.updateQuantity(product.id, cartItem.quantity - 1)
                    onQuantityChanged()
                }
            }
            
            increaseButton.setOnClickListener {
                CartManager.updateQuantity(product.id, cartItem.quantity + 1)
                onQuantityChanged()
            }
            
            removeButton.setOnClickListener {
                CartManager.removeFromCart(product.id)
                onQuantityChanged()
            }
        }
    }
}




