package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.db.ChoedooDatabase
import com.example.data.model.CartItem
import com.example.data.model.MenuItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItem
import com.example.data.model.UserEntity
import com.example.util.FormatUtils
import com.example.util.JsonUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class CheckoutResult {
    data class Success(val transaction: TransactionEntity, val items: List<TransactionItem>) : CheckoutResult()
    data class OutOfStock(val itemName: String, val availableStock: Int, val requestedQuantity: Int) : CheckoutResult()
    data class Error(val message: String) : CheckoutResult()
}

class ChoedooRepository(private val database: ChoedooDatabase) {
    private val userDao = database.userDao()
    private val menuDao = database.menuItemDao()
    private val transactionDao = database.transactionDao()

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        ChoedooDatabase.populateInitialData(database)
    }

    suspend fun authenticate(username: String, password: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.authenticate(username.trim(), password.trim())
    }

    suspend fun getUserByUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByUsername(username.trim())
    }

    fun getAllRiders(): Flow<List<UserEntity>> = userDao.getAllRidersFlow()

    suspend fun registerRider(username: String, name: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim().lowercase()
        val cleanName = name.trim()
        val cleanPassword = password.trim()

        if (cleanUsername.isEmpty() || cleanName.isEmpty() || cleanPassword.isEmpty()) {
            return@withContext Result.failure(Exception("Semua kolom harus diisi"))
        }

        val existing = userDao.getUserByUsername(cleanUsername)
        if (existing != null) {
            return@withContext Result.failure(Exception("Username '$cleanUsername' sudah digunakan"))
        }

        val newRider = UserEntity(
            username = cleanUsername,
            password = cleanPassword,
            role = "RIDER",
            name = cleanName
        )
        userDao.insertUser(newRider)
        Result.success(Unit)
    }

    fun getAllMenuItems(): Flow<List<MenuItemEntity>> = menuDao.getAllMenuItemsFlow()

    suspend fun addStock(menuId: Int, additionalStock: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (additionalStock <= 0) {
            return@withContext Result.failure(Exception("Jumlah penambahan stok harus lebih dari 0"))
        }
        val item = menuDao.getMenuItemById(menuId)
            ?: return@withContext Result.failure(Exception("Menu tidak ditemukan"))

        val newStock = item.stock + additionalStock
        menuDao.updateStock(menuId, newStock)
        Result.success(Unit)
    }

    suspend fun setStock(menuId: Int, newStock: Int): Result<Unit> = withContext(Dispatchers.IO) {
        if (newStock < 0) {
            return@withContext Result.failure(Exception("Stok tidak boleh negatif"))
        }
        menuDao.updateStock(menuId, newStock)
        Result.success(Unit)
    }

    fun getAllTransactions(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    fun getTransactionsByRider(riderUsername: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByRiderFlow(riderUsername)

    fun getTransactionsByDate(date: String): Flow<List<TransactionEntity>> =
        transactionDao.getTransactionsByDateFlow(date)

    suspend fun processCheckout(
        rider: UserEntity,
        cartItems: List<CartItem>
    ): CheckoutResult = withContext(Dispatchers.IO) {
        if (cartItems.isEmpty()) {
            return@withContext CheckoutResult.Error("Keranjang belanja kosong")
        }

        try {
            database.withTransaction {
                // Step 1: Validate stock for all items
                for (item in cartItems) {
                    val currentMenu = menuDao.getMenuItemById(item.menuItem.id)
                        ?: return@withTransaction CheckoutResult.Error("Menu ${item.menuItem.name} tidak ditemukan")

                    if (currentMenu.stock < item.quantity) {
                        return@withTransaction CheckoutResult.OutOfStock(
                            itemName = currentMenu.name,
                            availableStock = currentMenu.stock,
                            requestedQuantity = item.quantity
                        )
                    }
                }

                // Step 2: Decrement stock for each item
                for (item in cartItems) {
                    val rowsAffected = menuDao.decrementStock(item.menuItem.id, item.quantity)
                    if (rowsAffected == 0) {
                        val current = menuDao.getMenuItemById(item.menuItem.id)
                        return@withTransaction CheckoutResult.OutOfStock(
                            itemName = item.menuItem.name,
                            availableStock = current?.stock ?: 0,
                            requestedQuantity = item.quantity
                        )
                    }
                }

                // Step 3: Create transaction record
                val transactionItems = cartItems.map {
                    TransactionItem(
                        menuId = it.menuItem.id,
                        menuName = it.menuItem.name,
                        quantity = it.quantity,
                        price = it.menuItem.price,
                        subtotal = it.subtotal
                    )
                }

                val totalCups = cartItems.sumOf { it.quantity }
                val totalAmount = cartItems.sumOf { it.subtotal }
                val trxId = FormatUtils.generateTransactionId()
                val date = FormatUtils.getCurrentDateFormatted()
                val time = FormatUtils.getCurrentTimeFormatted()

                val transactionEntity = TransactionEntity(
                    transactionId = trxId,
                    riderUsername = rider.username,
                    riderName = rider.name,
                    date = date,
                    time = time,
                    timestamp = System.currentTimeMillis(),
                    totalCups = totalCups,
                    totalAmount = totalAmount,
                    itemsJson = JsonUtils.itemsToJson(transactionItems)
                )

                transactionDao.insertTransaction(transactionEntity)
                CheckoutResult.Success(transactionEntity, transactionItems)
            }
        } catch (e: Exception) {
            CheckoutResult.Error(e.message ?: "Terjadi kesalahan saat memproses transaksi")
        }
    }
}
