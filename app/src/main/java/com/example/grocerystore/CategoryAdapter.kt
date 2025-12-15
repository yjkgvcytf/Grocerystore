package com.example.grocerystore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: Category) {
            val context = itemView.context
            val currentLanguage = LocaleHelper.getLocale(context)
            
            categoryName.text = when (currentLanguage) {
                "zh" -> category.name
                "en" -> category.nameEn
                "ru" -> category.nameRu
                else -> category.name
            }
            
            categoryIcon.setImageResource(category.icon)
            categoryIcon.setColorFilter(ContextCompat.getColor(context, R.color.purple_primary))
            
            itemView.setOnClickListener {
                onItemClick(category)
            }
        }
    }
}

