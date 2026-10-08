package com.ssps.stockprediction.ui.inventory.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssps.stockprediction.data.local.entity.ProductEntity
import com.ssps.stockprediction.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddProductUiState(
    val productName: String = "",
    val stockQuantity: String = "",
    val leadTimeDays: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)

class AddProductViewModel(
    private val productRepository: ProductRepository,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    fun onProductNameChange(name: String) {
        _uiState.value = _uiState.value.copy(productName = name, errorMessage = null)
    }

    fun onStockQuantityChange(quantity: String) {
        _uiState.value = _uiState.value.copy(stockQuantity = quantity, errorMessage = null)
    }

    fun onLeadTimeDaysChange(leadTime: String) {
        _uiState.value = _uiState.value.copy(leadTimeDays = leadTime, errorMessage = null)
    }

    fun saveProduct() {
        val state = _uiState.value

        // Validation
        if (state.productName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Product name is required.")
            return
        }

        val stock = state.stockQuantity.toIntOrNull()
        if (stock == null || stock < 0) {
            _uiState.value = state.copy(errorMessage = "Stock must be a non-negative number.")
            return
        }

        val leadTime = state.leadTimeDays.toIntOrNull()
        if (leadTime == null || leadTime < 0) {
            _uiState.value = state.copy(errorMessage = "Lead time must be a non-negative number.")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val product = ProductEntity(
                    userId = userId,
                    name = state.productName.trim(),
                    stockQuantity = stock,
                    leadTimeDays = leadTime
                )
                productRepository.addProduct(product)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to save product: ${e.message}"
                )
            }
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val userId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddProductViewModel(productRepository, userId) as T
        }
    }
}