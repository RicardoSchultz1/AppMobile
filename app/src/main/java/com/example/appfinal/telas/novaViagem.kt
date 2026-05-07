package com.example.appfinal.telas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import viewModel.ViagemViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun novaViagem(
    userId: Int,
    viewModel: ViagemViewModel,
    onVoltar: () -> Unit
) {
    val state = viewModel.uiState
    val scrollState = rememberScrollState()

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") } }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            onVoltar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Viagem") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Destino
            OutlinedTextField(
                value = state.destino,
                onValueChange = { viewModel.updateDestino(it) },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.mensagemErro != null && state.destino.isBlank()
            )

            // Tipo
            Text("Tipo de Viagem", style = MaterialTheme.typography.bodyLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.tipo == "Lazer",
                    onClick = { viewModel.updateTipo("Lazer") }
                )
                Text("Lazer 🏖️", modifier = Modifier.clickable { viewModel.updateTipo("Lazer") })
                
                RadioButton(
                    selected = state.tipo == "Negócios",
                    onClick = { viewModel.updateTipo("Negócios") }
                )
                Text("Negócios 💼", modifier = Modifier.clickable { viewModel.updateTipo("Negócios") })
            }

            // Data Início
            OutlinedTextField(
                value = state.dataInicio?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Data Início") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar data")
                    }
                },
                isError = state.mensagemErro != null && state.dataInicio == null
            )

            // Data Fim
            OutlinedTextField(
                value = state.dataFim?.let { dateFormatter.format(Date(it)) } ?: "",
                onValueChange = {},
                label = { Text("Data Fim") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Selecionar data")
                    }
                },
                isError = state.mensagemErro != null && state.dataFim == null
            )

            // Orçamento
            OutlinedTextField(
                value = state.orcamento,
                onValueChange = { viewModel.updateOrcamento(it) },
                label = { Text("Orçamento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.mensagemErro != null && state.orcamento.toDoubleOrNull() == null
            )

            state.mensagemErro?.let {
                Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick = { viewModel.salvarViagem(userId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Viagem")
            }
        }
    }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDataInicio(datePickerState.selectedDateMillis)
                    showStartDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateDataFim(datePickerState.selectedDateMillis)
                    showEndDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
