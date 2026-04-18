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

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MintVibrant,
        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedLabelColor = MintVibrant,
        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(MidnightBlue).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(value = email, onValueChange = { viewModel.onRegisterChanged(it, password, confirmPassword, role) }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), colors = textFieldColors)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { viewModel.onRegisterChanged(email, it, confirmPassword, role) }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = confirmPassword, onValueChange = { viewModel.onRegisterChanged(email, password, it, role) }, label = { Text("Repetir contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), colors = textFieldColors)

        Spacer(modifier = Modifier.height(24.dp))

        Text("¿Qué perfil buscas?", color = Color.White, fontWeight = FontWeight.SemiBold)
        Row(Modifier.selectableGroup(), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = role == "cliente", onClick = { viewModel.onRoleChanged("cliente") }, colors = RadioButtonDefaults.colors(selectedColor = MintVibrant, unselectedColor = Color.White))
            Text("Cliente", color = Color.White)
            Spacer(Modifier.width(20.dp))
            RadioButton(selected = role == "empresa", onClick = { viewModel.onRoleChanged("empresa") }, colors = RadioButtonDefaults.colors(selectedColor = MintVibrant, unselectedColor = Color.White))
            Text("Empresa", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.onRegisterSelected() },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = isRegisterEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = MintVibrant)
        ) {
            Text("REGISTRARME", fontWeight = FontWeight.Bold, color = MidnightBlue)
        }

        TextButton(onClick = { onNavigateBack() }) {
            Text("Ya tengo cuenta", color = MintVibrant)
        }
    }
}