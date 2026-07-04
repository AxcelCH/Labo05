package com.jach.labo05.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jach.labo05.data.session.SessionManager
import com.jach.labo05.security.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SessionViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    companion object {
        // Ejercicio 5: validación con PBKDF2 en lugar de comparación directa de strings.
        // Salt fijo para el laboratorio (en producción: salt aleatorio por usuario, guardado en BD)
        private val SALT = "demodata-lab5-salt".toByteArray()
        private const val USUARIO_VALIDO = "jkn"
        // Hash PBKDF2 de la contraseña esperada ("jkn"), calculado una sola vez
        private val HASH_ESPERADO by lazy { PasswordHasher.hash("jkn", SALT) }
    }

    val isLoggedIn = sessionManager.isLoggedIn.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = false
    )

    val username = sessionManager.currentUsername.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = null
    )

    val isDarkMode = sessionManager.isDarkMode.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.Eagerly,
        initialValue = null
    )

    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Normalizamos: sin espacios accidentales ni mayúsculas del autocorrector
            val user = username.trim().lowercase()
            val pass = password.trim()

            // PBKDF2 con 120 000 iteraciones es costoso: se ejecuta fuera del Main Thread
            val esValido = withContext(Dispatchers.Default) {
                try {
                    val hashIngresado = PasswordHasher.hash(pass, SALT)
                    user == USUARIO_VALIDO &&
                            PasswordHasher.constantTimeEquals(hashIngresado, HASH_ESPERADO)
                } catch (e: Exception) {
                    // PBKDF2WithHmacSHA256 requiere API 26+; en dispositivos
                    // antiguos caemos a la comparación directa del lab base
                    user == USUARIO_VALIDO && pass == USUARIO_VALIDO
                }
            }
            if (esValido) {
                sessionManager.login(user)
                onResult(true)
            } else {
                onResult(false)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setDarkMode(enabled) }
    }

    fun logout() {
        viewModelScope.launch { sessionManager.logout() }
    }

    class Factory(private val sessionManager: SessionManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SessionViewModel(sessionManager) as T
    }
}
