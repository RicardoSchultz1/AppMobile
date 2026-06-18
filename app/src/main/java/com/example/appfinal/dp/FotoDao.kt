package com.example.appfinal.dp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import model.Foto

@Dao
interface FotoDao {
    @Insert
    suspend fun insert(foto: Foto)

    @Query("SELECT * FROM fotos WHERE viagemId = :viagemId")
    suspend fun getFotosByViagem(viagemId: Int): List<Foto>
}
