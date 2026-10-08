package com.ssps.stockprediction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ssps.stockprediction.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE userId = :userId ORDER BY name ASC")
    fun getProductsByUser(userId: Long): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Query("UPDATE products SET stockQuantity = stockQuantity - :quantity WHERE id = :productId")
    suspend fun reduceStock(productId: Long, quantity: Int)
}