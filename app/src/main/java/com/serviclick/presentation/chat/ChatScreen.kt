package com.serviclick.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serviclick.domain.model.ChatMessage

/**
 * Pantalla de mensajería (Chat) utilizando un diseño moderno de burbujas.
 * Muestra la conversación bidireccional entre cliente y profesional.
 * Utiliza el componente `Scaffold` para montar una estructura de AppBar arriba y
 * campo de texto (`ChatInputBar`) fijado abajo. Dependiendo del booleano `isClosed`, bloquea
 * la escritura si la cita ya finalizó.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    appointmentId: String,
    senderId: String,
    isClosed: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val messageText by viewModel.messageText.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startChat(appointmentId, senderId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat del Trabajo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (isClosed) {
                Surface(color = Color(0xFFF5F5F5), modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "El trabajo finalizó. Chat de solo lectura.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                ChatInputBar(
                    text = messageText,
                    onTextChange = viewModel::onMessageTextChanged,
                    onSend = viewModel::sendMessage
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC))
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { message ->
                MessageBubble(message = message, isMyMessage = message.senderId == senderId)
            }
        }
    }
}

/**
 * Componente que renderiza una burbuja individual de mensaje.
 * Cambia su alineación, color de fondo y forma de los bordes según quién lo envíe.
 */
@Composable
fun MessageBubble(message: ChatMessage, isMyMessage: Boolean) {
    val alignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isMyMessage) Color(0xFF2E7D32) else Color.LightGray.copy(alpha = 0.5f)
    val textColor = if (isMyMessage) Color.White else Color.Black

    val shape = if (isMyMessage) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

/**
 * Barra inferior del chat para escribir y enviar nuevos mensajes.
 */
@Composable
fun ChatInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF7043),
                    unfocusedBorderColor = Color.Gray
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .background(Color(0xFFFF7043), RoundedCornerShape(50))
                    .size(48.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = Color.White
                )
            }
        }
    }
}