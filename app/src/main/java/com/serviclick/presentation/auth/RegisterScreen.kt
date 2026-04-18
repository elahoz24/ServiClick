package com.serviclick.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
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
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val role by viewModel.role.collectAsState()
    val isRegisterEnabled by viewModel.isRegisterEnabled.collectAsState()

    // Usamos EXACTAMENTE los mismos colores de TextField que en el Login
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
        // Logo con el mismo tamaño que el Login (150.dp)
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "ServiClick Logo",
            modifier = Modifier.size(150.dp)
        )

        // Mismo espaciado tras el logo (48.dp)
        Spacer(modifier = Modifier.height(48.dp))

        // Campos de texto con espaciado consistente (16.dp)
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onRegisterChanged(it, password, confirmPassword, role) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onRegisterChanged(email, it, confirmPassword, role) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.onRegisterChanged(email, password, it, role) },
            label = { Text("Repetir Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Rol integrado en el diseño
        Text(
            text = "¿Qué perfil buscas?",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = role == "cliente",
                onClick = { viewModel.onRoleChanged("cliente") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MintVibrant,
                    unselectedColor = Color.White.copy(alpha = 0.6f)
                )
            )
            Text("Cliente", color = Color.White)

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(
                selected = role == "empresa",
                onClick = { viewModel.onRoleChanged("empresa") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MintVibrant,
                    unselectedColor = Color.White.copy(alpha = 0.6f)
                )
            )
            Text("Empresa", color = Color.White)
        }

        // Mismo espaciado antes del botón (40.dp aprox)
        Spacer(modifier = Modifier.height(40.dp))

        // Botón con el mismo estilo "ExtraBold" y altura (54.dp) que el Login
        Button(
            onClick = { viewModel.onRegisterSelected() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isRegisterEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MintVibrant,
                disabledContainerColor = MintVibrant.copy(alpha = 0.3f)
            )
        ) {
            Text("REGISTRARME", fontWeight = FontWeight.ExtraBold, color = MidnightBlue)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { onNavigateBack() }) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = MintVibrant)
        }
    }
}