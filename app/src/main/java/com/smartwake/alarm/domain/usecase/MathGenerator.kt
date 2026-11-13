package com.smartwake.alarm.domain.usecase

import com.smartwake.alarm.domain.model.MathDifficulty
import kotlin.random.Random

data class MathProblem(
    val expression: String,
    val answer: Int
)

object MathGenerator {

    /**
     * Генерирует математический пример в зависимости от сложности
     */
    fun generateProblem(difficulty: MathDifficulty): MathProblem {
        return when (difficulty) {
            MathDifficulty.EASY -> {
                val a = Random.nextInt(10, 50)
                val b = Random.nextInt(10, 50)
                val isPlus = Random.nextBoolean()
                if (isPlus) {
                    MathProblem("$a + $b", a + b)
                } else {
                    val max = maxOf(a, b)
                    val min = minOf(a, b)
                    MathProblem("$max - $min", max - min)
                }
            }
            MathDifficulty.MEDIUM -> {
                val a = Random.nextInt(12, 35)
                val b = Random.nextInt(3, 9)
                MathProblem("$a × $b", a * b)
            }
            MathDifficulty.HARD -> {
                val a = Random.nextInt(11, 25)
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(10, 40)
                val isPlus = Random.nextBoolean()
                if (isPlus) {
                    MathProblem("($a × $b) + $c", (a * b) + c)
                } else {
                    MathProblem("($a × $b) - $c", (a * b) - c)
                }
            }
        }
    }

    /**
     * Проверяет ответ пользователя
     */
    fun checkAnswer(problem: MathProblem, userAnswer: String): Boolean {
        val parsed = userAnswer.trim().toIntOrNull() ?: return false
        return parsed == problem.answer
    }
}
