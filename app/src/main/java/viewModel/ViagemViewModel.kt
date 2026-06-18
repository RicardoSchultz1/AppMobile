package viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.Foto
import model.Viagem
import repository.ViagemRepository
import java.util.Locale

data class ViagemFormState(
    val id: Int = 0,
    val destino: String = "",
    val tipo: String = "Lazer",
    val dataInicio: Long? = null,
    val dataFim: Long? = null,
    val orcamento: String = "",
    val mensagemErro: String? = null,
    val sucesso: Boolean = false,
    val listaViagens: List<Viagem> = emptyList(),
    val viagemAtual: Viagem? = null,
    val cidadeAtual: String? = null,
    val isLoadingLocation: Boolean = false,
    val localizacaoAtual: LatLng? = null,
    val fotosViagem: List<Foto> = emptyList()
)

class ViagemViewModel(private val repository: ViagemRepository) : ViewModel() {
    var uiState by mutableStateOf(ViagemFormState())
        private set

    fun resetSucesso() {
        uiState = uiState.copy(sucesso = false)
    }

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

    fun carregarViagens(userId: Int) {
        viewModelScope.launch {
            val viagens = repository.listarViagensPorUsuario(userId)
            uiState = uiState.copy(listaViagens = viagens)
        }
    }

    fun excluirViagem(viagem: Viagem, userId: Int) {
        viewModelScope.launch {
            repository.excluirViagem(viagem)
            carregarViagens(userId)
        }
    }

    fun prepararEdicao(viagem: Viagem) {
        uiState = uiState.copy(
            id = viagem.id,
            destino = viagem.destino,
            tipo = viagem.tipo,
            dataInicio = viagem.dataInicio,
            dataFim = viagem.dataFim,
            orcamento = viagem.orcamento.toString(),
            mensagemErro = null,
            sucesso = false
        )
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
                id = uiState.id,
                destino = destino,
                tipo = tipo,
                dataInicio = dataInicio,
                dataFim = dataFim,
                orcamento = orcamento,
                userId = userId
            )
            if (viagem.id == 0) {
                repository.salvarViagem(viagem)
            } else {
                repository.atualizarViagem(viagem)
            }
            uiState = uiState.copy(sucesso = true, id = 0, destino = "", orcamento = "", dataInicio = null, dataFim = null)
            carregarViagens(userId)
        }
    }

    @SuppressLint("MissingPermission")
    fun buscarLocalizacaoECidade(context: Context, userId: Int) {
        uiState = uiState.copy(isLoadingLocation = true)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val cidade = getCityName(context, location.latitude, location.longitude)
                    uiState = uiState.copy(cidadeAtual = cidade, localizacaoAtual = latLng)
                    if (cidade != null) {
                        val viagem = repository.buscarViagemAtual(userId, cidade, System.currentTimeMillis())
                        uiState = uiState.copy(viagemAtual = viagem)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                uiState = uiState.copy(isLoadingLocation = false)
            }
        }
    }

    private suspend fun getCityName(context: Context, lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
        } catch (e: Exception) {
            null
        }
    }

    fun carregarFotos(viagemId: Int) {
        viewModelScope.launch {
            val fotos = repository.listarFotosPorViagem(viagemId)
            uiState = uiState.copy(fotosViagem = fotos)
        }
    }

    fun adicionarFoto(viagemId: Int, uri: Uri) {
        viewModelScope.launch {
            val novaFoto = Foto(viagemId = viagemId, path = uri.toString())
            repository.salvarFoto(novaFoto)
            carregarFotos(viagemId)
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
