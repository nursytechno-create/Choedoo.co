package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionItem
import com.example.data.model.UserEntity
import com.example.ui.theme.ChoedooBlack
import com.example.ui.theme.ChoedooCardBorder
import com.example.ui.theme.ChoedooCharcoal
import com.example.ui.theme.ChoedooGold
import com.example.ui.theme.ChoedooGrayBackground
import com.example.ui.theme.ChoedooGrayDark
import com.example.ui.theme.ChoedooGrayLight
import com.example.ui.theme.ChoedooGrayMedium
import com.example.ui.theme.ChoedooGreenLight
import com.example.ui.theme.ChoedooGreenSuccess
import com.example.ui.theme.ChoedooPillBackground
import com.example.ui.theme.ChoedooRedContainer
import com.example.ui.theme.ChoedooRedDark
import com.example.ui.theme.ChoedooRedPrimary
import com.example.ui.theme.ChoedooWhite
import com.example.util.FormatUtils
import com.example.util.JsonUtils

@Composable
fun AppHeader(
    currentUser: UserEntity?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ChoedooRedPrimary,
        shadowElevation = 3.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = ChoedooWhite,
                    modifier = Modifier.size(36.dp),
                    border = BorderStroke(1.dp, ChoedooWhite.copy(alpha = 0.5f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.choedoo_logo),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    )
                }

                Column {
                    Text(
                        text = "CHOEDOO.CO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChoedooWhite,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "COFFEE ON STYLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = ChoedooWhite.copy(alpha = 0.9f),
                        letterSpacing = 2.sp
                    )
                }
            }

            if (currentUser != null) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = ChoedooWhite.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, ChoedooWhite.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = currentUser.role,
                            color = ChoedooWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = ChoedooBlack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = "Avatar",
                                tint = ChoedooWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("header_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ChoedooWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = ChoedooRedPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ChoedooPillBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, ChoedooGrayLight),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                color = ChoedooGrayMedium,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = ChoedooGrayMedium
                )
            }
        }
    }
}

@Composable
fun NaturalMetricPill(
    title: String,
    value: String,
    valueColor: Color = ChoedooRedPrimary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ChoedooPillBackground,
        border = BorderStroke(1.dp, ChoedooGrayLight),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                color = ChoedooGrayMedium,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

@Composable
fun StockBadge(stock: Int, modifier: Modifier = Modifier) {
    if (stock <= 0) {
        Text(
            text = "HABIS",
            color = ChoedooRedPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier
        )
    } else {
        Text(
            text = "Stok: $stock",
            color = ChoedooGrayMedium,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = modifier
        )
    }
}

@Composable
fun ReceiptDialog(
    transaction: TransactionEntity,
    items: List<TransactionItem>,
    onDismiss: () -> Unit,
    onNewTransactionClick: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(1.dp, ChoedooCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Badge
                Surface(
                    shape = CircleShape,
                    color = ChoedooGreenLight,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = ChoedooGreenSuccess,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "TRANSAKSI BERHASIL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooGreenSuccess
                )

                Text(
                    text = "CHOEDOO.CO — COFFEE ON STYLE",
                    fontSize = 10.sp,
                    color = ChoedooGrayMedium,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ChoedooCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Rows
                ReceiptRow(label = "ID Transaksi", value = transaction.transactionId, isBold = true)
                ReceiptRow(label = "Rider", value = transaction.riderName)
                ReceiptRow(label = "Tanggal", value = "${transaction.date} ${transaction.time}")

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ChoedooCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Items list
                Text(
                    text = "Rincian Menu:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooBlack,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.menuName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ChoedooBlack
                            )
                            Text(
                                text = "${item.quantity} cup x ${FormatUtils.formatRupiah(item.price)}",
                                fontSize = 11.sp,
                                color = ChoedooGrayMedium
                            )
                        }
                        Text(
                            text = FormatUtils.formatRupiah(item.subtotal),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChoedooBlack
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ChoedooCardBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))

                ReceiptRow(
                    label = "Total Cup Terjual",
                    value = "${transaction.totalCups} Cup",
                    isBold = true
                )

                ReceiptRow(
                    label = "TOTAL PEMBAYARAN",
                    value = FormatUtils.formatRupiah(transaction.totalAmount),
                    isBold = true,
                    valueColor = ChoedooRedPrimary,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("receipt_close_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ChoedooGrayLight)
                    ) {
                        Text("Tutup", color = ChoedooBlack)
                    }

                    if (onNewTransactionClick != null) {
                        Button(
                            onClick = {
                                onDismiss()
                                onNewTransactionClick()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("receipt_new_trx_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Transaksi Baru", color = ChoedooWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = ChoedooBlack,
    fontSize: androidx.compose.ui.unit.TextUnit = 12.sp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            color = ChoedooGrayDark,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = fontSize,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Logout", fontWeight = FontWeight.Bold) },
        text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("confirm_logout_button")
            ) {
                Text("Logout", color = ChoedooWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_logout_button")
            ) {
                Text("Batal", color = ChoedooGrayDark)
            }
        }
    )
}

