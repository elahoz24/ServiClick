package com.serviclick.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.serviclick.R
import com.serviclick.ui.theme.MidnightBlue
import com.serviclick.ui.theme.MintVibrant

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsState()

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
        // IMAGEN PNG (Debe estar en res/drawable con el nombre logo.png)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "ServiClick Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onLoginChanged(it, password) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onLoginChanged(email, it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.onLoginSelected() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isLoginEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MintVibrant,
                disabledContainerColor = MintVibrant.copy(alpha = 0.3f)
            )
        ) {
            Text("ENTRAR", fontWeight = FontWeight.ExtraBold, color = MidnightBlue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { onNavigateToRegister() }) {
            Text("¿Nuevo aquí? Crea tu cuenta", color = MintVibrant)
        }
    }
}