package com.example.model

enum class GameMode(
    val title: String,
    val subtitle: String,
    val description: String,
    val iconName: String
) {
    CLASSIC(
        title = "Classic Arcade",
        subtitle = "Endless Jungle Feast",
        description = "Slither, eat fruits, grow longer, avoid walls and your own tail. Beat the record!",
        iconName = "apple"
    ),
    ARENA_SURVIVAL(
        title = "Arena Survival",
        subtitle = "Rival Snake Battle",
        description = "Share the arena with rival snakes! Cut them off and feast on their fruit drops.",
        iconName = "swords"
    ),
    TIME_ATTACK(
        title = "Minnal Rush (90s)",
        subtitle = "Lightning Time Challenge",
        description = "90 seconds on the clock! Chain fruit combos and golden fruits for maximum score.",
        iconName = "timer"
    )
}

enum class Difficulty(
    val title: String,
    val tickDelayMs: Long,
    val scoreMultiplier: Float
) {
    CASUAL("Casual Paambu", 140L, 1.0f),
    CHALLENGER("Speed Cobra", 100L, 1.5f),
    INSANE("Raging Naagam", 70L, 2.0f)
}

enum class ControlMode(val label: String) {
    DPAD("Touch D-Pad"),
    GESTURE_SWIPE("Swipe Gestures"),
    JOYSTICK("Floating Thumbstick")
}
