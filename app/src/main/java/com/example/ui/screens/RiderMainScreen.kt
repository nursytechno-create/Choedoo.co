package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CartItem
import com.example.data.model.MenuItemEntity
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
import com.example.ui.theme.ChoedooRedPrimary
import com.example.ui.theme.ChoedooWhite
import com.example.ui.viewmodel.RiderTab
import com.example.util.FormatUtils
import com.example.util.JsonUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderMainScreen(
    currentUser: UserEntity,
    riderTab: RiderTab,
    onTabSelect: (RiderTab) -> Unit,
    menuItems: List<MenuItemEntity>,
    cart: Map<Int, CartItem>,
    allTransactions: List<TransactionEntity>,
    showCartSheet: Boolean,
    onToggleCartSheet: (Boolean) -> Unit,
    onAddToCart: (MenuItemEntity) -> Unit,
    onDecreaseCart: (MenuItemEntity) -> Unit,
    onClearCart: () -> Unit,
    isCheckingOut: Boolean,
    onCheckout: () -> Unit,
    completedTransaction: Pair<TransactionEntity, List<TransactionItem>>?,
    onDismissReceipt: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedTrxForReceipt by remember { mutableStateOf<Pair<TransactionEntity, List<TransactionItem>>?>(null) }

    // Rider-specific transactions & today metrics
    val riderTransactions = remember(allTransactions, currentUser.username) {
        allTransactions.filter { it.riderUsername == currentUser.username }
    }

    val todayDate = remember { FormatUtils.getCurrentDateFormatted() }
    val todayRiderTransactions = remember(riderTransactions, todayDate) {
        riderTransactions.filter { it.date == todayDate }
    }

    val todayOmzet = remember(todayRiderTransactions) {
        todayRiderTransactions.sumOf { it.totalAmount }
    }
    val todayCups = remember(todayRiderTransactions) {
        todayRiderTransactions.sumOf { it.totalCups }
    }

    val totalCartCups = cart.values.sumOf { it.quantity }
    val totalCartAmount = cart.values.sumOf { it.subtotal }

    Scaffold(
        topBar = {
            AppHeader(
                currentUser = currentUser,
                onLogoutClick = { showLogoutDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ChoedooWhite,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("rider_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = riderTab == RiderTab.KASIR,
                    onClick = { onTabSelect(RiderTab.KASIR) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PointOfSale,
                            contentDescription = "Kasir"
                        )
                    },
                    label = { Text("KASIR", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("nav_kasir")
                )

                NavigationBarItem(
                    selected = riderTab == RiderTab.RIWAYAT,
                    onClick = { onTabSelect(RiderTab.RIWAYAT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = "Riwayat"
                        )
                    },
                    label = { Text("RIWAYAT", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("nav_riwayat")
                )

                NavigationBarItem(
                    selected = riderTab == RiderTab.DASHBOARD,
                    onClick = { onTabSelect(RiderTab.DASHBOARD) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("DASHBOARD", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { showLogoutDialog = true },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ChoedooGrayMedium
                        )
                    },
                    label = { Text("LOGOUT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ChoedooGrayMedium) },
                    modifier = Modifier.testTag("nav_logout")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ChoedooGrayBackground)
        ) {
            when (riderTab) {
                RiderTab.KASIR -> {
                    RiderKasirView(
                        menuItems = menuItems,
                        cart = cart,
                        todayOmzet = todayOmzet,
                        todayCups = todayCups,
                        onAddToCart = onAddToCart,
                        onDecreaseCart = onDecreaseCart,
                        onOpenCart = { onToggleCartSheet(true) }
                    )
                }
                RiderTab.RIWAYAT -> {
                    RiderRiwayatView(
                        transactions = riderTransactions,
                        onTransactionClick = { trx ->
                            val items = JsonUtils.jsonToItems(trx.itemsJson)
                            selectedTrxForReceipt = Pair(trx, items)
                        }
                    )
                }
                RiderTab.DASHBOARD -> {
                    RiderDashboardView(
                        riderName = currentUser.name,
                        transactions = riderTransactions,
                        todayDate = todayDate
                    )
                }
            }

            // Floating Sticky Cart Bar (visible in Kasir tab when cart is not empty)
            if (riderTab == RiderTab.KASIR && totalCartCups > 0) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = ChoedooBlack,
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .testTag("floating_cart_bar")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "KERANJANG ($totalCartCups ITEM)",
                                color = ChoedooGrayMedium,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = FormatUtils.formatRupiah(totalCartAmount),
                                color = ChoedooWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onToggleCartSheet(true) },
                            colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("open_cart_button")
                        ) {
                            Text(
                                text = "Bayar",
                                color = ChoedooWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                tint = ChoedooWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Cart Bottom Sheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { onToggleCartSheet(false) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ChoedooWhite,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            CartSheetContent(
                cart = cart,
                onAddToCart = onAddToCart,
                onDecreaseCart = onDecreaseCart,
                onClearCart = onClearCart,
                isCheckingOut = isCheckingOut,
                onCheckout = onCheckout
            )
        }
    }

    // Active Completed Receipt Dialog (after checkout)
    if (completedTransaction != null) {
        ReceiptDialog(
            transaction = completedTransaction.first,
            items = completedTransaction.second,
            onDismiss = onDismissReceipt,
            onNewTransactionClick = {
                onDismissReceipt()
                onTabSelect(RiderTab.KASIR)
            }
        )
    }

    // Historical Receipt Dialog (when tapping history card)
    if (selectedTrxForReceipt != null) {
        ReceiptDialog(
            transaction = selectedTrxForReceipt!!.first,
            items = selectedTrxForReceipt!!.second,
            onDismiss = { selectedTrxForReceipt = null }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
fun RiderKasirView(
    menuItems: List<MenuItemEntity>,
    cart: Map<Int, CartItem>,
    todayOmzet: Long,
    todayCups: Int,
    onAddToCart: (MenuItemEntity) -> Unit,
    onDecreaseCart: (MenuItemEntity) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Semua") }

    val filteredItems = remember(menuItems, selectedCategory) {
        when (selectedCategory) {
            "Coffee" -> menuItems.filter { it.category == "Coffee" }
            "Tea" -> menuItems.filter { it.category == "Tea" }
            else -> menuItems
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Today's summary mini banner
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NaturalMetricPill(
                    title = "Omzet Hari Ini",
                    value = FormatUtils.formatRupiah(todayOmzet),
                    valueColor = ChoedooRedPrimary,
                    modifier = Modifier.defaultMinSize(minWidth = 140.dp)
                )
                NaturalMetricPill(
                    title = "Cup Terjual",
                    value = "$todayCups Cup",
                    valueColor = ChoedooBlack,
                    modifier = Modifier.defaultMinSize(minWidth = 120.dp)
                )
            }
        }

        // Category filter chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua", "Coffee", "Tea").forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChoedooRedPrimary,
                            selectedLabelColor = ChoedooWhite,
                            containerColor = ChoedooPillBackground,
                            labelColor = ChoedooBlack
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedCategory == category) ChoedooRedPrimary else ChoedooGrayLight
                        )
                    )
                }
            }
        }

        // Menu Item Cards
        items(filteredItems, key = { it.id }) { item ->
            val cartQty = cart[item.id]?.quantity ?: 0
            val isOutOfStock = item.stock <= 0

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOutOfStock) Color(0xFFFAFAFA) else ChoedooWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isOutOfStock) 0.dp else 1.dp),
                border = BorderStroke(
                    1.dp,
                    if (cartQty > 0) ChoedooRedPrimary else ChoedooCardBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Item details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOutOfStock) ChoedooGrayMedium else ChoedooBlack
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = FormatUtils.formatRupiah(item.price),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isOutOfStock) ChoedooGrayMedium else ChoedooRedPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Stock indicator badge
                        StockBadge(stock = item.stock)
                    }

                    // Action buttons
                    if (isOutOfStock) {
                        Surface(
                            shape = CircleShape,
                            color = ChoedooGrayLight,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Habis",
                                    tint = ChoedooGrayMedium,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        if (cartQty == 0) {
                            Surface(
                                shape = CircleShape,
                                color = ChoedooRedPrimary,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { onAddToCart(item) }
                                    .testTag("add_item_${item.id}")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = ChoedooWhite,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(ChoedooPillBackground, RoundedCornerShape(16.dp))
                                    .padding(2.dp)
                            ) {
                                IconButton(
                                    onClick = { onDecreaseCart(item) },
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("decrease_item_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Kurang",
                                        tint = ChoedooRedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "$cartQty",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ChoedooBlack,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { onAddToCart(item) },
                                    enabled = cartQty < item.stock,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .testTag("increase_item_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah",
                                        tint = if (cartQty < item.stock) ChoedooRedPrimary else ChoedooGrayMedium,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartSheetContent(
    cart: Map<Int, CartItem>,
    onAddToCart: (MenuItemEntity) -> Unit,
    onDecreaseCart: (MenuItemEntity) -> Unit,
    onClearCart: () -> Unit,
    isCheckingOut: Boolean,
    onCheckout: () -> Unit
) {
    val items = cart.values.toList()
    val totalCups = items.sumOf { it.quantity }
    val totalAmount = items.sumOf { it.subtotal }

    var cashPaidInput by remember { mutableStateOf("") }
    val cashPaid = cashPaidInput.toLongOrNull() ?: 0L
    val changeAmount = (cashPaid - totalAmount).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Keranjang Transaksi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooBlack
                )
                Text(
                    text = "$totalCups Cup dipilih",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChoedooGrayMedium
                )
            }

            if (items.isNotEmpty()) {
                TextButton(onClick = onClearCart) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Semua",
                        tint = ChoedooRedPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kosongkan", color = ChoedooRedPrimary, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = ChoedooCardBorder)
        Spacer(modifier = Modifier.height(10.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Keranjang masih kosong",
                    color = ChoedooGrayMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Cart Items List
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach { cartItem ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = ChoedooPillBackground,
                        border = BorderStroke(1.dp, ChoedooCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cartItem.menuItem.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ChoedooBlack
                                )
                                Text(
                                    text = FormatUtils.formatRupiah(cartItem.menuItem.price),
                                    fontSize = 11.sp,
                                    color = ChoedooGrayMedium
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDecreaseCart(cartItem.menuItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Minus",
                                        tint = ChoedooRedPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "${cartItem.quantity}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ChoedooBlack,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { onAddToCart(cartItem.menuItem) },
                                    enabled = cartItem.quantity < cartItem.menuItem.stock,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Plus",
                                        tint = if (cartItem.quantity < cartItem.menuItem.stock) ChoedooRedPrimary else ChoedooGrayMedium,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = FormatUtils.formatRupiah(cartItem.subtotal),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ChoedooBlack,
                                    modifier = Modifier.width(76.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = ChoedooCardBorder)
                Spacer(modifier = Modifier.height(6.dp))

                // Total Summary
                ReceiptRow(label = "Total Jumlah Cup", value = "$totalCups Cup", isBold = true)
                ReceiptRow(
                    label = "TOTAL TAGIHAN",
                    value = FormatUtils.formatRupiah(totalAmount),
                    isBold = true,
                    valueColor = ChoedooRedPrimary,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Cash Calculation Helper
                OutlinedTextField(
                    value = cashPaidInput,
                    onValueChange = { cashPaidInput = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Uang Tunai Diterima (Opsional)") },
                    placeholder = { Text("Contoh: $totalAmount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Cash Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { cashPaidInput = "$totalAmount" },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Uang Pas", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { cashPaidInput = "50000" },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("50.000", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { cashPaidInput = "100000" },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("100.000", fontSize = 11.sp)
                    }
                }

                if (cashPaid > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    ReceiptRow(
                        label = "Kembalian",
                        value = FormatUtils.formatRupiah(changeAmount),
                        isBold = true,
                        valueColor = if (cashPaid >= totalAmount) ChoedooGreenSuccess else ChoedooRedPrimary,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Checkout Button
                Button(
                    onClick = onCheckout,
                    enabled = !isCheckingOut && items.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("complete_transaction_button")
                ) {
                    if (isCheckingOut) {
                        CircularProgressIndicator(
                            color = ChoedooWhite,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Selesaikan Transaksi (${FormatUtils.formatRupiah(totalAmount)})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChoedooWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun RiderRiwayatView(
    transactions: List<TransactionEntity>,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyToday by remember { mutableStateOf(false) }
    val todayDate = remember { FormatUtils.getCurrentDateFormatted() }

    val displayedTrx = remember(transactions, showOnlyToday, todayDate) {
        if (showOnlyToday) transactions.filter { it.date == todayDate } else transactions
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Filter tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Riwayat Transaksi Anda",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooBlack
                )
                Text(
                    text = "Total ${transactions.size} transaksi tersimpan",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChoedooGrayMedium
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = !showOnlyToday,
                    onClick = { showOnlyToday = false },
                    label = { Text("Semua", fontSize = 11.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChoedooRedPrimary,
                        selectedLabelColor = ChoedooWhite,
                        containerColor = ChoedooPillBackground,
                        labelColor = ChoedooBlack
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (!showOnlyToday) ChoedooRedPrimary else ChoedooGrayLight
                    )
                )
                FilterChip(
                    selected = showOnlyToday,
                    onClick = { showOnlyToday = true },
                    label = { Text("Hari Ini", fontSize = 11.sp) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ChoedooRedPrimary,
                        selectedLabelColor = ChoedooWhite,
                        containerColor = ChoedooPillBackground,
                        labelColor = ChoedooBlack
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (showOnlyToday) ChoedooRedPrimary else ChoedooGrayLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (displayedTrx.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Empty",
                        tint = ChoedooGrayMedium,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Belum ada transaksi ${if (showOnlyToday) "hari ini" else ""}",
                        color = ChoedooGrayDark,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Lakukan penjualan di menu Kasir",
                        color = ChoedooGrayMedium,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedTrx, key = { it.transactionId }) { trx ->
                    val items = remember(trx.itemsJson) { JsonUtils.jsonToItems(trx.itemsJson) }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, ChoedooCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(trx) }
                            .testTag("trx_card_${trx.transactionId}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = trx.transactionId,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = ChoedooBlack
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = ChoedooGreenLight
                                ) {
                                    Text(
                                        text = "${trx.totalCups} Cup",
                                        color = ChoedooGreenSuccess,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${trx.date} • ${trx.time}",
                                fontSize = 11.sp,
                                color = ChoedooGrayMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = ChoedooCardBorder)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Item summary text
                            val itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.menuName}" }
                            Text(
                                text = itemsSummary,
                                fontSize = 12.sp,
                                color = ChoedooGrayDark,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ChoedooGrayDark
                                )
                                Text(
                                    text = FormatUtils.formatRupiah(trx.totalAmount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ChoedooRedPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RiderDashboardView(
    riderName: String,
    transactions: List<TransactionEntity>,
    todayDate: String,
    modifier: Modifier = Modifier
) {
    val todayTrx = remember(transactions, todayDate) { transactions.filter { it.date == todayDate } }
    val todayOmzet = remember(todayTrx) { todayTrx.sumOf { it.totalAmount } }
    val todayCups = remember(todayTrx) { todayTrx.sumOf { it.totalCups } }
    val todayTrxCount = todayTrx.size

    val allTimeOmzet = remember(transactions) { transactions.sumOf { it.totalAmount } }
    val allTimeCups = remember(transactions) { transactions.sumOf { it.totalCups } }
    val allTimeTrxCount = transactions.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ChoedooRedPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DASHBOARD RIDER",
                    fontSize = 10.sp,
                    color = ChoedooWhite.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = riderName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooWhite
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Tanggal: $todayDate",
                    fontSize = 11.sp,
                    color = ChoedooWhite.copy(alpha = 0.9f)
                )
            }
        }

        Text(
            text = "Kinerja Hari Ini",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ChoedooBlack
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Omzet Hari Ini",
                value = FormatUtils.formatRupiah(todayOmzet),
                subtitle = "Total pendapatan",
                accentColor = ChoedooRedPrimary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Cup Terjual",
                value = "$todayCups Cup",
                subtitle = "$todayTrxCount transaksi",
                accentColor = ChoedooBlack,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Total Kumulatif Anda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ChoedooBlack
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Semua Omzet",
                value = FormatUtils.formatRupiah(allTimeOmzet),
                subtitle = "Seluruh waktu",
                accentColor = ChoedooBlack,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Semua Cup",
                value = "$allTimeCups Cup",
                subtitle = "$allTimeTrxCount transaksi",
                accentColor = ChoedooRedPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
