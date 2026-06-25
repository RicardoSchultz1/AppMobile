package viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.RoteiroRepository
import repository.ViagemRepository
import java.util.concurrent.TimeUnit

data class RoteiroUiState(
    val destino: String = "",
    val dias: String = "",
    val interesses: String = "",
    val roteiroGerado: String? = null,
    val isLoading: Boolean = false,
    val erro: String? = null
)

class RoteiroViewModel(
    private val repository: RoteiroRepository,
    private val viagemRepository: ViagemRepository
) : ViewModel() {
    var uiState by mutableStateOf(RoteiroUiState())
        private set

    fun updateDestino(novoDestino: String) {
        uiState = uiState.copy(destino = novoDestino)
    }

    fun updateDias(novosDias: String) {
        uiState = uiState.copy(dias = novosDias)
    }

    fun updateInteresses(novosInteresses: String) {
        uiState = uiState.copy(interesses = novosInteresses)
    }

    fun carregarDadosViagem(viagemId: Int) {
        viewModelScope.launch {
            val viagem = viagemRepository.buscarViagemPorId(viagemId)
            viagem?.let {
                val diff = it.dataFim - it.dataInicio
                val dias = TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
                uiState = uiState.copy(
                    destino = it.destino,
                    dias = (dias + 1).toString()
                )
            }
        }
    }

    fun gerarRoteiro(apiKey: String) {
        if (uiState.destino.isBlank() || uiState.dias.isBlank()) {
            uiState = uiState.copy(erro = "Preencha destino e quantidade de dias")
            return
        }

        val diasInt = uiState.dias.toIntOrNull() ?: 0
        if (diasInt <= 0) {
            uiState = uiState.copy(erro = "Quantidade de dias inválida")
            return
        }

        uiState = uiState.copy(isLoading = true, erro = null, roteiroGerado = null)

        viewModelScope.launch {
            val result = repository.gerarRoteiro(
                apiKey = apiKey,
                destino = uiState.destino,
                dias = diasInt,
                interesses = uiState.interesses
            )

            result.onSuccess { roteiro ->
                uiState = uiState.copy(roteiroGerado = roteiro, isLoading = false)
            }.onFailure { exception ->
                uiState = uiState.copy(erro = exception.message, isLoading = false)
            }
        }
    }
}

class RoteiroViewModelFactory(
    private val repository: RoteiroRepository,
    private val viagemRepository: ViagemRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoteiroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoteiroViewModel(repository, viagemRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
