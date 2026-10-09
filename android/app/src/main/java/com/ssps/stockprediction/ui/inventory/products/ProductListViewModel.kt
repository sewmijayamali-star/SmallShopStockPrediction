package com.ssps.stockprediction.ui.inventory.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ssps.stockprediction.SessionManager
import com.ssps.stockprediction.data.local.entity.ProductEntity
import com.ssps.stockprediction.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductListUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val sessionManager: SessionManager,
    private val userId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                productRepository.getProductsByUser(userId).collect { products ->
                    _uiState.value = ProductListUiState(
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

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
        }
    }

    class Factory(
        private val productRepository: ProductRepository,
        private val sessionManager: SessionManager,
        private val userId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductListViewModel(productRepository, sessionManager, userId) as T
        }
    }
}