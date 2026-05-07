package com.example.appfinal.dp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import model.Viagem

@Dao
interface ViagemDao {
    @Insert
    suspend fun insert(viagem: Viagem)

    @Query("SELECT * FROM viagens WHERE userId = :userId")
    suspend fun getViagensByUser(userId: Int): List<Viagem>
}
