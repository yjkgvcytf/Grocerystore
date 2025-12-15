package com.example.grocerystore

import android.app.Application
import android.content.Context

class GroceryStoreApplication : Application() {
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}




