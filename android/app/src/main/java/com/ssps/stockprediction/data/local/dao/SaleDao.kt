package com.ssps.stockprediction.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.ssps.stockprediction.data.local.entity.SaleEntity

@Dao
interface SaleDao {

    @Insert
    suspend fun insertSale(sale: SaleEntity): Long

    @Query("SELECT * FROM sales WHERE productId = :productId ORDER BY saleTimestamp DESC")
    suspend fun getSalesByProduct(productId: Long): List<SaleEntity>

    /**
     * Returns the total quantity sold for a product.
     */
    @Query("SELECT COALESCE(SUM(quantitySold), 0) FROM sales WHERE productId = :productId")
    suspend fun getTotalQuantitySold(productId: Long): Int

    /**
     * Returns the earliest sale timestamp for a product, or null if no sales exist.
     */
    @Query("SELECT MIN(saleTimestamp) FROM sales WHERE productId = :productId")
    suspend fun getEarliestSaleTimestamp(productId: Long): Long?

    /**
     * Returns the latest sale timestamp for a product, or null if no sales exist.
     */
    @Query("SELECT MAX(saleTimestamp) FROM sales WHERE productId = :productId")
    suspend fun getLatestSaleTimestamp(productId: Long): Long?

    @Query("SELECT COUNT(*) FROM sales WHERE productId = :productId")
    suspend fun getSaleCountForProduct(productId: Long): Int
}
<<<<<<< HEAD

=======
>>>>>>> main
