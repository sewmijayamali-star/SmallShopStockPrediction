package com.ssps.stockprediction.data.repository

import com.ssps.stockprediction.data.local.dao.ProductDao
import com.ssps.stockprediction.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository for product-related operations.
 */
class ProductRepository(private val productDao: ProductDao) {

    fun getProductsByUser(userId: Long): Flow<List<ProductEntity>> {
        return productDao.getProductsByUser(userId)
    }

    suspend fun getProductById(productId: Long): ProductEntity? {
        return productDao.getProductById(productId)
    }

    suspend fun addProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }
}
