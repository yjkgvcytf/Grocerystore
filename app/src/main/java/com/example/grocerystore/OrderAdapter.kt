package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val orders: List<Order>,
    private val onItemClick: (Order) -> Unit
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

        fun bind(order: Order) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            // Display order number (without prefix)
            orderNumber.text = order.orderNumber
            
            // Display order status with appropriate background
            orderStatus.text = when (order.status) {
                OrderStatus.PENDING -> context.getString(R.string.pending)
                OrderStatus.PROCESSING -> context.getString(R.string.processing)
                OrderStatus.SHIPPED -> context.getString(R.string.shipped)
                OrderStatus.DELIVERED -> context.getString(R.string.delivered)
                OrderStatus.CANCELLED -> context.getString(R.string.cancelled)
            }
            
            // Set status background color
            orderStatus.background = when (order.status) {
                OrderStatus.DELIVERED -> ContextCompat.getDrawable(context, R.drawable.status_delivered)
                OrderStatus.SHIPPED -> ContextCompat.getDrawable(context, R.drawable.status_shipped)
                OrderStatus.PROCESSING -> ContextCompat.getDrawable(context, R.drawable.status_processing)
                OrderStatus.PENDING -> ContextCompat.getDrawable(context, R.drawable.status_pending)
                OrderStatus.CANCELLED -> ContextCompat.getDrawable(context, R.drawable.status_pending)
            }
            
            // Create items preview
            val itemsPreview = order.items.take(3).joinToString(", ") { item ->
                val productName = when (currentLanguage) {
                    "zh" -> item.product.name
                    "en" -> item.product.nameEn
                    "ru" -> item.product.nameRu
                    else -> item.product.name
                }
                "$productName x${item.quantity}"
            }
            if (order.items.size > 3) {
                orderItemsPreview.text = "$itemsPreview..."
            } else {
                orderItemsPreview.text = itemsPreview
            }
            
            // Format date as dd.MM.yyyy
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            orderDate.text = dateFormat.format(order.orderDate)
            
            orderTotal.text = "¥${String.format("%.2f", order.finalTotal)}"
            
            itemView.setOnClickListener {
                onItemClick(order)
            }
        }
    }
}
