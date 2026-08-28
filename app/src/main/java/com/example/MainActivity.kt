package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.db.ChoedooDatabase
import com.example.data.repository.ChoedooRepository
import com.example.ui.screens.AdminMainScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.RiderMainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ChoedooViewModel
import com.example.ui.viewmodel.ChoedooViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ChoedooDatabase.getDatabase(applicationContext, (this as ComponentActivity).lifecycle.let {
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
        })
        val repository = ChoedooRepository(database)
        val viewModelFactory = ChoedooViewModelFactory(repository)
        val viewModel: ChoedooViewModel by viewModels { viewModelFactory }

        setContent {
            MyApplicationTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
                val riderTab by viewModel.riderTab.collectAsStateWithLifecycle()
                val adminTab by viewModel.adminTab.collectAsStateWithLifecycle()
                val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
                val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
                val allRiders by viewModel.allRiders.collectAsStateWithLifecycle()
                val selectedRiderForDetail by viewModel.selectedRiderForDetail.collectAsStateWithLifecycle()
                val addRiderUiState by viewModel.addRiderUiState.collectAsStateWithLifecycle()
                val cart by viewModel.cart.collectAsStateWithLifecycle()
                val showCartSheet by viewModel.showCartSheet.collectAsStateWithLifecycle()
                val completedTransaction by viewModel.completedTransaction.collectAsStateWithLifecycle()
                val isCheckingOut by viewModel.isCheckingOut.collectAsStateWithLifecycle()
                val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()

                LaunchedEffect(snackMessage) {
                    snackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearSnackMessage()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val user = currentUser
                    if (user == null) {
                        LoginScreen(
                            authUiState = authUiState,
                            onUsernameChange = viewModel::onUsernameChange,
                            onPasswordChange = viewModel::onPasswordChange,
                            onLoginClick = { viewModel.login() },
                            onQuickLogin = { u, p -> viewModel.login(u, p) },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else if (user.role == "ADMIN") {
                        AdminMainScreen(
                            currentUser = user,
                            adminTab = adminTab,
                            onTabSelect = viewModel::setAdminTab,
                            menuItems = menuItems,
                            allTransactions = allTransactions,
                            allRiders = allRiders,
                            selectedRiderForDetail = selectedRiderForDetail,
                            onSelectRiderForDetail = viewModel::selectRiderForDetail,
                            addRiderUiState = addRiderUiState,
                            onAddRiderUsernameChange = viewModel::onAddRiderUsernameChange,
                            onAddRiderNameChange = viewModel::onAddRiderNameChange,
                            onAddRiderPasswordChange = viewModel::onAddRiderPasswordChange,
                            onRegisterRider = viewModel::registerRider,
                            onResetAddRiderForm = viewModel::resetAddRiderForm,
                            onAddStock = viewModel::addStockToMenu,
                            onSetStock = viewModel::setStockForMenu,
                            onLogoutClick = viewModel::logout,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        // Rider Role
                        RiderMainScreen(
                            currentUser = user,
                            riderTab = riderTab,
                            onTabSelect = viewModel::setRiderTab,
                            menuItems = menuItems,
                            cart = cart,
                            allTransactions = allTransactions,
                            showCartSheet = showCartSheet,
                            onToggleCartSheet = viewModel::toggleCartSheet,
                            onAddToCart = viewModel::addToCart,
                            onDecreaseCart = viewModel::decreaseOrRemoveFromCart,
                            onClearCart = viewModel::clearCart,
                            isCheckingOut = isCheckingOut,
                            onCheckout = viewModel::processCheckout,
                            completedTransaction = completedTransaction,
                            onDismissReceipt = viewModel::dismissReceipt,
                            onLogoutClick = viewModel::logout,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
