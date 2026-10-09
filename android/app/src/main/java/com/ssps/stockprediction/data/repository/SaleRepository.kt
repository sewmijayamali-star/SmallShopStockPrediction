package com.ssps.stockprediction.data.repository

import com.ssps.stockprediction.data.local.dao.ProductDao
import com.ssps.stockprediction.data.local.dao.SaleDao
import com.ssps.stockprediction.data.local.entity.SaleEntity

/**
 * Repository for sale-related operations.
 * Coordinates sale recording with stock reduction.
 */
class SaleRepository(
    private val saleDao: SaleDao,
    private val productDao: ProductDao
) {

    /**
     * Records a sale and reduces stock atomically.
     * Returns Result.success(saleId) or Result.failure with an error message.
     */
    suspend fun recordSale(productId: Long, quantity: Int): Result<Long> {
        return try {
            val product = productDao.getProductById(productId)
                ?: return Result.failure(Exception("Product not found."))

            if (quantity > product.stockQuantity) {
                return Result.failure(
                    Exception("Insufficient stock. Available: ${product.stockQuantity}, Requested: $quantity")
                )
            }

            if (quantity <= 0) {
                return Result.failure(Exception("Quantity must be greater than zero."))
            }

            // Record the sale
            val sale = SaleEntity(
                productId = productId,
                quantitySold = quantity,
                saleTimestamp = System.currentTimeMillis()
            )
            val saleId = saleDao.insertSale(sale)

            // Reduce stock
            productDao.reduceStock(productId, quantity)

            Result.success(saleId)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to record sale: ${e.message}"))
        }
    }

    suspend fun getSalesByProduct(productId: Long): List<SaleEntity> {
        return saleDao.getSalesByProduct(productId)
    }

    suspend fun getTotalQuantitySold(productId: Long): Int {
        return saleDao.getTotalQuantitySold(productId)
    }

    suspend fun getEarliestSaleTimestamp(productId: Long): Long? {
        return saleDao.getEarliestSaleTimestamp(productId)
    }

    suspend fun getLatestSaleTimestamp(productId: Long): Long? {
        return saleDao.getLatestSaleTimestamp(productId)
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> main
