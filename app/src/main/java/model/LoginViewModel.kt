package model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfinal.dp.CadastroDao
import kotlinx.coroutines.launch

data class LoginFormState(
    val email: String = "",
    val senha: String = "",
    val mensagemErro: String? = null
)

class LoginViewModel(private val cadastroDao: CadastroDao) : ViewModel() {
    var uiState by mutableStateOf(LoginFormState())
        private set

    fun updateEmail(novoEmail: String) {
        uiState = uiState.copy(email = novoEmail, mensagemErro = null)
    }

    fun updateSenha(novaSenha: String) {
        uiState = uiState.copy(senha = novaSenha, mensagemErro = null)
    }

    fun fazerLogin(onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val usuario = cadastroDao.login(uiState.email, uiState.senha)
            if (usuario != null) {
                onSuccess(usuario.id)
            } else {
                uiState = uiState.copy(mensagemErro = "Email ou senha incorretos")
            }
        }
    }
}
