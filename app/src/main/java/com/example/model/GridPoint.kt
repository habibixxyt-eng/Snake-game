package com.example.model

data class GridPoint(
    val x: Int,
    val y: Int
) {
    fun offset(dx: Int, dy: Int): GridPoint = GridPoint(x + dx, y + dy)
}

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    fun isOpposite(other: Direction): Boolean {
        return (dx + other.dx == 0) && (dy + other.dy == 0)
    }
}
