package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BannerAdapter(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bannerImage: ImageView = itemView.findViewById(R.id.bannerImage)
        private val bannerProductName: TextView = itemView.findViewById(R.id.bannerProductName)
        private val bannerProductDescription: TextView = itemView.findViewById(R.id.bannerProductDescription)

        fun bind(product: Product) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            bannerProductName.text = when (currentLanguage) {
                "zh" -> product.name
                "en" -> product.nameEn
                "ru" -> product.nameRu
                else -> product.name
            }
            
            bannerProductDescription.text = when (currentLanguage) {
                "zh" -> product.description
                "en" -> product.descriptionEn
                "ru" -> product.descriptionRu
                else -> product.description
            }
            
            // Load product image
            ImageHelper.loadProductImage(context, bannerImage, product.imageUrl)
            
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }
}

