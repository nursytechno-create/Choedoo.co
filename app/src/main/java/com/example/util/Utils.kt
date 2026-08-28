package com.example.util

import com.example.data.model.TransactionItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object JsonUtils {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, TransactionItem::class.java)
    private val adapter = moshi.adapter<List<TransactionItem>>(listType)

    fun itemsToJson(items: List<TransactionItem>): String {
        return try {
            adapter.toJson(items)
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToItems(json: String): List<TransactionItem> {
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

object FormatUtils {
    private val indonesianLocale = Locale("id", "ID")
    private val currencyFormat = NumberFormat.getCurrencyInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }

    fun formatRupiah(amount: Long): String {
        return try {
            currencyFormat.format(amount).replace(",00", "")
        } catch (e: Exception) {
            "Rp " + NumberFormat.getNumberInstance(indonesianLocale).format(amount)
        }
    }

    fun getCurrentDateFormatted(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", indonesianLocale)
        return sdf.format(Date())
    }

    fun getCurrentTimeFormatted(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", indonesianLocale)
        return sdf.format(Date())
    }

    fun generateTransactionId(): String {
        val sdf = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)
        val rand = (100..999).random()
        return "TRX-${sdf.format(Date())}-$rand"
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", indonesianLocale)
        return sdf.format(Date(timestamp))
    }
}
