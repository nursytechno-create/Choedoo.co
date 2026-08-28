package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MenuItemEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun authenticate(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'RIDER' ORDER BY name ASC")
    fun getAllRidersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'RIDER' ORDER BY name ASC")
    suspend fun getAllRiders(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface MenuItemDao {
    @Query("SELECT * FROM menu_items ORDER BY id ASC")
    fun getAllMenuItemsFlow(): Flow<List<MenuItemEntity>>

    @Query("SELECT * FROM menu_items ORDER BY id ASC")
    suspend fun getAllMenuItems(): List<MenuItemEntity>

    @Query("SELECT * FROM menu_items WHERE id = :id LIMIT 1")
    suspend fun getMenuItemById(id: Int): MenuItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItems(items: List<MenuItemEntity>)

    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)

    @Query("UPDATE menu_items SET stock = :newStock WHERE id = :id")
    suspend fun updateStock(id: Int, newStock: Int)

    @Query("UPDATE menu_items SET stock = stock + :additionalStock WHERE id = :id")
    suspend fun addStock(id: Int, additionalStock: Int)

    @Query("UPDATE menu_items SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity")
    suspend fun decrementStock(id: Int, quantity: Int): Int

    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getMenuItemCount(): Int
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE riderUsername = :riderUsername ORDER BY timestamp DESC")
    fun getTransactionsByRiderFlow(riderUsername: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date = :date ORDER BY timestamp DESC")
    fun getTransactionsByDateFlow(date: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE riderUsername = :riderUsername AND date = :date ORDER BY timestamp DESC")
    fun getTransactionsByRiderAndDateFlow(riderUsername: String, date: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE riderUsername = :riderUsername ORDER BY timestamp DESC")
    suspend fun getTransactionsByRider(riderUsername: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>
}
