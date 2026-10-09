package com.ssps.stockprediction

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ssps.stockprediction.ui.auth.login.LoginScreen
import com.ssps.stockprediction.ui.auth.login.LoginViewModel
import com.ssps.stockprediction.ui.auth.registration.RegistrationScreen
import com.ssps.stockprediction.ui.auth.registration.RegistrationViewModel
import com.ssps.stockprediction.ui.inventory.addproduct.AddProductScreen
import com.ssps.stockprediction.ui.inventory.addproduct.AddProductViewModel
import com.ssps.stockprediction.ui.inventory.products.ProductListScreen
import com.ssps.stockprediction.ui.inventory.products.ProductListViewModel
import com.ssps.stockprediction.ui.sales.RecordSaleScreen
import com.ssps.stockprediction.ui.sales.RecordSaleViewModel
import com.ssps.stockprediction.ui.theme.StockPredictionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as StockPredictionApp

        setContent {
            StockPredictionTheme {
                val navController = rememberNavController()

                // Observe the session state
                val loggedInUserId by app.sessionManager.loggedInUserId
                    .collectAsState(initial = null)

                // Track whether we've finished checking session
                var sessionChecked by remember { mutableStateOf(false) }
                var initialUserId by remember { mutableStateOf<Long?>(null) }

                // We need to wait for the first emission to determine the start destination
                LaunchedEffect(Unit) {
                    app.sessionManager.loggedInUserId.collect { userId ->
                        if (!sessionChecked) {
                            initialUserId = userId
                            sessionChecked = true
                        }
                    }
                }

                if (!sessionChecked) {
                    // Show loading while checking session
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    val startDestination = if (initialUserId != null) "productList" else "login"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = LoginViewModel.Factory(
                                    app.userRepository,
                                    app.sessionManager
                                )
                            )
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate("productList") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("registration")
                                }
                            )
                        }

                        composable("registration") {
                            val registrationViewModel: RegistrationViewModel = viewModel(
                                factory = RegistrationViewModel.Factory(app.userRepository)
                            )
                            RegistrationScreen(
                                viewModel = registrationViewModel,
                                onRegistrationSuccess = {
                                    navController.popBackStack()
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("productList") {
                            val userId = loggedInUserId ?: initialUserId ?: 0L
                            val productListViewModel: ProductListViewModel = viewModel(
                                factory = ProductListViewModel.Factory(
                                    app.productRepository,
                                    app.sessionManager,
                                    userId
                                )
                            )
                            ProductListScreen(
                                viewModel = productListViewModel,
                                onNavigateToAddProduct = {
                                    navController.navigate("addProduct")
                                },
                                onNavigateToRecordSale = {
                                    navController.navigate("recordSale")
                                },
                                onLogout = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("addProduct") {
                            val userId = loggedInUserId ?: initialUserId ?: 0L
                            val addProductViewModel: AddProductViewModel = viewModel(
                                factory = AddProductViewModel.Factory(
                                    app.productRepository,
                                    userId
                                )
                            )
                            AddProductScreen(
                                viewModel = addProductViewModel,
                                onProductSaved = {
                                    navController.popBackStack()
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("recordSale") {
                            val userId = loggedInUserId ?: initialUserId ?: 0L
                            val recordSaleViewModel: RecordSaleViewModel = viewModel(
                                factory = RecordSaleViewModel.Factory(
                                    app.saleRepository,
                                    app.productRepository,
                                    userId
                                )
                            )
                            RecordSaleScreen(
                                viewModel = recordSaleViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}