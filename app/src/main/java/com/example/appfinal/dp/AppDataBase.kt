package com.example.appfinal.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import model.Cadastro
import model.Viagem
import model.Foto

@Database(entities = [Cadastro::class, Viagem::class, Foto::class], version = 3, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    abstract fun cadastroDao(): CadastroDao
    abstract fun viagemDao(): ViagemDao
    abstract fun fotoDao(): FotoDao
    
    companion object{
        @Volatile
        private var INSTANCE: AppDataBase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `viagens` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `destino` TEXT NOT NULL, 
                        `tipo` TEXT NOT NULL, 
                        `dataInicio` INTEGER NOT NULL, 
                        `dataFim` INTEGER NOT NULL, 
                        `orcamento` REAL NOT NULL, 
                        `userId` INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `fotos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `viagemId` INTEGER NOT NULL, 
                        `path` TEXT NOT NULL, 
                        FOREIGN KEY(`viagemId`) REFERENCES `viagens`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_fotos_viagemId` ON `fotos` (`viagemId`)")
            }
        }

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
