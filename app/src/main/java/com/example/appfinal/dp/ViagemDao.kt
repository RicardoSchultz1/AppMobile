package com.example.appfinal.dp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import model.Viagem

@Dao
interface ViagemDao {
    @Insert
    suspend fun insert(viagem: Viagem)

    @Update
    suspend fun update(viagem: Viagem)

    @Delete
    suspend fun delete(viagem: Viagem)

    @Query("SELECT * FROM viagens WHERE userId = :userId")
    suspend fun getViagensByUser(userId: Int): List<Viagem>

    @Query("SELECT * FROM viagens WHERE id = :id LIMIT 1")
    suspend fun getViagemById(id: Int): Viagem?

    @Query("""
        SELECT * FROM viagens 
        WHERE userId = :userId 
        AND UPPER(destino) = UPPER(:cidade)
        AND :dataAtual BETWEEN dataInicio AND dataFim
        LIMIT 1
    """)
    suspend fun findViagemByCidadeEData(userId: Int, cidade: String, dataAtual: Long): Viagem?
}
