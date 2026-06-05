package com.serviclick.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serviclick.domain.model.ChatMessage
import com.serviclick.domain.use_case.GetChatMessagesUseCase
import com.serviclick.domain.use_case.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Controlador de la pantalla del chat.
 * Actua como puente entre la base de datos en tiempo real de Firebase y las burbujas de la UI.
 * En el inicio, se suscribe al Flow de mensajes mediante `listenForMessages`. Cada vez que Firestore
 * notifica un cambio, el array de mensajes se actualiza automáticamente.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    // Lista de mensajes en tiempo real
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // El texto que el usuario escribe en la caja de texto
    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private var currentAppointmentId: String = ""
    private var currentSenderId: String = ""

    // 1. Iniciamos la conexión cuando se abre el chat
    fun startChat(appointmentId: String, senderId: String) {
        currentAppointmentId = appointmentId
        currentSenderId = senderId
        listenForMessages()
    }

    // 2. Escuchamos Firebase
    private fun listenForMessages() {
        if (currentAppointmentId.isEmpty()) return

        viewModelScope.launch {
            getChatMessagesUseCase(currentAppointmentId)
                .catch { e -> e.printStackTrace() } // Si hay fallo de red, no crashea
                .collect { newMessages ->
                    _messages.value = newMessages
                }
        }
    }

    // 3. Actualizamos el texto mientras escribe
    fun onMessageTextChanged(newText: String) {
        _messageText.value = newText
    }

    // 4. Enviamos el mensaje
    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty() || currentAppointmentId.isEmpty() || currentSenderId.isEmpty()) return

        val newMessage = ChatMessage(
            senderId = currentSenderId,
            text = text
        )

        // Limpiamos el input al instante para dar sensación de inmediatez (Optimistic UI)
        _messageText.value = ""

        viewModelScope.launch {
            sendMessageUseCase(currentAppointmentId, newMessage).onFailure {
                // Si falla (ej. pérdida de cobertura), devolvemos el texto a la caja para no perderlo
                _messageText.value = text
            }
        }
    }
}