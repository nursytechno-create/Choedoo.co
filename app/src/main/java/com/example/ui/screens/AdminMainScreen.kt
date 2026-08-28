package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.AddRiderUiState
import com.example.ui.viewmodel.AdminTab
import com.example.util.FormatUtils
import com.example.util.JsonUtils

@Composable
fun AdminMainScreen(
    currentUser: UserEntity,
    adminTab: AdminTab,
    onTabSelect: (AdminTab) -> Unit,
    menuItems: List<MenuItemEntity>,
    allTransactions: List<TransactionEntity>,
    allRiders: List<UserEntity>,
    selectedRiderForDetail: UserEntity?,
    onSelectRiderForDetail: (UserEntity?) -> Unit,
    addRiderUiState: AddRiderUiState,
    onAddRiderUsernameChange: (String) -> Unit,
    onAddRiderNameChange: (String) -> Unit,
    onAddRiderPasswordChange: (String) -> Unit,
    onRegisterRider: () -> Unit,
    onResetAddRiderForm: () -> Unit,
    onAddStock: (Int, Int) -> Unit,
    onSetStock: (Int, Int) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAddRiderDialog by remember { mutableStateOf(false) }
    var selectedTrxForReceipt by remember { mutableStateOf<Pair<TransactionEntity, List<TransactionItem>>?>(null) }

    val todayDate = remember { FormatUtils.getCurrentDateFormatted() }

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
                    .testTag("admin_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = adminTab == AdminTab.DASHBOARD && selectedRiderForDetail == null,
                    onClick = {
                        onSelectRiderForDetail(null)
                        onTabSelect(AdminTab.DASHBOARD)
                    },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("admin_nav_dashboard")
                )

                NavigationBarItem(
                    selected = adminTab == AdminTab.RIDER || selectedRiderForDetail != null,
                    onClick = {
                        onSelectRiderForDetail(null)
                        onTabSelect(AdminTab.RIDER)
                    },
                    icon = { Icon(Icons.Default.People, contentDescription = "Rider") },
                    label = { Text("RIDER", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("admin_nav_rider")
                )

                NavigationBarItem(
                    selected = adminTab == AdminTab.STOK && selectedRiderForDetail == null,
                    onClick = {
                        onSelectRiderForDetail(null)
                        onTabSelect(AdminTab.STOK)
                    },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Stok") },
                    label = { Text("STOK", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("admin_nav_stok")
                )

                NavigationBarItem(
                    selected = adminTab == AdminTab.TRANSAKSI && selectedRiderForDetail == null,
                    onClick = {
                        onSelectRiderForDetail(null)
                        onTabSelect(AdminTab.TRANSAKSI)
                    },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = "Transaksi") },
                    label = { Text("TRANSAKSI", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ChoedooRedPrimary,
                        selectedTextColor = ChoedooRedPrimary,
                        unselectedIconColor = ChoedooGrayMedium,
                        unselectedTextColor = ChoedooGrayMedium,
                        indicatorColor = ChoedooPillBackground
                    ),
                    modifier = Modifier.testTag("admin_nav_transaksi")
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { showLogoutDialog = true },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = ChoedooGrayMedium
                        )
                    },
                    label = { Text("LOGOUT", color = ChoedooGrayMedium, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    modifier = Modifier.testTag("admin_nav_logout")
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
            if (selectedRiderForDetail != null) {
                // Drill-down view for a specific rider
                AdminRiderDetailView(
                    rider = selectedRiderForDetail,
                    allTransactions = allTransactions,
                    todayDate = todayDate,
                    onBackClick = { onSelectRiderForDetail(null) },
                    onTransactionClick = { trx ->
                        val items = JsonUtils.jsonToItems(trx.itemsJson)
                        selectedTrxForReceipt = Pair(trx, items)
                    }
                )
            } else {
                when (adminTab) {
                    AdminTab.DASHBOARD -> {
                        AdminDashboardView(
                            allTransactions = allTransactions,
                            allRiders = allRiders,
                            menuItems = menuItems,
                            todayDate = todayDate,
                            onOpenRider = { rider -> onSelectRiderForDetail(rider) },
                            onGoToStok = { onTabSelect(AdminTab.STOK) }
                        )
                    }
                    AdminTab.RIDER -> {
                        AdminRiderListView(
                            allRiders = allRiders,
                            allTransactions = allTransactions,
                            todayDate = todayDate,
                            onAddRiderClick = {
                                onResetAddRiderForm()
                                showAddRiderDialog = true
                            },
                            onSelectRider = { rider -> onSelectRiderForDetail(rider) }
                        )
                    }
                    AdminTab.STOK -> {
                        AdminStokView(
                            menuItems = menuItems,
                            onAddStock = onAddStock,
                            onSetStock = onSetStock
                        )
                    }
                    AdminTab.TRANSAKSI -> {
                        AdminTransaksiView(
                            allTransactions = allTransactions,
                            onTransactionClick = { trx ->
                                val items = JsonUtils.jsonToItems(trx.itemsJson)
                                selectedTrxForReceipt = Pair(trx, items)
                            }
                        )
                    }
                }
            }
        }
    }

    // Add Rider Dialog
    if (showAddRiderDialog) {
        AddRiderDialog(
            uiState = addRiderUiState,
            onUsernameChange = onAddRiderUsernameChange,
            onNameChange = onAddRiderNameChange,
            onPasswordChange = onAddRiderPasswordChange,
            onSubmit = onRegisterRider,
            onDismiss = {
                showAddRiderDialog = false
                onResetAddRiderForm()
            }
        )
    }

    // Receipt Dialog for Transaction details
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
fun AdminDashboardView(
    allTransactions: List<TransactionEntity>,
    allRiders: List<UserEntity>,
    menuItems: List<MenuItemEntity>,
    todayDate: String,
    onOpenRider: (UserEntity) -> Unit,
    onGoToStok: () -> Unit,
    modifier: Modifier = Modifier
) {
    val todayTransactions = remember(allTransactions, todayDate) {
        allTransactions.filter { it.date == todayDate }
    }

    val todayOmzet = remember(todayTransactions) { todayTransactions.sumOf { it.totalAmount } }
    val todayCups = remember(todayTransactions) { todayTransactions.sumOf { it.totalCups } }
    val todayCount = todayTransactions.size

    val allTimeOmzet = remember(allTransactions) { allTransactions.sumOf { it.totalAmount } }
    val allTimeCups = remember(allTransactions) { allTransactions.sumOf { it.totalCups } }

    val lowStockItems = remember(menuItems) { menuItems.filter { it.stock < 10 } }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Today's Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ChoedooRedPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL OMZET HARI INI",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ChoedooWhite.copy(alpha = 0.85f),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = todayDate,
                            fontSize = 11.sp,
                            color = ChoedooWhite.copy(alpha = 0.85f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = FormatUtils.formatRupiah(todayOmzet),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChoedooWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ChoedooWhite.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Transaksi Hari Ini", fontSize = 11.sp, color = ChoedooWhite.copy(alpha = 0.8f))
                            Text("$todayCount Transaksi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ChoedooWhite)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Cup Terjual Hari Ini", fontSize = 11.sp, color = ChoedooWhite.copy(alpha = 0.8f))
                            Text("$todayCups Cup", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ChoedooWhite)
                        }
                    }
                }
            }
        }

        // All Time Summary Stats
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Omzet Keseluruhan",
                    value = FormatUtils.formatRupiah(allTimeOmzet),
                    subtitle = "${allTransactions.size} Transaksi Total",
                    icon = Icons.Default.TrendingUp,
                    accentColor = ChoedooBlack,
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Total Cup Terjual",
                    value = "$allTimeCups Cup",
                    subtitle = "${allRiders.size} Rider Aktif",
                    icon = Icons.Default.Coffee,
                    accentColor = ChoedooRedPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Low stock warning banner if any
        if (lowStockItems.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                    border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onGoToStok() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = "Alert",
                                tint = Color(0xFFC2410C),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Perhatian: ${lowStockItems.size} Menu Menipis/Habis!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF9A3412)
                                )
                                Text(
                                    text = lowStockItems.joinToString(", ") { "${it.name} (${it.stock})" },
                                    fontSize = 11.sp,
                                    color = Color(0xFFC2410C),
                                    maxLines = 1
                                )
                            }
                        }

                        TextButton(onClick = onGoToStok) {
                            Text("Isi Stok", color = ChoedooRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Rider performance leaderboard
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Performa Rider Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooBlack
                )
                Text(
                    text = "${allRiders.size} Rider",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChoedooGrayMedium
                )
            }
        }

        if (allRiders.isEmpty()) {
            item {
                Text("Belum ada data rider", color = ChoedooGrayMedium)
            }
        } else {
            items(allRiders, key = { it.username }) { rider ->
                val riderTodayTrx = remember(todayTransactions, rider.username) {
                    todayTransactions.filter { it.riderUsername == rider.username }
                }
                val riderTodayOmzet = remember(riderTodayTrx) { riderTodayTrx.sumOf { it.totalAmount } }
                val riderTodayCups = remember(riderTodayTrx) { riderTodayTrx.sumOf { it.totalCups } }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, ChoedooCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenRider(rider) }
                        .testTag("admin_dashboard_rider_${rider.username}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = ChoedooPillBackground,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = rider.name,
                                        tint = ChoedooRedPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = rider.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ChoedooBlack
                                )
                                Text(
                                    text = "@${rider.username}",
                                    fontSize = 11.sp,
                                    color = ChoedooGrayMedium
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = FormatUtils.formatRupiah(riderTodayOmzet),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = ChoedooRedPrimary
                                )
                                Text(
                                    text = "$riderTodayCups Cup (${riderTodayTrx.size} Trx)",
                                    fontSize = 11.sp,
                                    color = ChoedooGrayDark
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Detail",
                                tint = ChoedooGrayMedium,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminRiderListView(
    allRiders: List<UserEntity>,
    allTransactions: List<TransactionEntity>,
    todayDate: String,
    onAddRiderClick: () -> Unit,
    onSelectRider: (UserEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRiderClick,
                containerColor = ChoedooRedPrimary,
                contentColor = ChoedooWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("add_rider_fab")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Tambah Rider")
            }
        },
        containerColor = ChoedooGrayBackground,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Daftar Rider CHOEDOO",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChoedooBlack
                    )
                    Text(
                        text = "Kelola dan pantau kinerja seluruh rider",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChoedooGrayMedium
                    )
                }

                Button(
                    onClick = onAddRiderClick,
                    colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_rider_top_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (allRiders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Empty",
                            tint = ChoedooGrayMedium,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada Rider terdaftar",
                            fontWeight = FontWeight.SemiBold,
                            color = ChoedooGrayDark
                        )
                        Text(
                            text = "Tekan tombol Tambah Rider untuk membuat akun",
                            fontSize = 12.sp,
                            color = ChoedooGrayMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allRiders, key = { it.username }) { rider ->
                        val riderTrx = remember(allTransactions, rider.username) {
                            allTransactions.filter { it.riderUsername == rider.username }
                        }
                        val riderTodayTrx = remember(riderTrx, todayDate) {
                            riderTrx.filter { it.date == todayDate }
                        }
                        val riderTodayOmzet = remember(riderTodayTrx) { riderTodayTrx.sumOf { it.totalAmount } }
                        val riderTodayCups = remember(riderTodayTrx) { riderTodayTrx.sumOf { it.totalCups } }
                        val riderAllTimeOmzet = remember(riderTrx) { riderTrx.sumOf { it.totalAmount } }
                        val riderAllTimeCups = remember(riderTrx) { riderTrx.sumOf { it.totalCups } }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            border = BorderStroke(1.dp, ChoedooCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectRider(rider) }
                                .testTag("rider_item_${rider.username}")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = ChoedooBlack,
                                            modifier = Modifier.size(42.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = rider.name,
                                                    tint = ChoedooWhite,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = rider.name,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 15.sp,
                                                color = ChoedooBlack
                                            )
                                            Text(
                                                text = "Username: ${rider.username}",
                                                fontSize = 12.sp,
                                                color = ChoedooGrayMedium
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = ChoedooPillBackground,
                                        border = BorderStroke(1.dp, ChoedooCardBorder)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Detail",
                                                color = ChoedooRedPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = "Go",
                                                tint = ChoedooRedPrimary,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = ChoedooCardBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Omzet Hari Ini", fontSize = 11.sp, color = ChoedooGrayMedium)
                                        Text(
                                            text = FormatUtils.formatRupiah(riderTodayOmzet),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ChoedooRedPrimary
                                        )
                                        Text("$riderTodayCups Cup", fontSize = 11.sp, color = ChoedooGrayDark)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Total Omzet Keseluruhan", fontSize = 11.sp, color = ChoedooGrayMedium)
                                        Text(
                                            text = FormatUtils.formatRupiah(riderAllTimeOmzet),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = ChoedooBlack
                                        )
                                        Text("$riderAllTimeCups Cup (${riderTrx.size} Trx)", fontSize = 11.sp, color = ChoedooGrayDark)
                                    }
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
fun AdminRiderDetailView(
    rider: UserEntity,
    allTransactions: List<TransactionEntity>,
    todayDate: String,
    onBackClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val riderTransactions = remember(allTransactions, rider.username) {
        allTransactions.filter { it.riderUsername == rider.username }
    }

    val todayRiderTrx = remember(riderTransactions, todayDate) {
        riderTransactions.filter { it.date == todayDate }
    }

    val todayOmzet = remember(todayRiderTrx) { todayRiderTrx.sumOf { it.totalAmount } }
    val todayCups = remember(todayRiderTrx) { todayRiderTrx.sumOf { it.totalCups } }

    val allTimeOmzet = remember(riderTransactions) { riderTransactions.sumOf { it.totalAmount } }
    val allTimeCups = remember(riderTransactions) { riderTransactions.sumOf { it.totalCups } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back Navigation Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("back_to_riders_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = ChoedooBlack
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Column {
                Text(
                    text = rider.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ChoedooBlack
                )
                Text(
                    text = "Detail Kinerja & Riwayat Transaksi Rider",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChoedooGrayMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard(
                title = "Omzet Hari Ini",
                value = FormatUtils.formatRupiah(todayOmzet),
                subtitle = "$todayCups Cup Terjual",
                icon = Icons.Default.Payments,
                accentColor = ChoedooRedPrimary,
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Total Keseluruhan",
                value = FormatUtils.formatRupiah(allTimeOmzet),
                subtitle = "$allTimeCups Cup (${riderTransactions.size} Trx)",
                icon = Icons.Default.TrendingUp,
                accentColor = ChoedooBlack,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Seluruh Riwayat Transaksi (${riderTransactions.size})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = ChoedooBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (riderTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Rider ini belum memiliki riwayat transaksi",
                    color = ChoedooGrayMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(riderTransactions, key = { it.transactionId }) { trx ->
                    val items = remember(trx.itemsJson) { JsonUtils.jsonToItems(trx.itemsJson) }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, ChoedooCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(trx) }
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
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${trx.date} • ${trx.time}",
                                fontSize = 11.sp,
                                color = ChoedooGrayMedium
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = items.joinToString(", ") { "${it.quantity}x ${it.menuName}" },
                                fontSize = 12.sp,
                                color = ChoedooGrayDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = ChoedooCardBorder)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Tagihan", fontSize = 11.sp, color = ChoedooGrayDark)
                                Text(
                                    text = FormatUtils.formatRupiah(trx.totalAmount),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
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
fun AdminStokView(
    menuItems: List<MenuItemEntity>,
    onAddStock: (Int, Int) -> Unit,
    onSetStock: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingItem by remember { mutableStateOf<MenuItemEntity?>(null) }
    var customStockInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Manajemen Stok Menu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooBlack
                )
                Text(
                    text = "Stok cup siap jual diperbarui otomatis saat transaksi",
                    style = MaterialTheme.typography.bodySmall,
                    color = ChoedooGrayMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(menuItems, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, ChoedooCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = ChoedooBlack
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${FormatUtils.formatRupiah(item.price)} • Kategori ${item.category}",
                                    fontSize = 12.sp,
                                    color = ChoedooGrayDark
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                StockBadge(stock = item.stock)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${item.stock} Cup",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (item.stock == 0) ChoedooRedPrimary else ChoedooBlack
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = ChoedooCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Add Stock Buttons
                        Text(
                            text = "Tambah Stok Cepat:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ChoedooGrayMedium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5, 10, 20, 50).forEach { amount ->
                                OutlinedButton(
                                    onClick = { onAddStock(item.id, amount) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ChoedooBlack),
                                    border = BorderStroke(1.dp, ChoedooGrayLight),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("quick_add_stock_${item.id}_$amount")
                                ) {
                                    Text("+$amount", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    editingItem = item
                                    customStockInput = "${item.stock}"
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ChoedooRedPrimary),
                                border = BorderStroke(1.dp, ChoedooRedPrimary),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("edit_stock_${item.id}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Ubah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Stock Edit Dialog
    if (editingItem != null) {
        val targetItem = editingItem!!
        AlertDialog(
            onDismissRequest = { editingItem = null },
            shape = RoundedCornerShape(20.dp),
            containerColor = ChoedooWhite,
            title = {
                Text(
                    text = "Ubah Stok: ${targetItem.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text("Masukkan jumlah stok baru (cup):", fontSize = 13.sp, color = ChoedooGrayDark)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customStockInput,
                        onValueChange = { customStockInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Jumlah Stok") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newStock = customStockInput.toIntOrNull() ?: 0
                        onSetStock(targetItem.id, newStock)
                        editingItem = null
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary)
                ) {
                    Text("Simpan Stok", color = ChoedooWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text("Batal", color = ChoedooGrayDark)
                }
            }
        )
    }
}

@Composable
fun AdminTransaksiView(
    allTransactions: List<TransactionEntity>,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRiderFilter by remember { mutableStateOf("Semua") }

    val filteredTransactions = remember(allTransactions, searchQuery, selectedRiderFilter) {
        allTransactions.filter { trx ->
            val matchSearch = searchQuery.isBlank() ||
                trx.transactionId.contains(searchQuery, ignoreCase = true) ||
                trx.riderName.contains(searchQuery, ignoreCase = true) ||
                trx.itemsJson.contains(searchQuery, ignoreCase = true)

            val matchRider = selectedRiderFilter == "Semua" || trx.riderName == selectedRiderFilter
            matchSearch && matchRider
        }
    }

    val totalFilteredOmzet = remember(filteredTransactions) {
        filteredTransactions.sumOf { it.totalAmount }
    }
    val totalFilteredCups = remember(filteredTransactions) {
        filteredTransactions.sumOf { it.totalCups }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Semua Transaksi Masuk",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ChoedooBlack
        )
        Text(
            text = "Pantau seluruh transaksi dari seluruh rider secara real-time",
            style = MaterialTheme.typography.bodySmall,
            color = ChoedooGrayMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari ID Transaksi / Rider / Menu...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Cari", tint = ChoedooGrayMedium)
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChoedooRedPrimary,
                unfocusedBorderColor = ChoedooCardBorder,
                focusedContainerColor = ChoedooWhite,
                unfocusedContainerColor = ChoedooWhite,
                cursorColor = ChoedooRedPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Summary Bar
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = ChoedooBlack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTransactions.size} Transaksi ($totalFilteredCups Cup)",
                    fontSize = 12.sp,
                    color = ChoedooWhite
                )
                Text(
                    text = FormatUtils.formatRupiah(totalFilteredOmzet),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ChoedooGold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Empty",
                        tint = ChoedooGrayMedium,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tidak ada transaksi ditemukan",
                        fontWeight = FontWeight.SemiBold,
                        color = ChoedooGrayDark
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTransactions, key = { it.transactionId }) { trx ->
                    val items = remember(trx.itemsJson) { JsonUtils.jsonToItems(trx.itemsJson) }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = BorderStroke(1.dp, ChoedooCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(trx) }
                            .testTag("admin_trx_${trx.transactionId}")
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
                                    color = ChoedooPillBackground,
                                    border = BorderStroke(1.dp, ChoedooCardBorder)
                                ) {
                                    Text(
                                        text = trx.riderName,
                                        color = ChoedooRedPrimary,
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

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = items.joinToString(", ") { "${it.quantity}x ${it.menuName}" },
                                fontSize = 12.sp,
                                color = ChoedooGrayDark
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = ChoedooCardBorder)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${trx.totalCups} Cup Terjual",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ChoedooGrayDark
                                )
                                Text(
                                    text = FormatUtils.formatRupiah(trx.totalAmount),
                                    fontSize = 14.sp,
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
fun AddRiderDialog(
    uiState: AddRiderUiState,
    onUsernameChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = ChoedooWhite,
        title = {
            Text(
                text = "Tambah Rider Baru",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Buat akun login untuk rider kasir CHOEDOO.CO",
                    fontSize = 12.sp,
                    color = ChoedooGrayMedium
                )

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = { Text("Nama Lengkap Rider") },
                    placeholder = { Text("Contoh: Rider 4 - Ilham") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_rider_name_input")
                )

                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username Login") },
                    placeholder = { Text("Contoh: rider4") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_rider_username_input")
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password Login") },
                    placeholder = { Text("Contoh: 123") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_rider_password_input")
                )

                if (uiState.errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ChoedooRedContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = ChoedooRedPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                if (uiState.isSuccess) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = ChoedooGreenLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rider berhasil ditambahkan!",
                            color = ChoedooGreenSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (uiState.isSuccess) {
                        onDismiss()
                    } else {
                        onSubmit()
                    }
                },
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("submit_add_rider_button")
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = ChoedooWhite,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (uiState.isSuccess) "Selesai" else "Simpan Rider",
                        fontWeight = FontWeight.Bold,
                        color = ChoedooWhite
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = ChoedooGrayDark)
            }
        }
    )
}
