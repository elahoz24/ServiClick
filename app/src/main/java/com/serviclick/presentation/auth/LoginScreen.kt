package com.serviclick.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    // Inyectamos el ViewModel automáticamente
    viewModel: LoginViewModel = viewModel()
) {
    // Observamos los estados de forma moderna (StateFlow)
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoginEnabled by viewModel.isLoginEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "ServiClick", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onLoginChanged(it, password) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onLoginChanged(email, it) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            // Esto oculta los caracteres de la contraseña con puntitos
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onLoginSelected() },
            modifier = Modifier.fillMaxWidth(),
            // El botón reacciona instantáneamente a nuestra validación del ViewModel
            enabled = isLoginEnabled
        ) {
            Text("Iniciar Sesión")
        }
    }
}
