package viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import model.Viagem
import repository.ViagemRepository

data class ViagemFormState(
    val destino: String = "",
    val tipo: String = "Lazer",
    val dataInicio: Long? = null,
    val dataFim: Long? = null,
    val orcamento: String = "",
    val mensagemErro: String? = null,
    val sucesso: Boolean = false
)

class ViagemViewModel(private val repository: ViagemRepository) : ViewModel() {
    var uiState by mutableStateOf(ViagemFormState())
        private set

    fun updateDestino(novoDestino: String) {
        uiState = uiState.copy(destino = novoDestino)
    }

    fun updateTipo(novoTipo: String) {
        uiState = uiState.copy(tipo = novoTipo)
    }

    fun updateDataInicio(novaData: Long?) {
        uiState = uiState.copy(dataInicio = novaData)
    }

    fun updateDataFim(novaData: Long?) {
        uiState = uiState.copy(dataFim = novaData)
    }

    fun updateOrcamento(novoOrcamento: String) {
        uiState = uiState.copy(orcamento = novoOrcamento)
    }

    fun salvarViagem(userId: Int) {
        val destino = uiState.destino
        val tipo = uiState.tipo
        val dataInicio = uiState.dataInicio
        val dataFim = uiState.dataFim
        val orcamentoStr = uiState.orcamento
        val orcamento = orcamentoStr.toDoubleOrNull()

        if (destino.isBlank() || dataInicio == null || dataFim == null || orcamento == null) {
            uiState = uiState.copy(mensagemErro = "Todos os campos são obrigatórios e orçamento deve ser numérico")
            return
        }

        viewModelScope.launch {
            val viagem = Viagem(
                destino = destino,
                tipo = tipo,
                dataInicio = dataInicio,
                dataFim = dataFim,
                orcamento = orcamento,
                userId = userId
            )
            repository.salvarViagem(viagem)
            uiState = uiState.copy(sucesso = true)
        }
    }
}

class ViagemViewModelFactory(private val repository: ViagemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViagemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViagemViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
