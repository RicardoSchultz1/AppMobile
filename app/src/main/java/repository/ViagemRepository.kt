package repository

import com.example.appfinal.dp.ViagemDao
import model.Viagem

class ViagemRepository(private val viagemDao: ViagemDao) {
    suspend fun salvarViagem(viagem: Viagem) {
        viagemDao.insert(viagem)
    }

    suspend fun atualizarViagem(viagem: Viagem) {
        viagemDao.update(viagem)
    }

    suspend fun excluirViagem(viagem: Viagem) {
        viagemDao.delete(viagem)
    }

    suspend fun listarViagensPorUsuario(userId: Int): List<Viagem> {
        return viagemDao.getViagensByUser(userId)
    }

    suspend fun buscarViagemAtual(userId: Int, cidade: String, dataAtual: Long): Viagem? {
        return viagemDao.findViagemByCidadeEData(userId, cidade, dataAtual)
    }
}
