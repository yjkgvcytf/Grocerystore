package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.grocerystore.api.Order as ApiOrder

class OrderAdapter(
    private val orders: List<ApiOrder>,
    private val onItemClick: (ApiOrder) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val orderNumber: TextView = itemView.findViewById(R.id.orderNumber)
        private val orderStatus: TextView = itemView.findViewById(R.id.orderStatus)
        private val orderItemsPreview: TextView = itemView.findViewById(R.id.orderItemsPreview)
        private val orderDate: TextView = itemView.findViewById(R.id.orderDate)
        private val orderTotal: TextView = itemView.findViewById(R.id.orderTotal)

        fun bind(order: ApiOrder) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            // Display order number (without prefix)
            orderNumber.text = order.orderNumber
            
            // Display order status with appropriate background
            orderStatus.text = getStatusText(order.status, context)
            
            // Set status background color
            orderStatus.background = getStatusBackground(order.status, context)
            
            // Create items preview
            val itemsPreview = order.items.take(3).joinToString(", ") { item ->
                val productName = when (currentLanguage) {
                    "zh" -> item.productName
                    "en" -> item.productNameEn
                    else -> item.productName
                } ?: "Product"
                "$productName x${item.quantity}"
            }
            if (order.items.size > 3) {
                orderItemsPreview.text = "$itemsPreview..."
            } else {
                orderItemsPreview.text = itemsPreview
            }
            
            // Format date - API returns ISO string
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                val date = inputFormat.parse(order.orderDate)
                orderDate.text = date?.let { outputFormat.format(it) } ?: order.orderDate
            } catch (e: Exception) {
                orderDate.text = order.orderDate
            }
            
            orderTotal.text = "¥${String.format("%.2f", order.finalTotal)}"
            
            itemView.setOnClickListener {
                onItemClick(order)
            }
        }
        
        private fun getStatusText(status: String, context: android.content.Context): String {
            return when (status.lowercase()) {
                "pending" -> context.getString(R.string.pending)
                "processing" -> context.getString(R.string.processing)
                "shipped" -> context.getString(R.string.shipped)
                "delivered" -> context.getString(R.string.delivered)
                "cancelled" -> context.getString(R.string.cancelled)
                else -> status
            }
        }
        
        private fun getStatusBackground(status: String, context: android.content.Context): android.graphics.drawable.Drawable? {
            return when (status.lowercase()) {
                "delivered" -> ContextCompat.getDrawable(context, R.drawable.status_delivered)
                "shipped" -> ContextCompat.getDrawable(context, R.drawable.status_shipped)
                "processing" -> ContextCompat.getDrawable(context, R.drawable.status_processing)
                "pending" -> ContextCompat.getDrawable(context, R.drawable.status_pending)
                "cancelled" -> ContextCompat.getDrawable(context, R.drawable.status_pending)
                else -> ContextCompat.getDrawable(context, R.drawable.status_pending)
            }
        }
    }
}
