package com.example.appfinal.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import model.Cadastro
import model.Viagem

@Database(entities = [Cadastro::class, Viagem::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    abstract fun cadastroDao(): CadastroDao
    abstract fun viagemDao(): ViagemDao
    
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

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_db"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
