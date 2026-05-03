package com.example.grocerystore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerystore.repository.CartRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onQuantityChanged: () -> Unit,
    private val context: Context
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartRepository = CartRepository(context)

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
            
            // Load product image
            ImageHelper.loadProductImage(context, productImage, product.imageUrl)
            
            decreaseButton.setOnClickListener {
                val newQuantity = cartItem.quantity - 1
                if (newQuantity >= 1) {
                    updateQuantityAPI(cartItem, newQuantity)
                }
            }
            
            increaseButton.setOnClickListener {
                updateQuantityAPI(cartItem, cartItem.quantity + 1)
            }
            
            removeButton.setOnClickListener {
                removeFromCartAPI(cartItem)
            }
        }
        
        private fun updateQuantityAPI(cartItem: CartItem, newQuantity: Int) {
            val itemId = cartItem.id ?: return
            
            CoroutineScope(Dispatchers.Main).launch {
                cartRepository.updateCartItem(itemId, newQuantity).onSuccess {
                    onQuantityChanged()
                }.onFailure {
                    Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        private fun removeFromCartAPI(cartItem: CartItem) {
            val itemId = cartItem.id ?: return
            
            CoroutineScope(Dispatchers.Main).launch {
                cartRepository.removeFromCart(itemId).onSuccess {
                    onQuantityChanged()
                }.onFailure {
                    Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
