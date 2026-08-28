package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.DashboardOverview
import com.example.data.model.MenuItemEntity
import com.example.data.model.RiderSummary
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItem
import com.example.data.model.UserEntity
import com.example.data.repository.CheckoutResult
import com.example.data.repository.ChoedooRepository
import com.example.util.FormatUtils
import com.example.util.JsonUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RiderTab {
    KASIR, RIWAYAT, DASHBOARD
}

enum class AdminTab {
    DASHBOARD, RIDER, STOK, TRANSAKSI
}

data class AuthUiState(
    val usernameInput: String = "",
    val passwordInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class AddRiderUiState(
    val username: String = "",
    val name: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class ChoedooViewModel(
    private val repository: ChoedooRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _authUiState = MutableStateFlow(AuthUiState())
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    // Active Navigation Tabs
    private val _riderTab = MutableStateFlow(RiderTab.KASIR)
    val riderTab: StateFlow<RiderTab> = _riderTab.asStateFlow()

    private val _adminTab = MutableStateFlow(AdminTab.DASHBOARD)
    val adminTab: StateFlow<AdminTab> = _adminTab.asStateFlow()

    // Selected Rider for Admin detailed drill-down
    private val _selectedRiderForDetail = MutableStateFlow<UserEntity?>(null)
    val selectedRiderForDetail: StateFlow<UserEntity?> = _selectedRiderForDetail.asStateFlow()

    // Add Rider UI State
    private val _addRiderUiState = MutableStateFlow(AddRiderUiState())
    val addRiderUiState: StateFlow<AddRiderUiState> = _addRiderUiState.asStateFlow()

    // Menu items flow
    val menuItems: StateFlow<List<MenuItemEntity>> = repository.getAllMenuItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All transactions flow
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All riders flow
    val allRiders: StateFlow<List<UserEntity>> = repository.getAllRiders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart state for Rider: Map of menuItemId -> CartItem
    private val _cart = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val cart: StateFlow<Map<Int, CartItem>> = _cart.asStateFlow()

    // Checkout & Dialog UI States
    private val _showCartSheet = MutableStateFlow(false)
    val showCartSheet: StateFlow<Boolean> = _showCartSheet.asStateFlow()

    private val _completedTransaction = MutableStateFlow<Pair<TransactionEntity, List<TransactionItem>>?>(null)
    val completedTransaction: StateFlow<Pair<TransactionEntity, List<TransactionItem>>?> = _completedTransaction.asStateFlow()

    private val _snackMessage = MutableStateFlow<String?>(null)
    val snackMessage: StateFlow<String?> = _snackMessage.asStateFlow()

    private val _isCheckingOut = MutableStateFlow(false)
    val isCheckingOut: StateFlow<Boolean> = _isCheckingOut.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    // AUTH ACTIONS
    fun onUsernameChange(input: String) {
        _authUiState.update { it.copy(usernameInput = input, errorMessage = null) }
    }

    fun onPasswordChange(input: String) {
        _authUiState.update { it.copy(passwordInput = input, errorMessage = null) }
    }

    fun login(quickUsername: String? = null, quickPassword: String? = null) {
        val user = quickUsername ?: _authUiState.value.usernameInput
        val pass = quickPassword ?: _authUiState.value.passwordInput

        if (user.isBlank() || pass.isBlank()) {
            _authUiState.update { it.copy(errorMessage = "Username dan password tidak boleh kosong") }
            return
        }

        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val authenticatedUser = repository.authenticate(user, pass)
            if (authenticatedUser != null) {
                _currentUser.value = authenticatedUser
                _authUiState.value = AuthUiState()
                _cart.value = emptyMap()
                _riderTab.value = RiderTab.KASIR
                _adminTab.value = AdminTab.DASHBOARD
                _selectedRiderForDetail.value = null
            } else {
                _authUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Username atau password salah. Coba lagi."
                    )
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _cart.value = emptyMap()
        _authUiState.value = AuthUiState()
        _selectedRiderForDetail.value = null
        _completedTransaction.value = null
        _showCartSheet.value = false
    }

    // NAVIGATION
    fun setRiderTab(tab: RiderTab) {
        _riderTab.value = tab
    }

    fun setAdminTab(tab: AdminTab) {
        _adminTab.value = tab
        if (tab != AdminTab.RIDER) {
            _selectedRiderForDetail.value = null
        }
    }

    fun selectRiderForDetail(rider: UserEntity?) {
        _selectedRiderForDetail.value = rider
    }

    // CART ACTIONS (Rider)
    fun addToCart(menuItem: MenuItemEntity) {
        if (menuItem.stock <= 0) {
            _snackMessage.value = "Stok ${menuItem.name} sudah HABIS"
            return
        }

        val currentCart = _cart.value
        val existingItem = currentCart[menuItem.id]
        val currentQty = existingItem?.quantity ?: 0

        if (currentQty + 1 > menuItem.stock) {
            _snackMessage.value = "Maksimal stok tercapai (${menuItem.stock} cup)"
            return
        }

        val updatedMap = currentCart.toMutableMap()
        updatedMap[menuItem.id] = CartItem(menuItem, currentQty + 1)
        _cart.value = updatedMap
    }

    fun decreaseOrRemoveFromCart(menuItem: MenuItemEntity) {
        val currentCart = _cart.value
        val existingItem = currentCart[menuItem.id] ?: return

        val updatedMap = currentCart.toMutableMap()
        if (existingItem.quantity > 1) {
            updatedMap[menuItem.id] = CartItem(menuItem, existingItem.quantity - 1)
        } else {
            updatedMap.remove(menuItem.id)
        }
        _cart.value = updatedMap
    }

    fun setItemQuantity(menuItem: MenuItemEntity, quantity: Int) {
        val updatedMap = _cart.value.toMutableMap()
        if (quantity <= 0) {
            updatedMap.remove(menuItem.id)
        } else {
            val validQty = quantity.coerceAtMost(menuItem.stock)
            if (quantity > menuItem.stock) {
                _snackMessage.value = "Stok hanya tersedia ${menuItem.stock} cup"
            }
            updatedMap[menuItem.id] = CartItem(menuItem, validQty)
        }
        _cart.value = updatedMap
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun toggleCartSheet(show: Boolean) {
        _showCartSheet.value = show
    }

    fun dismissReceipt() {
        _completedTransaction.value = null
    }

    fun clearSnackMessage() {
        _snackMessage.value = null
    }

    // CHECKOUT PROCESS
    fun processCheckout() {
        val user = _currentUser.value ?: return
        val cartList = _cart.value.values.toList()

        if (cartList.isEmpty()) {
            _snackMessage.value = "Keranjang masih kosong"
            return
        }

        viewModelScope.launch {
            _isCheckingOut.value = true
            when (val result = repository.processCheckout(user, cartList)) {
                is CheckoutResult.Success -> {
                    _cart.value = emptyMap()
                    _showCartSheet.value = false
                    _completedTransaction.value = Pair(result.transaction, result.items)
                    _snackMessage.value = "Transaksi berhasil disimpan!"
                }
                is CheckoutResult.OutOfStock -> {
                    _snackMessage.value = "Gagal: Stok ${result.itemName} tidak mencukupi (Tersisa: ${result.availableStock} cup, Diminta: ${result.requestedQuantity})"
                }
                is CheckoutResult.Error -> {
                    _snackMessage.value = "Gagal: ${result.message}"
                }
            }
            _isCheckingOut.value = false
        }
    }

    // ADMIN ACTIONS: STOCK MANAGEMENT
    fun addStockToMenu(menuId: Int, amount: Int) {
        viewModelScope.launch {
            val result = repository.addStock(menuId, amount)
            result.onSuccess {
                _snackMessage.value = "Stok berhasil ditambahkan (+$amount cup)"
            }.onFailure {
                _snackMessage.value = it.message ?: "Gagal menambah stok"
            }
        }
    }

    fun setStockForMenu(menuId: Int, newStock: Int) {
        viewModelScope.launch {
            val result = repository.setStock(menuId, newStock)
            result.onSuccess {
                _snackMessage.value = "Stok berhasil diperbarui ($newStock cup)"
            }.onFailure {
                _snackMessage.value = it.message ?: "Gagal mengubah stok"
            }
        }
    }

    // ADMIN ACTIONS: ADD RIDER
    fun onAddRiderUsernameChange(text: String) {
        _addRiderUiState.update { it.copy(username = text, errorMessage = null, isSuccess = false) }
    }

    fun onAddRiderNameChange(text: String) {
        _addRiderUiState.update { it.copy(name = text, errorMessage = null, isSuccess = false) }
    }

    fun onAddRiderPasswordChange(text: String) {
        _addRiderUiState.update { it.copy(password = text, errorMessage = null, isSuccess = false) }
    }

    fun registerRider() {
        val state = _addRiderUiState.value
        viewModelScope.launch {
            _addRiderUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.registerRider(state.username, state.name, state.password)
            result.onSuccess {
                _addRiderUiState.value = AddRiderUiState(isSuccess = true)
                _snackMessage.value = "Rider baru '${state.name}' berhasil ditambahkan!"
            }.onFailure { error ->
                _addRiderUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal mendaftarkan rider"
                    )
                }
            }
        }
    }

    fun resetAddRiderForm() {
        _addRiderUiState.value = AddRiderUiState()
    }
}

class ChoedooViewModelFactory(
    private val repository: ChoedooRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChoedooViewModel::class.java)) {
            return ChoedooViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
