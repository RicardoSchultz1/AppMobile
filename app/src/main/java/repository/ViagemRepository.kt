package repository

import com.example.appfinal.dp.ViagemDao
import model.Viagem

class ViagemRepository(private val viagemDao: ViagemDao) {
    suspend fun salvarViagem(viagem: Viagem) {
        viagemDao.insert(viagem)
    }

    suspend fun listarViagensPorUsuario(userId: Int): List<Viagem> {
        return viagemDao.getViagensByUser(userId)
    }
}
