package com.example.a15game

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName


@IgnoreExtraProperties
data class Player(
    @get:PropertyName("Name") @set:PropertyName("Name") var name: String = "",
    @get:PropertyName("Score") @set:PropertyName("Score") var score: String = "",
    @get:PropertyName("Time") @set:PropertyName("Time") var time: String = "",
    @get:PropertyName("Moves") @set:PropertyName("Moves") var moves: String = "",
    @get:PropertyName("Mode") @set:PropertyName("Mode") var mode: String = "",
    @get:PropertyName("EasyScore") @set:PropertyName("EasyScore") var EasyScore: String = "",
@get:PropertyName("StandartScore") @set:PropertyName("StandartScore") var SrandartScore: String = "",
@get:PropertyName("HardScore") @set:PropertyName("HardScore") var  HardScore: String = "",
@get:PropertyName("BSTUScore") @set:PropertyName("BSTUScore") var   BSTUScore: String = ""
) {
    // Конвертируем объект в HashMap
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "Name" to name,
            "Score" to score,
            "Time" to time,
            "Moves" to moves,
            "Mode" to mode,
            "EasyScore" to EasyScore,
            "StandartScore" to SrandartScore,
            "HardScore" to HardScore,
            "BSTUScore" to BSTUScore
        )
    }
}






