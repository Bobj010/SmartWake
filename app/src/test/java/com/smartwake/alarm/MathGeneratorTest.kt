package com.smartwake.alarm

import com.smartwake.alarm.domain.model.MathDifficulty
import com.smartwake.alarm.domain.usecase.MathGenerator
import com.smartwake.alarm.domain.usecase.MathProblem
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MathGeneratorTest {

    @Test
    fun generateProblem_createsValidExpressions() {
        for (difficulty in MathDifficulty.values()) {
            for (i in 1..20) {
                val problem = MathGenerator.generateProblem(difficulty)
                assertTrue(problem.expression.isNotBlank())
            }
        }
    }

    @Test
    fun checkAnswer_validatesCorrectAndIncorrectAnswers() {
        val problem = MathProblem("25 + 14", 39)

        assertTrue(MathGenerator.checkAnswer(problem, "39"))
        assertTrue(MathGenerator.checkAnswer(problem, " 39 "))
        assertFalse(MathGenerator.checkAnswer(problem, "40"))
        assertFalse(MathGenerator.checkAnswer(problem, "abc"))
        assertFalse(MathGenerator.checkAnswer(problem, ""))
    }
}
