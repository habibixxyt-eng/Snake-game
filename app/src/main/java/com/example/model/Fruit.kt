package com.example.model

enum class FruitType(
    val title: String,
    val tamilName: String,
    val points: Int,
    val growth: Int,
    val durationTicks: Int = 0,
    val description: String
) {
    APPLE(
        title = "Red Apple",
        tamilName = "Sevvaappil",
        points = 10,
        growth = 1,
        description = "Classic crispy apple. Gives steady growth."
    ),
    WATERMELON(
        title = "Watermelon Slice",
        tamilName = "Tharpuzhani",
        points = 25,
        growth = 2,
        description = "Juicy & massive! Grants double growth points."
    ),
    BANANA(
        title = "Yellow Banana",
        tamilName = "Vaazhaipazham",
        points = 15,
        growth = 1,
        durationTicks = 35,
        description = "Potassium punch! Gives a temporary speed boost."
    ),
    WILD_BERRY(
        title = "Wild Berries",
        tamilName = "Naaval Pazham",
        points = 20,
        growth = 1,
        durationTicks = 50,
        description = "Deep forest berry! 2x Score Multiplier active."
    ),
    GOLDEN_MANGO(
        title = "Golden Mango",
        tamilName = "Thanga Mambazham",
        points = 50,
        growth = 3,
        durationTicks = 60,
        description = "The King of Fruits! Grants Golden Shield (ghost pass-through)."
    ),
    CHILLI_PEPPER(
        title = "Ghost Chilli",
        tamilName = "Kanthari Milagai",
        points = 30,
        growth = 1,
        durationTicks = 40,
        description = "Fiery rage! Super turbo slither with fire trail."
    )
}

data class ActiveFruit(
    val position: GridPoint,
    val type: FruitType,
    val spawnTime: Long = System.currentTimeMillis(),
    val expiresAtTick: Int = -1
)
