package com.example.grocerystore

import android.app.Application
import android.content.Context

class GroceryStoreApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        UserManager.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}




