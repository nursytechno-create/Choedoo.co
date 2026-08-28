package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Motorcycle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ChoedooBlack
import com.example.ui.theme.ChoedooCardBorder
import com.example.ui.theme.ChoedooCharcoal
import com.example.ui.theme.ChoedooGold
import com.example.ui.theme.ChoedooGrayBackground
import com.example.ui.theme.ChoedooGrayDark
import com.example.ui.theme.ChoedooGrayLight
import com.example.ui.theme.ChoedooGrayMedium
import com.example.ui.theme.ChoedooPillBackground
import com.example.ui.theme.ChoedooRedContainer
import com.example.ui.theme.ChoedooRedPrimary
import com.example.ui.theme.ChoedooWhite
import com.example.ui.viewmodel.AuthUiState

@Composable
fun LoginScreen(
    authUiState: AuthUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onQuickLogin: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ChoedooGrayBackground)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo and Branding Header
            Surface(
                shape = CircleShape,
                color = ChoedooWhite,
                shadowElevation = 4.dp,
                border = BorderStroke(1.5.dp, ChoedooCardBorder),
                modifier = Modifier.size(110.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.choedoo_logo),
                    contentDescription = "CHOEDOO.CO Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CHOEDOO.CO",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = ChoedooRedPrimary,
                letterSpacing = 2.sp
            )

            Text(
                text = "COFFEE ON STYLE",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = ChoedooCharcoal,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sistem Kasir & Monitoring Rider",
                style = MaterialTheme.typography.bodyMedium,
                color = ChoedooGrayMedium
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Login Card Form
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, ChoedooCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Silakan Masuk",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChoedooBlack
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username Field
                    OutlinedTextField(
                        value = authUiState.usernameInput,
                        onValueChange = onUsernameChange,
                        label = { Text("Username") },
                        placeholder = { Text("admin / rider1") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Username",
                                tint = ChoedooRedPrimary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChoedooRedPrimary,
                            unfocusedBorderColor = ChoedooCardBorder,
                            focusedLabelColor = ChoedooRedPrimary,
                            cursorColor = ChoedooRedPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = authUiState.passwordInput,
                        onValueChange = onPasswordChange,
                        label = { Text("Password") },
                        placeholder = { Text("Masukkan password") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = ChoedooRedPrimary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = ChoedooGrayMedium
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                onLoginClick()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChoedooRedPrimary,
                            unfocusedBorderColor = ChoedooCardBorder,
                            focusedLabelColor = ChoedooRedPrimary,
                            cursorColor = ChoedooRedPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    // Error Message
                    if (authUiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ChoedooRedContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = authUiState.errorMessage,
                                color = ChoedooRedPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onLoginClick()
                        },
                        enabled = !authUiState.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ChoedooRedPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        if (authUiState.isLoading) {
                            CircularProgressIndicator(
                                color = ChoedooWhite,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Masuk ke Sistem",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChoedooWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Access / Demo Accounts
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ChoedooWhite),
                border = BorderStroke(1.dp, ChoedooCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Akses Cepat (Demo / Uji Coba):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChoedooGrayDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Admin Quick Button
                    OutlinedButton(
                        onClick = { onQuickLogin("admin", "admin") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ChoedooBlack),
                        border = BorderStroke(1.dp, ChoedooGrayLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_login_admin")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = ChoedooRedPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login sebagai ADMIN (admin / admin)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rider 1 Quick Button
                    OutlinedButton(
                        onClick = { onQuickLogin("rider1", "123") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ChoedooBlack),
                        border = BorderStroke(1.dp, ChoedooGrayLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("quick_login_rider1")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Motorcycle,
                                contentDescription = "Rider 1",
                                tint = ChoedooBlack,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Login sebagai RIDER 1 (rider1 / 123)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Rider 2 & 3 Quick Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onQuickLogin("rider2", "123") },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ChoedooGrayLight),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_login_rider2")
                        ) {
                            Text("Rider 2", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ChoedooBlack)
                        }
                        OutlinedButton(
                            onClick = { onQuickLogin("rider3", "123") },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ChoedooGrayLight),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_login_rider3")
                        ) {
                            Text("Rider 3", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ChoedooBlack)
                        }
                    }
                }
            }
        }
    }
}
