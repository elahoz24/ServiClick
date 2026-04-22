package com.serviclick.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.R
import com.serviclick.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val resetMessage by viewModel.resetMessage.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onNavigateToHome()
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SunsetOrange,
        unfocusedBorderColor = ForestGreen.copy(alpha = 0.5f),
        focusedTextColor = ForestGreen,
        unfocusedTextColor = ForestGreen,
        focusedLabelColor = SunsetOrange,
        unfocusedLabelColor = ForestGreen.copy(alpha = 0.7f),
        cursorColor = SunsetOrange
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "ServiClick Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        errorMessage?.let {
            Text(text = it, color = Color(0xFFFF5252), modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)
        }

        resetMessage?.let {
            Text(text = it, color = SunsetOrange, modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)
        }

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onLoginChanged(it, password) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onLoginChanged(email, it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isLoading
        )

        TextButton(
            onClick = {
                viewModel.clearMessages()
                showResetDialog = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("¿Has olvidado tu contraseña?", color = ForestGreen.copy(alpha = 0.7f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onLoginSelected() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isLoginEnabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = SunsetOrange,
                disabledContainerColor = SunsetOrange.copy(alpha = 0.3f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = CreamBackground, modifier = Modifier.size(24.dp))
            } else {
                Text("ENTRAR", fontWeight = FontWeight.ExtraBold, color = CreamBackground)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { onNavigateToRegister() }, enabled = !isLoading) {
            Text("¿Nuevo aquí? Crea tu cuenta", color = SunsetOrange)
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = BeigeSurface,
            titleContentColor = ForestGreen,
            textContentColor = ForestGreen,
            title = { Text(text = "Recuperar contraseña", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Introduce tu correo electrónico y te enviaremos las instrucciones para cambiar tu contraseña.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Correo electrónico") },
                        colors = textFieldColors,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onResetPassword(resetEmail)
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SunsetOrange)
                ) {
                    Text("Enviar", color = CreamBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = ForestGreen)
                }
            }
        )
    }
}