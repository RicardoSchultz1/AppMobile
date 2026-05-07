package model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appfinal.dp.CadastroDao

class LoginViewModelFactory(private val cadastroDao: CadastroDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(cadastroDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
