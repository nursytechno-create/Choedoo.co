package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val role: String, // "ADMIN" or "RIDER"
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val price: Long,
    val stock: Int,
    val category: String = "Coffee"
)

@JsonClass(generateAdapter = true)
data class TransactionItem(
    val menuId: Int,
    val menuName: String,
    val quantity: Int,
    val price: Long,
    val subtotal: Long
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val transactionId: String,
    val riderUsername: String,
    val riderName: String,
    val date: String, // e.g. "2026-08-28"
    val time: String, // e.g. "14:30:00"
    val timestamp: Long = System.currentTimeMillis(),
    val totalCups: Int,
    val totalAmount: Long,
    val itemsJson: String // Serialized List<TransactionItem>
)

data class CartItem(
    val menuItem: MenuItemEntity,
    val quantity: Int
) {
    val subtotal: Long get() = menuItem.price * quantity
}

data class RiderSummary(
    val username: String,
    val name: String,
    val totalOmzet: Long,
    val totalCups: Int,
    val transactionCount: Int,
    val todayOmzet: Long,
    val todayCups: Int,
    val todayTransactions: Int
)

data class DashboardOverview(
    val todayOmzet: Long,
    val todayTransactions: Int,
    val todayCups: Int,
    val allTimeOmzet: Long,
    val allTimeTransactions: Int,
    val allTimeCups: Int
)
