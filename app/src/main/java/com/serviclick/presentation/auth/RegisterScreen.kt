package com.serviclick.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.serviclick.ui.theme.CreamBackground
import com.serviclick.ui.theme.ForestGreen
import com.serviclick.ui.theme.SunsetOrange

/**
 * Interfaz gráfica (UI) para la pantalla de Registro.
 * Permite a un usuario nuevo crear una cuenta, seleccionando su rol inicial.
 * Muestra un formulario estándar. Usa `selectableGroup()` para asegurar que
 * solo se pueda elegir un rol a la vez ("cliente" o "empresa") mediante los `RadioButton`.
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val role by viewModel.role.collectAsState()
    val isRegisterEnabled by viewModel.isRegisterEnabled.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_con_texto),
            contentDescription = "ServiClick Logo",
            modifier = Modifier.size(150.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        errorMessage?.let {
            Text(
                text = it,
                color = Color(0xFFFF5252),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.onRegisterChanged(it, password, confirmPassword, role) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.onRegisterChanged(email, it, confirmPassword, role) },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.onRegisterChanged(email, password, it, role) },
            label = { Text("Repetir Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Qué perfil buscas?",
            color = ForestGreen,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )

        Row(
            modifier = Modifier.selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = role == "cliente",
                onClick = { if (!isLoading) viewModel.onRoleChanged("cliente") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = SunsetOrange,
                    unselectedColor = ForestGreen.copy(alpha = 0.6f)
                )
            )
            Text("Cliente", color = ForestGreen)

            Spacer(modifier = Modifier.width(20.dp))

            RadioButton(
                selected = role == "empresa",
                onClick = { if (!isLoading) viewModel.onRoleChanged("empresa") },
                colors = RadioButtonDefaults.colors(
                    selectedColor = SunsetOrange,
                    unselectedColor = ForestGreen.copy(alpha = 0.6f)
                )
            )
            Text("Empresa", color = ForestGreen)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = { viewModel.onRegisterSelected() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = isRegisterEnabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = SunsetOrange,
                disabledContainerColor = SunsetOrange.copy(alpha = 0.3f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = CreamBackground, modifier = Modifier.size(24.dp))
            } else {
                Text("REGISTRARME", fontWeight = FontWeight.ExtraBold, color = CreamBackground)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { onNavigateBack() }, enabled = !isLoading) {
            Text("¿Ya tienes cuenta? Inicia sesión", color = SunsetOrange)
        }
    }
}