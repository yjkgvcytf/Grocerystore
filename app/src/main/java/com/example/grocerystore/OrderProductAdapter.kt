package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderProductAdapter(
    private val cartItems: List<CartItem>
) : RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class OrderProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val unitPrice: TextView = itemView.findViewById(R.id.unitPrice)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)

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
            
            // Display unit price
            unitPrice.text = "${context.getString(R.string.unit_price)}: ¥${product.price}"
            
            // Display quantity
            quantityText.text = "${context.getString(R.string.quantity)}: ${cartItem.quantity}"
            
            // Display subtotal
            productPrice.text = "¥${cartItem.getTotalPrice()}"
            
            // Set placeholder image
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }
}

