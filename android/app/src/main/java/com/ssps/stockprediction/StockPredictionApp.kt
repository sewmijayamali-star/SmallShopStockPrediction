package com.ssps.stockprediction

import android.app.Application
import com.ssps.stockprediction.data.local.database.AppDatabase
import com.ssps.stockprediction.data.repository.ProductRepository
import com.ssps.stockprediction.data.repository.SaleRepository
import com.ssps.stockprediction.data.repository.UserRepository

/**
 * Application class providing manual dependency injection.
 * Initializes the database and repositories once for the entire app lifecycle.
 */
class StockPredictionApp : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var userRepository: UserRepository
        private set
    lateinit var productRepository: ProductRepository
        private set
    lateinit var saleRepository: SaleRepository
        private set
    lateinit var sessionManager: SessionManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        userRepository = UserRepository(database.userDao())
        productRepository = ProductRepository(database.productDao())
        saleRepository = SaleRepository(database.saleDao(), database.productDao())
        sessionManager = SessionManager(this)
    }
}
