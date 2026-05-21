package com.example.appfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.appfinal.dp.AppDataBase
import com.example.appfinal.telas.ModLogin
import com.example.appfinal.telas.esqueciSenha
import com.example.appfinal.telas.menu
import com.example.appfinal.telas.minhasViagens
import com.example.appfinal.telas.novaViagem
import com.example.appfinal.telas.novoUsuario
import com.example.appfinal.ui.theme.AppFInalTheme
import model.LoginViewModel
import model.LoginViewModelFactory
import repository.ViagemRepository
import viewModel.ViagemViewModel
import viewModel.ViagemViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppFInalTheme {
                val context = LocalContext.current
                val db = remember { AppDataBase.getDatabase(context) }
                val cadastroDao = remember { db.cadastroDao() }
                val viagemDao = remember { db.viagemDao() }
                
                val viagemRepository = remember { ViagemRepository(viagemDao) }
                
                val backStack = remember { mutableStateListOf<Destino>(Destino.login) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeLast() },
                    entryProvider = { key ->
                        when (key) {
                            Destino.login -> NavEntry(key) {
                                val loginViewModel: LoginViewModel = viewModel(
                                    factory = LoginViewModelFactory(cadastroDao)
                                )
                                ModLogin(
                                    onEsqueciSenhaClick = { backStack.add(Destino.esqueciSenha) },
                                    onLoginClick = { userId -> backStack.add(Destino.menu(userId)) },
                                    onNovoUsuario = { backStack.add(Destino.novoUsuario) },
                                    viewModel = loginViewModel
                                )
                            }
                            Destino.esqueciSenha -> NavEntry(key) {
                                esqueciSenha(
                                    onVoltar = { backStack.removeLast() }
                                )
                            }
                            is Destino.menu -> NavEntry(key) {
                                val viagemViewModel: ViagemViewModel = viewModel(
                                    factory = ViagemViewModelFactory(viagemRepository)
                                )
                                menu(
                                    userId = key.userId,
                                    viewModel = viagemViewModel,
                                    onNovaViagem = { backStack.add(Destino.novaViagem(key.userId)) },
                                    onMinhasViagens = { backStack.add(Destino.minhasViagens(key.userId)) },
                                    onVoltar = { backStack.removeLast() }
                                )
                            }
                           Destino.novoUsuario -> NavEntry(key) {
                                novoUsuario(
                                    onVoltar = { backStack.removeLast() }
                                )
                            }
                            is Destino.novaViagem -> NavEntry(key) {
                                val viagemViewModel: ViagemViewModel = viewModel(
                                    factory = ViagemViewModelFactory(viagemRepository)
                                )
                                novaViagem(
                                    userId = key.userId,
                                    viewModel = viagemViewModel,
                                    onVoltar = { backStack.removeLast() }
                                )
                            }
                            is Destino.minhasViagens -> NavEntry(key) {
                                val viagemViewModel: ViagemViewModel = viewModel(
                                    factory = ViagemViewModelFactory(viagemRepository)
                                )
                                minhasViagens(
                                    userId = key.userId,
                                    viewModel = viagemViewModel,
                                    onEditarViagem = { backStack.add(Destino.novaViagem(key.userId)) },
                                    onVoltar = { backStack.removeLast() }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
