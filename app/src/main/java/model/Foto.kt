package model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fotos",
    foreignKeys = [
        ForeignKey(
            entity = Viagem::class,
            parentColumns = ["id"],
            childColumns = ["viagemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["viagemId"])]
)
data class Foto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val viagemId: Int,
    val path: String
)
