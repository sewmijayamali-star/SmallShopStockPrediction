package com.ssps.stockprediction.ui.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssps.stockprediction.data.repository.ProductRepository
import com.ssps.stockprediction.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class PredictionUiState(
    val averageDailyDemand: Double = 0.0,
    val estimatedDaysRemaining: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PredictionViewModel(
    private val productRepository: ProductRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun loadPrediction(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val product = productRepository.getProductById(productId)
                if (product == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Product not found."
                    )
                    return@launch
                }

                val totalSold = saleRepository.getTotalQuantitySold(productId)
                val earliest = saleRepository.getEarliestSaleTimestamp(productId)
                val latest = saleRepository.getLatestSaleTimestamp(productId)

                val numberOfDays = if (earliest != null && latest != null && latest > earliest) {
                    val diffMillis = latest - earliest
                    maxOf(1, TimeUnit.MILLISECONDS.toDays(diffMillis).toInt())
                } else if (totalSold > 0) {
                    1 // At least 1 day if there are sales on the same day
                } else {
                    0
                }

                val result = DemandPredictor.calculatePrediction(
                    totalQuantitySold = totalSold,
                    numberOfDays = numberOfDays,
                    currentStock = product.stockQuantity
                )

                _uiState.value = PredictionUiState(
                    averageDailyDemand = result.averageDailyDemand,
                    estimatedDaysRemaining = result.estimatedDaysRemaining,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Prediction failed: ${e.message}"
                )
            }
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val saleRepository: SaleRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PredictionViewModel(productRepository, saleRepository) as T
        }
    }
}
