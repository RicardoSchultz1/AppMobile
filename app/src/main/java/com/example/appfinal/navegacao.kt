package com.example.appfinal

import kotlinx.serialization.Serializable

@Serializable
sealed interface Destino {
    @Serializable
    data object login : Destino

    @Serializable
    data object esqueciSenha : Destino

    @Serializable
    data class menu(val userId: Int) : Destino

    @Serializable
    data object novoUsuario : Destino

    @Serializable
    data class novaViagem(val userId: Int) : Destino
}
