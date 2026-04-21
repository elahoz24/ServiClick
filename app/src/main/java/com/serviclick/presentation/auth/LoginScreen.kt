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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serviclick.R
import com.serviclick.ui.theme.MidnightBlue
import com.serviclick.ui.theme.MintVibrant

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
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

    // Variable para controlar si mostramos u ocultamos el Pop-Up
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onNavigateToHome()
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MintVibrant,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = MintVibrant,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
        cursorColor = MintVibrant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightBlue)
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

        // Mensaje de Error (Rojo)
        errorMessage?.let {
            Text(text = it, color = Color(0xFFFF5252), modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)
        }

        // Mensaje de Éxito al resetear (Verde menta)
        resetMessage?.let {
            Text(text = it, color = MintVibrant, modifier = Modifier.padding(bottom = 16.dp), textAlign = TextAlign.Center)
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

        // Botón de recuperar contraseña
        TextButton(
            onClick = {
                viewModel.clearMessages()
                showResetDialog = true
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("¿Has olvidado tu contraseña?", color = Color.White.copy(alpha = 0.7f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.onLoginSelected() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isLoginEnabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MintVibrant,
                disabledContainerColor = MintVibrant.copy(alpha = 0.3f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MidnightBlue, modifier = Modifier.size(24.dp))
            } else {
                Text("ENTRAR", fontWeight = FontWeight.ExtraBold, color = MidnightBlue)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { onNavigateToRegister() }, enabled = !isLoading) {
            Text("¿Nuevo aquí? Crea tu cuenta", color = MintVibrant)
        }
    }

    // --- EL POP-UP DE RECUPERAR CONTRASEÑA ---
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = MidnightBlue, // Color de fondo del pop-up
            titleContentColor = Color.White,
            textContentColor = Color.White,
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
                    colors = ButtonDefaults.buttonColors(containerColor = MintVibrant)
                ) {
                    Text("Enviar", color = MidnightBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}