package com.example.grocerystore

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView

object ImageHelper {

    /**
     * Maps the English asset filenames stored in the database to the Chinese-named
     * image files in assets/images/ so each product shows the correct photo.
     */
    private val englishToChineseFileName = mapOf(
        "product_bamboo_towel.png"       to "竹纤维毛巾.png",
        "product_electric_toothbrush.png" to "电动牙刷.png",
        "product_shampoo.png"             to "洗发水.png",
        "product_liquid_soap.png"         to "液体肥皂.png",
        "product_thermos.png"             to "保温杯.png",
        "product_organic_rice.png"        to "有机大米.png",
        "product_fresh_apples.png"        to "新鲜苹果.png",
        "product_laundry_detergent.png"  to "洗衣液.png"
    )

    /**
     * Resolves [imageUrl] to the actual file under assets/images/.
     * - API / DB: `product_xxx.png` → mapped to Chinese `xxx.png`
     * - [ProductData] local samples: Chinese base name without extension, e.g. `新鲜苹果` → `新鲜苹果.png`
     */
    private fun resolveAssetFileName(imageUrl: String): String {
        englishToChineseFileName[imageUrl]?.let { return it }
        val t = imageUrl.trim()
        return when {
            t.endsWith(".png", ignoreCase = true) ||
                t.endsWith(".jpg", ignoreCase = true) ||
                t.endsWith(".jpeg", ignoreCase = true) -> t
            else -> "$t.png"
        }
    }

    /**
     * Loads the product image into the ImageView.
     * Tries assets/images/ first (using the English→Chinese name map), then
     * falls back to a drawable resource with the same base name, and finally
     * to the system gallery icon.
     */
    fun loadProductImage(context: Context, imageView: ImageView, imageUrl: String?) {
        if (imageUrl.isNullOrEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
            return
        }

        // 1. Try assets/images/
        val assetFileName = resolveAssetFileName(imageUrl)
        try {
            context.assets.open("images/$assetFileName").use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    return
                }
            }
        } catch (_: Exception) {
            // not found in assets — continue to fallback
        }

        // 2. Fallback: try loading as a drawable resource (uses the base name without extension)
        val baseName = imageUrl
            .removeSuffix(".png")
            .removeSuffix(".jpg")
            .removeSuffix(".jpeg")
        val resourceId = context.resources.getIdentifier(
            baseName,
            "drawable",
            context.packageName
        )
        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }
}

