package com.ssps.stockprediction.ui.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssps.stockprediction.data.local.entity.ProductEntity
import com.ssps.stockprediction.data.repository.ProductRepository
import com.ssps.stockprediction.data.repository.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecordSaleUiState(
    val products: List<ProductEntity> = emptyList(),
    val selectedProduct: ProductEntity? = null,
    val quantity: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSaleRecorded: Boolean = false
)

class RecordSaleViewModel(
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordSaleUiState())
    val uiState: StateFlow<RecordSaleUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                productRepository.getProductsByUser(userId).collect { products ->
                    _uiState.value = _uiState.value.copy(
                        products = products,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load products: ${e.message}"
                )
            }
        }
    }

    fun onProductSelected(product: ProductEntity) {
        _uiState.value = _uiState.value.copy(
            selectedProduct = product,
            errorMessage = null,
            successMessage = null
        )
    }

    fun onQuantityChange(quantity: String) {
        _uiState.value = _uiState.value.copy(
            quantity = quantity,
            errorMessage = null,
            successMessage = null
        )
    }

    fun recordSale() {
        val state = _uiState.value

        val product = state.selectedProduct
        if (product == null) {
            _uiState.value = state.copy(errorMessage = "Please select a product.")
            return
        }

        val qty = state.quantity.toIntOrNull()
        if (qty == null || qty <= 0) {
            _uiState.value = state.copy(errorMessage = "Please enter a valid quantity greater than zero.")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null, successMessage = null)

        viewModelScope.launch {
            val result = saleRepository.recordSale(product.id, qty)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Sale recorded! Sold $qty × ${product.name}.",
                        quantity = "",
                        selectedProduct = null,
                        isSaleRecorded = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to record sale."
                    )
                }
            )
        }
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, isSaleRecorded = false)
    }

    class Factory(
        private val saleRepository: SaleRepository,
        private val productRepository: ProductRepository,
        private val userId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RecordSaleViewModel(saleRepository, productRepository, userId) as T
        }
    }
}
