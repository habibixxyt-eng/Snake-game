package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class RivalSnake(
    val id: String,
    val name: String,
    val body: List<GridPoint>,
    val direction: Direction,
    val color: Color,
    val isAlive: Boolean = true
) {
    fun nextMove(
        boardWidth: Int,
        boardHeight: Int,
        fruits: List<ActiveFruit>,
        playerBody: List<GridPoint>,
        otherRivals: List<RivalSnake>
    ): Direction {
        if (!isAlive || body.isEmpty()) return direction
        val head = body.first()

        // Target nearest fruit if available
        val target = fruits.minByOrNull {
            kotlin.math.abs(it.position.x - head.x) + kotlin.math.abs(it.position.y - head.y)
        }?.position

        val possibleDirs = Direction.values().filter { !it.isOpposite(direction) }

        // Score each direction based on safety and distance to target
        val ratedDirs = possibleDirs.map { dir ->
            val nextPos = head.offset(dir.dx, dir.dy)
            var penalty = 0

            // Wall collision
            if (nextPos.x < 0 || nextPos.x >= boardWidth || nextPos.y < 0 || nextPos.y >= boardHeight) {
                penalty += 1000
            }
            // Player collision
            if (playerBody.contains(nextPos)) {
                penalty += 500
            }
            // Own body collision
            if (body.dropLast(1).contains(nextPos)) {
                penalty += 500
            }
            // Other rival collision
            for (rival in otherRivals) {
                if (rival.id != this.id && rival.isAlive && rival.body.contains(nextPos)) {
                    penalty += 500
                }
            }

            // Desirability towards fruit
            val dist = if (target != null) {
                kotlin.math.abs(target.x - nextPos.x) + kotlin.math.abs(target.y - nextPos.y)
            } else {
                Random.nextInt(10)
            }

            val score = penalty * 10 + dist
            dir to score
        }

        val best = ratedDirs.minByOrNull { it.second }
        return best?.first ?: direction
    }
}
