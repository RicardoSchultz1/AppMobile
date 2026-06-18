package com.example.appfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.appfinal.dp.AppDataBase
import com.example.appfinal.telas.ModLogin
import com.example.appfinal.telas.esqueciSenha
import com.example.appfinal.telas.Fotos
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
                val fotoDao = remember { db.fotoDao() }
                
                val viagemRepository = remember { ViagemRepository(viagemDao, fotoDao) }
                
                val backStack = remember { mutableStateListOf<Destino>(Destino.login) }

                NavDisplay(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
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
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
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
                                    onFotos = { viagemId -> backStack.add(Destino.fotos(viagemId)) },
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                           Destino.novoUsuario -> NavEntry(key) {
                                novoUsuario(
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                            is Destino.novaViagem -> NavEntry(key) {
                                val viagemViewModel: ViagemViewModel = viewModel(
                                    factory = ViagemViewModelFactory(viagemRepository)
                                )
                                novaViagem(
                                    userId = key.userId,
                                    viewModel = viagemViewModel,
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
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
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                            is Destino.fotos -> NavEntry(key) {
                                val viagemViewModel: ViagemViewModel = viewModel(
                                    factory = ViagemViewModelFactory(viagemRepository)
                                )
                                Fotos(
                                    viagemId = key.viagemId,
                                    viewModel = viagemViewModel,
                                    onVoltar = { backStack.removeAt(backStack.lastIndex) }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
