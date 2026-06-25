package repository

import android.util.Log
import model.Content
import model.GeminiRequest
import model.Part
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.json.JSONObject

class RoteiroRepository {
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Ajustamos a baseUrl para incluir v1beta, que é a versão correta para o Gemini 1.5 Flash
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(GeminiService::class.java)

    suspend fun gerarRoteiro(apiKey: String, destino: String, dias: Int, interesses: String): Result<String> {
        val prompt = "Gere um roteiro turístico para $destino de $dias dias, com foco em: $interesses. Retorne apenas o roteiro formatado em texto simples de forma organizada."
        val request = GeminiRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = prompt)))
            )
        )

        return try {
            val response = service.generateContent(apiKey.trim(), request)
            val texto = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (texto != null) {
                Result.success(texto)
            } else {
                Result.failure(Exception("A IA não retornou conteúdo válido."))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Log.e("GeminiAPI", "Erro da API: $errorBody")
            
            val detailedMessage = try {
                val json = JSONObject(errorBody ?: "")
                json.getJSONObject("error").getString("message")
            } catch (ex: Exception) {
                errorBody ?: "Erro desconhecido"
            }
            
            Result.failure(Exception(detailedMessage))
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Erro de conexão", e)
            Result.failure(Exception("Erro de rede: ${e.localizedMessage}"))
        }
    }
}
