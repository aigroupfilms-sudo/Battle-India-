package com.example.model

import androidx.compose.ui.graphics.Color

enum class WeaponType(
    val displayName: String,
    val caliber: String,
    val baseDamage: Float,
    val headshotMultiplier: Float,
    val fireRateMs: Long,
    val magCapacity: Int,
    val baseSpread: Float,
    val recoilVertical: Float,
    val isSniper: Boolean,
    val isHeavy: Boolean
) {
    AKM("AKM 7.62", "7.62mm", 48f, 2.5f, 100L, 30, 0.08f, 1.4f, false, true),
    M416("M416 5.56", "5.56mm", 41f, 2.3f, 85L, 30, 0.04f, 0.9f, false, false),
    SCAR_L("SCAR-L", "5.56mm", 42f, 2.2f, 95L, 30, 0.05f, 0.85f, false, false),
    MP40("MP40 Submachine", "9mm", 32f, 2.0f, 65L, 32, 0.07f, 0.6f, false, false),
    UMP45("UMP-45 Tactical", "9mm", 38f, 2.1f, 90L, 25, 0.05f, 0.7f, false, false),
    AWM("AWM Arctic Warfare", ".300 Mag", 105f, 3.0f, 1200L, 5, 0.01f, 3.5f, true, true),
    M24("M24 Sniper", "7.62mm", 82f, 2.8f, 1050L, 5, 0.02f, 2.8f, true, true),
    SHOTGUN("S1897 Shotgun", "12 Gauge", 72f, 2.0f, 750L, 5, 0.18f, 2.5f, false, true),
    DESERT_EAGLE("Desert Eagle", ".45 ACP", 55f, 2.4f, 250L, 7, 0.06f, 1.8f, false, true),
    GRENADE("Frag Grenade M67", "Explosive", 140f, 1.0f, 2000L, 1, 0f, 0f, false, true)
}

enum class PlayerStance {
    STANDING,
    CROUCHING,
    PRONE
}

enum class RankTier(val title: String, val badgeColor: Color, val minPoints: Int) {
    BRONZE("Bronze III", Color(0xFFCD7F32), 0),
    SILVER("Silver II", Color(0xFFC0C0C0), 1200),
    GOLD("Gold I", Color(0xFFFFD700), 2200),
    PLATINUM("Platinum III", Color(0xFF00E5FF), 3200),
    DIAMOND("Diamond II", Color(0xFF00B0FF), 4200),
    HEROIC("Heroic Master", Color(0xFFFF3D00), 5200),
    LEGEND("Battle India Legend", Color(0xFFFFAB00), 6500)
}

enum class VehicleType(
    val displayName: String,
    val maxSpeed: Float,
    val acceleration: Float,
    val maxHp: Float,
    val maxFuel: Float,
    val capacity: Int
) {
    JEEP("Armored Military Jeep", 72f, 18f, 600f, 100f, 4),
    BIKE("Tactical Motorbike", 95f, 32f, 250f, 70f, 2),
    CAR("Desert Buggy", 85f, 24f, 400f, 85f, 2),
    BOAT("Assault Riverboat", 60f, 15f, 500f, 100f, 4)
}

data class MapLandmark(
    val id: String,
    val name: String,
    val hindiName: String,
    val x: Float,
    val y: Float,
    val radius: Float,
    val lootTier: String,
    val description: String
)

data class GroundLoot(
    val id: String,
    val weaponType: WeaponType? = null,
    val ammoAmount: Int = 0,
    val isMedkit: Boolean = false,
    val isArmor: Boolean = false,
    val armorLevel: Int = 0,
    val isHelmet: Boolean = false,
    val helmetLevel: Int = 0,
    val x: Float,
    val y: Float
)

data class BotEntity(
    val id: String,
    val name: String,
    var x: Float,
    var y: Float,
    var angleDeg: Float,
    var hp: Float = 100f,
    val maxHp: Float = 100f,
    var currentWeapon: WeaponType = WeaponType.M416,
    var state: BotState = BotState.PATROL,
    var lastShotTime: Long = 0L,
    var targetX: Float = 0f,
    var targetY: Float = 0f,
    var isAlive: Boolean = true
)

enum class BotState {
    PATROL,
    LOOTING,
    ENGAGING,
    TAKING_COVER,
    HEALING,
    RUNNING_TO_ZONE
}

data class VehicleEntity(
    val id: String,
    val type: VehicleType,
    var x: Float,
    var y: Float,
    var rotationDeg: Float = 0f,
    var speed: Float = 0f,
    var hp: Float = 500f,
    var fuel: Float = 100f,
    var isDriverSeated: Boolean = false,
    var isTirePunctured: Boolean = false
)

data class BulletParticle(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Color,
    val creationTime: Long,
    val lifeTimeMs: Long = 120L
)

data class ExplosionEffect(
    val x: Float,
    val y: Float,
    val maxRadius: Float,
    val creationTime: Long,
    val durationMs: Long = 400L
)

data class KillFeedItem(
    val id: String,
    val killer: String,
    val victim: String,
    val weapon: String,
    val isHeadshot: Boolean,
    val isPlayerKiller: Boolean
)

data class SquadMember(
    val uid: String,
    val name: String,
    val rank: String,
    val isReady: Boolean,
    val isLeader: Boolean,
    val micOn: Boolean = false
)
