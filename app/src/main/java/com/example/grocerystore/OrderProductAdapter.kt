package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerystore.api.OrderItem

class OrderProductAdapter(
    private val orderItems: List<OrderItem>
) : RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_product, parent, false)
        return OrderProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderProductViewHolder, position: Int) {
        holder.bind(orderItems[position])
    }

    override fun getItemCount(): Int = orderItems.size

    inner class OrderProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val unitPrice: TextView = itemView.findViewById(R.id.unitPrice)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)

        fun bind(orderItem: OrderItem) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            productName.text = when (currentLanguage) {
                "zh" -> orderItem.productName
                "en" -> orderItem.productNameEn
                else -> orderItem.productName
            } ?: "Product"
            
            // Display unit price
            unitPrice.text = "${context.getString(R.string.unit_price)}: ¥${orderItem.unitPrice}"
            
            // Display quantity
            quantityText.text = "${context.getString(R.string.quantity)}: ${orderItem.quantity}"
            
            // Display subtotal
            productPrice.text = "¥${orderItem.subtotal}"
            
            // Load product image
            ImageHelper.loadProductImage(context, productImage, orderItem.imageUrl)
        }
    }
}

