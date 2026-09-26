package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class SnakeSkin(
    val id: String,
    val skinName: String,
    val tamilTitle: String,
    val description: String,
    val headColor: Color,
    val bodyColor: Color,
    val underbellyColor: Color,
    val eyeColor: Color,
    val crownColor: Color,
    val hasCrown: Boolean = true,
    val unlockScore: Int = 0
) {
    RAJA_PAVI(
        id = "raja_pavi",
        skinName = "Raja Pavi",
        tamilTitle = "ராஜ பாம்பு (The Crown King)",
        description = "The undisputed emperor of the jungle! Wears the legendary ruby crown.",
        headColor = CobraGreen,
        bodyColor = CobraGreenDark,
        underbellyColor = AmberGlow,
        eyeColor = RubyRed,
        crownColor = RoyalGold,
        hasCrown = true,
        unlockScore = 0
    ),
    VETTAIYAN(
        id = "vettaiyan",
        skinName = "Vettaiyan Viper",
        tamilTitle = "வேட்டையன் (Jungle Hunter)",
        description = "A fierce golden-striped hunter prowling the Kolli Hills canopy.",
        headColor = RoyalGold,
        bodyColor = RoyalGoldDark,
        underbellyColor = Color(0xFFFEF3C7),
        eyeColor = Color.Black,
        crownColor = CobraGreen,
        hasCrown = false,
        unlockScore = 150
    ),
    MINNAL_PAVI(
        id = "minnal_pavi",
        skinName = "Minnal Neon",
        tamilTitle = "மின்னல் பாம்பு (Electric Surge)",
        description = "Charged with atmospheric lightning! Slithers with neon electric pulse.",
        headColor = ElectricCyan,
        bodyColor = Color(0xFF0E7490),
        underbellyColor = Color(0xFFA5F3FC),
        eyeColor = RoyalGold,
        crownColor = ElectricCyan,
        hasCrown = true,
        unlockScore = 300
    ),
    RUBY_NAAGAM(
        id = "ruby_naagam",
        skinName = "Ruby Naagam",
        tamilTitle = "மாணிக்க நாகம் (Crimson Dragon)",
        description = "Born from ancient temple volcanic magma. Fiery crimson scales.",
        headColor = RubyRed,
        bodyColor = CrimsonDeep,
        underbellyColor = AmberGlow,
        eyeColor = GoldenSun,
        crownColor = RoyalGold,
        hasCrown = true,
        unlockScore = 500
    ),
    MYSTIC_SAGE(
        id = "mystic_sage",
        skinName = "Siddhar Naagam",
        tamilTitle = "சித்தர் நாகம் (Mystic Sage)",
        description = "Enlightened serpent marked with holy purple cosmic energy.",
        headColor = MysticPurple,
        bodyColor = Color(0xFF7E22CE),
        underbellyColor = Color(0xFFF3E8FF),
        eyeColor = ElectricCyan,
        crownColor = RoyalGold,
        hasCrown = true,
        unlockScore = 750
    )
}
