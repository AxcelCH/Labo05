package com.jach.labo05.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.jach.labo05.workers.DelayedNotificationWorker
import java.util.UUID
import java.util.concurrent.TimeUnit

// Ejercicio 3: registro local de un WorkRequest programado para poder cancelarlo
data class ScheduledWork(val id: UUID, val message: String)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationsScreen() {
    val context = LocalContext.current
    var mensaje             by remember { mutableStateOf("") }
    var ultimoEnvio         by remember { mutableStateOf<String?>(null) }
    var contadorProgramadas by remember { mutableStateOf(0) }
    // Ejercicio 3: lista de trabajos programados en esta sesión (UUID + mensaje)
    var trabajosProgramados by remember { mutableStateOf<List<ScheduledWork>>(emptyList()) }

    val notifPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    val tienePermiso = notifPermission?.status?.isGranted ?: true

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Notificaciones", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Programa notificaciones locales con WorkManager",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (!tienePermiso) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Se requiere permiso para mostrar notificaciones.", color = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { notifPermission?.launchPermissionRequest() }) { Text("Conceder permiso") }
                }
            }
            return@Column
        }

        OutlinedTextField(
            value         = mensaje,
            onValueChange = { mensaje = it },
            label         = { Text("Mensaje de la notificación") },
            placeholder   = { Text("Ej: Revisar inventario") },
            modifier      = Modifier.fillMaxWidth(),
            minLines      = 2,
            maxLines      = 4
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val request = OneTimeWorkRequestBuilder<DelayedNotificationWorker>()
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .setInputData(workDataOf(DelayedNotificationWorker.INPUT_MESSAGE to mensaje))
                    .build()

                WorkManager.getInstance(context).enqueue(request)
                // Ejercicio 3: guardamos el UUID del request para poder cancelarlo
                trabajosProgramados = trabajosProgramados + ScheduledWork(request.id, mensaje)
                ultimoEnvio = mensaje
                contadorProgramadas++
                mensaje = ""
            },
            enabled  = mensaje.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.NotificationAdd, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Programar notificación (10 s)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Notificaciones programadas en esta sesión: $contadorProgramadas",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (ultimoEnvio != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Último mensaje: \"$ultimoEnvio\"", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        // ── Ejercicio 3: lista de trabajos pendientes con botón Cancelar ──
        if (trabajosProgramados.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Pendientes de esta sesión", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items = trabajosProgramados, key = { it.id }) { trabajo ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier          = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(trabajo.message, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    trabajo.id.toString().take(8),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            TextButton(onClick = {
                                WorkManager.getInstance(context).cancelWorkById(trabajo.id)
                                trabajosProgramados = trabajosProgramados.filterNot { it.id == trabajo.id }
                            }) {
                                Icon(
                                    Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cancelar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
