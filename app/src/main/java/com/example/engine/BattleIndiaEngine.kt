package com.example.engine

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.audio.SoundManager
import com.example.model.BotEntity
import com.example.model.BotState
import com.example.model.BulletParticle
import com.example.model.ExplosionEffect
import com.example.model.GroundLoot
import com.example.model.KillFeedItem
import com.example.model.MapLandmark
import com.example.model.PlayerStance
import com.example.model.VehicleEntity
import com.example.model.VehicleType
import com.example.model.WeaponType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class MatchPhase {
    AIRPLANE_FLYOVER,
    PARACHUTE_DESCENT,
    ACTIVE_COMBAT,
    MATCH_OVER
}

class BattleIndiaEngine {
    // Map dimensions
    val mapWidth = 2400f
    val mapHeight = 2400f

    // Match phase
    var matchPhase by mutableStateOf(MatchPhase.AIRPLANE_FLYOVER)
    var isVictory by mutableStateOf(false)
    var finalPlacement by mutableIntStateOf(1)

    // Airplane trajectory
    var planeX by mutableFloatStateOf(100f)
    var planeY by mutableFloatStateOf(300f)
    var planeSpeed by mutableFloatStateOf(160f)
    val planeStartX = 100f
    val planeStartY = 300f
    val planeEndX = 2300f
    val planeEndY = 2100f

    // Parachute state
    var playerAltitude by mutableFloatStateOf(1500f)
    var parachuteDeployed by mutableStateOf(false)

    // Player state
    var playerX by mutableFloatStateOf(1200f)
    var playerY by mutableFloatStateOf(1200f)
    var playerRotationDeg by mutableFloatStateOf(0f)
    var playerHp by mutableFloatStateOf(100f)
    val maxHp = 100f
    var playerArmor by mutableFloatStateOf(100f)
    var stance by mutableStateOf(PlayerStance.STANDING)
    var isSprinting by mutableStateOf(false)
    var isAimingScope by mutableStateOf(false)
    var screenShakeAmount by mutableFloatStateOf(0f)

    // Inventory & Weapons
    var primaryWeapon by mutableStateOf(WeaponType.M416)
    var secondaryWeapon by mutableStateOf(WeaponType.AKM)
    var currentWeaponSlot by mutableIntStateOf(1) // 1 or 2
    val activeWeapon: WeaponType
        get() = if (currentWeaponSlot == 1) primaryWeapon else secondaryWeapon

    var magAmmo1 by mutableIntStateOf(WeaponType.M416.magCapacity)
    var reserveAmmo1 by mutableIntStateOf(180)
    var magAmmo2 by mutableIntStateOf(WeaponType.AKM.magCapacity)
    var reserveAmmo2 by mutableIntStateOf(150)

    var medkitsCount by mutableIntStateOf(3)
    var grenadesCount by mutableIntStateOf(2)
    var helmetLevel by mutableIntStateOf(2)
    var vestLevel by mutableIntStateOf(2)

    var isReloading by mutableStateOf(false)
    private var lastShotTime = 0L

    // Driving
    var currentVehicle by mutableStateOf<VehicleEntity?>(null)
    val isDriving: Boolean get() = currentVehicle != null

    // Safe Zone
    var safeZoneCenterX by mutableFloatStateOf(1200f)
    var safeZoneCenterY by mutableFloatStateOf(1200f)
    var safeZoneRadius by mutableFloatStateOf(1100f)
    var targetSafeZoneRadius by mutableFloatStateOf(650f)
    var zonePhase by mutableIntStateOf(1)
    var zoneTimerSeconds by mutableIntStateOf(90)
    var isZoneShrinking by mutableStateOf(false)
    var isPlayerInStorm by mutableStateOf(false)

    // Red Zone
    var redZoneActive by mutableStateOf(false)
    var redZoneX by mutableFloatStateOf(900f)
    var redZoneY by mutableFloatStateOf(1000f)
    var redZoneRadius by mutableFloatStateOf(240f)
    var redZoneTimer by mutableIntStateOf(40)

    // Supply Drop
    var supplyDropX by mutableFloatStateOf(1150f)
    var supplyDropY by mutableFloatStateOf(1300f)
    var isSupplyDropLanded by mutableStateOf(false)
    var supplyDropActive by mutableStateOf(true)

    // Entities
    val bots = mutableStateListOf<BotEntity>()
    val vehicles = mutableStateListOf<VehicleEntity>()
    val groundLoots = mutableStateListOf<GroundLoot>()
    val bulletParticles = mutableStateListOf<BulletParticle>()
    val explosionEffects = mutableStateListOf<ExplosionEffect>()
    val killFeed = mutableStateListOf<KillFeedItem>()

    // Stats
    var alivePlayersCount by mutableIntStateOf(50)
    var killCount by mutableIntStateOf(0)
    var damageDealt by mutableFloatStateOf(0f)
    var headshotKills by mutableIntStateOf(0)
    var matchStartTime by mutableLongStateOf(System.currentTimeMillis())

    // Landmarks
    val landmarks = listOf(
        MapLandmark("1", "Varanasi Ghats & Temples", "काशी मन्दिर", 450f, 500f, 220f, "High", "Ancient stone temples along sacred waters"),
        MapLandmark("2", "Pokhran Military Base", "पोखरण छावनी", 1850f, 450f, 260f, "Military", "Fortified bunkers, watchtowers, heavy artillery"),
        MapLandmark("3", "Mumbai Metro & Shipping Port", "मुंबई बंदरगाह", 400f, 1750f, 280f, "High", "Shipping containers, cranes, skyscrapers"),
        MapLandmark("4", "Ganges River & Iron Bridges", "गंगा सेतु", 1200f, 1200f, 350f, "Medium", "Major central river dividing North & South zones"),
        MapLandmark("5", "Thar Desert Outpost", "थार मरुस्थल", 1950f, 1800f, 240f, "Medium", "Sandy dunes, clay settlements, oasis palms"),
        MapLandmark("6", "Punjab Mustard Farms", "पंजाब खेत", 900f, 650f, 200f, "Medium", "Golden agricultural fields, silos, barns"),
        MapLandmark("7", "Nilgiri Pine Forest", "नीलगिरि वन", 1600f, 1300f, 260f, "High", "Dense forest canopy, rocky ridges, tactical cover"),
        MapLandmark("8", "Howrah Bridge Crossroad", "हावड़ा पुल", 850f, 1450f, 180f, "Chokepoint", "Massive cantilever steel bridge crossing river")
    )

    fun resetMatch() {
        matchPhase = MatchPhase.AIRPLANE_FLYOVER
        isVictory = false
        finalPlacement = 1
        planeX = planeStartX
        planeY = planeStartY
        playerAltitude = 1500f
        parachuteDeployed = false
        playerHp = 100f
        playerArmor = 100f
        stance = PlayerStance.STANDING
        isSprinting = false
        isAimingScope = false
        screenShakeAmount = 0f
        currentVehicle = null
        alivePlayersCount = 50
        killCount = 0
        damageDealt = 0f
        headshotKills = 0
        matchStartTime = System.currentTimeMillis()

        // Weapons
        primaryWeapon = WeaponType.M416
        secondaryWeapon = WeaponType.AKM
        currentWeaponSlot = 1
        magAmmo1 = primaryWeapon.magCapacity
        reserveAmmo1 = 180
        magAmmo2 = secondaryWeapon.magCapacity
        reserveAmmo2 = 150
        medkitsCount = 3
        grenadesCount = 2
        isReloading = false

        // Safe Zone
        safeZoneCenterX = 1200f
        safeZoneCenterY = 1200f
        safeZoneRadius = 1100f
        targetSafeZoneRadius = 650f
        zonePhase = 1
        zoneTimerSeconds = 60
        isZoneShrinking = false
        isPlayerInStorm = false

        // Red Zone
        redZoneActive = true
        redZoneX = 900f
        redZoneY = 900f
        redZoneRadius = 260f
        redZoneTimer = 35

        // Airdrop
        supplyDropX = 1250f
        supplyDropY = 1100f
        isSupplyDropLanded = false
        supplyDropActive = true

        // Populate Bots
        bots.clear()
        val botNames = listOf(
            "Vikram_Warrior", "Aarav_Sniper", "Rohan_Tactical", "Kabir_Hunter",
            "Dev_Ranger", "Arjun_Strike", "Rahul_Apex", "Sanjay_Marksman",
            "Amit_Ghost", "Karan_Predator", "Veer_Rajput", "Manish_Titan",
            "Ananya_Valkyrie", "Pooja_Phantom", "Suraj_Fire", "Deepak_Cobra",
            "Gaurav_Iron", "Naveen_Storm", "Rishi_Blaze", "Aditya_Viper"
        )
        val rand = Random(42)
        val weaponsPool = listOf(WeaponType.AKM, WeaponType.M416, WeaponType.SCAR_L, WeaponType.MP40, WeaponType.AWM, WeaponType.SHOTGUN)

        for (i in botNames.indices) {
            val bx = 300f + rand.nextFloat() * 1800f
            val by = 300f + rand.nextFloat() * 1800f
            bots.add(
                BotEntity(
                    id = "bot_$i",
                    name = botNames[i],
                    x = bx,
                    y = by,
                    angleDeg = rand.nextFloat() * 360f,
                    hp = 100f,
                    currentWeapon = weaponsPool[rand.nextInt(weaponsPool.size)],
                    targetX = bx,
                    targetY = by
                )
            )
        }

        // Populate Vehicles
        vehicles.clear()
        vehicles.add(VehicleEntity("v1", VehicleType.JEEP, 750f, 850f, 45f))
        vehicles.add(VehicleEntity("v2", VehicleType.BIKE, 1400f, 600f, 90f))
        vehicles.add(VehicleEntity("v3", VehicleType.CAR, 1750f, 1550f, 180f))
        vehicles.add(VehicleEntity("v4", VehicleType.BOAT, 1200f, 1150f, 270f))

        // Populate Ground Loot
        groundLoots.clear()
        for (landmark in landmarks) {
            groundLoots.add(GroundLoot("l_${landmark.id}_1", WeaponType.AKM, 60, false, false, 0, false, 0, landmark.x - 30f, landmark.y - 20f))
            groundLoots.add(GroundLoot("l_${landmark.id}_2", WeaponType.M416, 60, false, false, 0, false, 0, landmark.x + 40f, landmark.y - 10f))
            groundLoots.add(GroundLoot("l_${landmark.id}_3", null, 0, true, false, 0, false, 0, landmark.x - 20f, landmark.y + 40f))
            groundLoots.add(GroundLoot("l_${landmark.id}_4", null, 0, false, true, 2, false, 0, landmark.x + 30f, landmark.y + 30f))
            groundLoots.add(GroundLoot("l_${landmark.id}_5", WeaponType.AWM, 20, false, false, 0, true, 3, landmark.x, landmark.y - 50f))
        }

        bulletParticles.clear()
        explosionEffects.clear()
        killFeed.clear()
    }

    init {
        resetMatch()
    }

    // Jump from airplane
    fun jumpFromPlane() {
        if (matchPhase == MatchPhase.AIRPLANE_FLYOVER) {
            playerX = planeX
            playerY = planeY
            playerAltitude = 1400f
            matchPhase = MatchPhase.PARACHUTE_DESCENT
            SoundManager.playZoneWarning()
        }
    }

    fun deployParachute() {
        if (matchPhase == MatchPhase.PARACHUTE_DESCENT) {
            parachuteDeployed = true
        }
    }

    // Main 60 FPS update loop
    fun update(dt: Float) {
        if (screenShakeAmount > 0f) {
            screenShakeAmount = (screenShakeAmount - dt * 5f).coerceAtLeast(0f)
        }

        when (matchPhase) {
            MatchPhase.AIRPLANE_FLYOVER -> {
                val dx = planeEndX - planeStartX
                val dy = planeEndY - planeStartY
                val dist = sqrt(dx * dx + dy * dy)
                val dirX = dx / dist
                val dirY = dy / dist
                planeX += dirX * planeSpeed * dt
                planeY += dirY * planeSpeed * dt
                // Auto jump if reaching end
                if (planeX >= planeEndX - 50f) {
                    jumpFromPlane()
                }
            }
            MatchPhase.PARACHUTE_DESCENT -> {
                val fallSpeed = if (parachuteDeployed) 55f else 180f
                playerAltitude -= fallSpeed * dt
                if (playerAltitude <= 0f) {
                    playerAltitude = 0f
                    matchPhase = MatchPhase.ACTIVE_COMBAT
                    SoundManager.playReload()
                }
            }
            MatchPhase.ACTIVE_COMBAT -> {
                updateCombat(dt)
            }
            MatchPhase.MATCH_OVER -> {}
        }

        // Clean up particles
        val now = System.currentTimeMillis()
        bulletParticles.removeAll { now - it.creationTime > it.lifeTimeMs }
        explosionEffects.removeAll { now - it.creationTime > it.durationMs }
    }

    private fun updateCombat(dt: Float) {
        // Vehicle driving physics
        currentVehicle?.let { v ->
            if (v.fuel > 0f && v.hp > 0f) {
                val rad = Math.toRadians(v.rotationDeg.toDouble())
                v.x += (sin(rad) * v.speed * dt).toFloat()
                v.y -= (cos(rad) * v.speed * dt).toFloat()
                v.fuel = (v.fuel - dt * 0.4f).coerceAtLeast(0f)
                // Sync player coordinates to vehicle
                playerX = v.x
                playerY = v.y
                playerRotationDeg = v.rotationDeg

                // Roadkill collision with bots
                for (bot in bots) {
                    if (bot.isAlive && v.speed > 30f) {
                        val dist = distance(v.x, v.y, bot.x, bot.y)
                        if (dist < 40f) {
                            damageBot(bot, 120f, isHeadshot = false)
                            SoundManager.playExplosion()
                        }
                    }
                }
            }
        }

        // Safe zone mechanics
        updateSafeZone(dt)

        // Red zone mechanics
        updateRedZone(dt)

        // Bot AI simulation
        updateBots(dt)

        // Check victory or death
        if (playerHp <= 0f && matchPhase == MatchPhase.ACTIVE_COMBAT) {
            matchPhase = MatchPhase.MATCH_OVER
            isVictory = false
            finalPlacement = alivePlayersCount
            SoundManager.playExplosion()
        } else if (alivePlayersCount <= 1 && playerHp > 0f && matchPhase == MatchPhase.ACTIVE_COMBAT) {
            matchPhase = MatchPhase.MATCH_OVER
            isVictory = true
            finalPlacement = 1
            SoundManager.playVictoryFanfare()
        }
    }

    private fun updateSafeZone(dt: Float) {
        if (!isZoneShrinking) {
            // Countdown until next shrink
            if (Math.random() < dt / 1.0) {
                // Approximate 1 second tick
                zoneTimerSeconds = (zoneTimerSeconds - 1).coerceAtLeast(0)
                if (zoneTimerSeconds <= 0) {
                    isZoneShrinking = true
                    SoundManager.playZoneWarning()
                }
            }
        } else {
            // Shrinking
            if (safeZoneRadius > targetSafeZoneRadius) {
                safeZoneRadius -= 18f * dt
            } else {
                safeZoneRadius = targetSafeZoneRadius
                isZoneShrinking = false
                zonePhase++
                targetSafeZoneRadius = (safeZoneRadius * 0.55f).coerceAtLeast(120f)
                zoneTimerSeconds = 60 - (zonePhase * 8).coerceAtMost(30)
            }
        }

        // Check if player is outside safe zone
        val distToCenter = distance(playerX, playerY, safeZoneCenterX, safeZoneCenterY)
        isPlayerInStorm = distToCenter > safeZoneRadius
        if (isPlayerInStorm) {
            // Storm tick damage
            val stormDps = 3.5f * zonePhase
            playerHp = (playerHp - stormDps * dt).coerceAtLeast(0f)
        }
    }

    private fun updateRedZone(dt: Float) {
        if (redZoneActive) {
            if (Math.random() < dt / 1.0) {
                redZoneTimer = (redZoneTimer - 1).coerceAtLeast(0)
                if (redZoneTimer <= 0) {
                    // Trigger artillery bombardment
                    triggerArtilleryStrike()
                    redZoneTimer = 45
                    // Move red zone
                    redZoneX = 400f + Random.nextFloat() * 1600f
                    redZoneY = 400f + Random.nextFloat() * 1600f
                }
            }
        }
    }

    private fun triggerArtilleryStrike() {
        SoundManager.playExplosion()
        screenShakeAmount = 18f
        // Spawn explosion effects inside red zone
        for (i in 0..4) {
            val ex = redZoneX + (Random.nextFloat() * 2f - 1f) * redZoneRadius * 0.7f
            val ey = redZoneY + (Random.nextFloat() * 2f - 1f) * redZoneRadius * 0.7f
            explosionEffects.add(
                ExplosionEffect(
                    x = ex,
                    y = ey,
                    maxRadius = 70f,
                    creationTime = System.currentTimeMillis() + i * 80L
                )
            )
            // Damage player if close
            val pDist = distance(playerX, playerY, ex, ey)
            if (pDist < 70f) {
                playerHp = (playerHp - 65f).coerceAtLeast(0f)
            }
            // Damage bots in blast
            for (bot in bots) {
                if (bot.isAlive && distance(bot.x, bot.y, ex, ey) < 70f) {
                    damageBot(bot, 85f, false)
                }
            }
        }
    }

    private fun updateBots(dt: Float) {
        val now = System.currentTimeMillis()
        for (bot in bots) {
            if (!bot.isAlive) continue

            // Distance to player
            val distToPlayer = distance(bot.x, bot.y, playerX, playerY)
            val distToSafeZone = distance(bot.x, bot.y, safeZoneCenterX, safeZoneCenterY)

            // Outside zone priority
            if (distToSafeZone > safeZoneRadius) {
                bot.state = BotState.RUNNING_TO_ZONE
                bot.hp = (bot.hp - 4f * dt).coerceAtLeast(0f)
                if (bot.hp <= 0f) {
                    eliminateBot(bot, "Safe Zone Electric Storm", isPlayerKiller = false)
                    continue
                }
            } else if (distToPlayer < 320f && playerHp > 0f) {
                bot.state = BotState.ENGAGING
            } else if (bot.hp < 40f) {
                bot.state = BotState.HEALING
            } else {
                bot.state = BotState.PATROL
            }

            when (bot.state) {
                BotState.RUNNING_TO_ZONE -> {
                    val angle = atan2(safeZoneCenterY - bot.y, safeZoneCenterX - bot.x)
                    bot.x += cos(angle) * 75f * dt
                    bot.y += sin(angle) * 75f * dt
                    bot.angleDeg = Math.toDegrees(angle.toDouble()).toFloat()
                }
                BotState.ENGAGING -> {
                    val angle = atan2(playerY - bot.y, playerX - bot.x)
                    bot.angleDeg = Math.toDegrees(angle.toDouble()).toFloat()

                    // Move closer or strafe
                    if (distToPlayer > 180f) {
                        bot.x += cos(angle) * 50f * dt
                        bot.y += sin(angle) * 50f * dt
                    }

                    // Bot shooting player
                    if (now - bot.lastShotTime > bot.currentWeapon.fireRateMs * 3) {
                        bot.lastShotTime = now
                        // Bot accuracy check
                        val hit = Random.nextFloat() < 0.35f
                        if (hit && !isDriving) {
                            val botDmg = bot.currentWeapon.baseDamage * 0.4f
                            applyDamageToPlayer(botDmg)
                            SoundManager.playGunshot(bot.currentWeapon.isHeavy, bot.currentWeapon.isSniper)
                            bulletParticles.add(
                                BulletParticle(
                                    startX = bot.x,
                                    startY = bot.y,
                                    endX = playerX + (Random.nextFloat() * 10f - 5f),
                                    endY = playerY + (Random.nextFloat() * 10f - 5f),
                                    color = Color(0xFFFF5252),
                                    creationTime = now
                                )
                            )
                        }
                    }
                }
                BotState.HEALING -> {
                    bot.hp = (bot.hp + 12f * dt).coerceAtMost(bot.maxHp)
                    if (bot.hp >= 80f) bot.state = BotState.PATROL
                }
                BotState.PATROL, BotState.LOOTING, BotState.TAKING_COVER -> {
                    // Wander slowly
                    if (distance(bot.x, bot.y, bot.targetX, bot.targetY) < 20f || Math.random() < 0.02) {
                        bot.targetX = bot.x + (Random.nextFloat() * 300f - 150f).coerceIn(100f, mapWidth - 100f)
                        bot.targetY = bot.y + (Random.nextFloat() * 300f - 150f).coerceIn(100f, mapHeight - 100f)
                    }
                    val angle = atan2(bot.targetY - bot.y, bot.targetX - bot.x)
                    bot.x += cos(angle) * 35f * dt
                    bot.y += sin(angle) * 35f * dt
                    bot.angleDeg = Math.toDegrees(angle.toDouble()).toFloat()
                }
            }
        }

        // Random bot-on-bot elimination in background
        if (bots.count { it.isAlive } > 1 && Math.random() < 0.03 * dt * 60) {
            val aliveBots = bots.filter { it.isAlive }
            if (aliveBots.size >= 2) {
                val killer = aliveBots[Random.nextInt(aliveBots.size)]
                val victim = aliveBots.filter { it.id != killer.id }.randomOrNull()
                if (victim != null) {
                    eliminateBot(victim, killer.name, isPlayerKiller = false)
                }
            }
        }
    }

    private fun applyDamageToPlayer(amount: Float) {
        screenShakeAmount = 6f
        var remainingDmg = amount
        if (playerArmor > 0f) {
            val absorbed = (remainingDmg * 0.6f).coerceAtMost(playerArmor)
            playerArmor -= absorbed
            remainingDmg -= absorbed
        }
        playerHp = (playerHp - remainingDmg).coerceAtLeast(0f)
    }

    // Player Actions
    fun movePlayer(stickX: Float, stickY: Float, dt: Float) {
        if (isDriving) {
            currentVehicle?.let { v ->
                // Steer vehicle
                v.rotationDeg += stickX * 90f * dt
                // Accelerate or reverse
                if (stickY < -0.2f) {
                    v.speed = (v.speed + v.type.acceleration * dt).coerceAtMost(v.type.maxSpeed)
                } else if (stickY > 0.2f) {
                    v.speed = (v.speed - v.type.acceleration * 1.5f * dt).coerceAtLeast(-25f)
                } else {
                    v.speed = (v.speed * 0.96f)
                }
            }
            return
        }

        if (stickX == 0f && stickY == 0f) return

        val speedMultiplier = when (stance) {
            PlayerStance.STANDING -> if (isSprinting) 180f else 120f
            PlayerStance.CROUCHING -> 75f
            PlayerStance.PRONE -> 45f
        }

        val angleRad = atan2(stickY.toDouble(), stickX.toDouble())
        playerRotationDeg = Math.toDegrees(angleRad).toFloat() + 90f

        playerX = (playerX + stickX * speedMultiplier * dt).coerceIn(50f, mapWidth - 50f)
        playerY = (playerY + stickY * speedMultiplier * dt).coerceIn(50f, mapHeight - 50f)
    }

    fun fireWeapon() {
        if (isReloading || isDriving) return
        val now = System.currentTimeMillis()
        val weapon = activeWeapon
        if (now - lastShotTime < weapon.fireRateMs) return

        // Ammo check
        if (currentWeaponSlot == 1) {
            if (magAmmo1 <= 0) {
                reloadWeapon()
                return
            }
            magAmmo1--
        } else {
            if (magAmmo2 <= 0) {
                reloadWeapon()
                return
            }
            magAmmo2--
        }

        lastShotTime = now
        SoundManager.playGunshot(weapon.isHeavy, weapon.isSniper)
        screenShakeAmount = weapon.recoilVertical * 3.5f

        // Calculate bullet trajectory
        val aimRad = Math.toRadians((playerRotationDeg - 90.0))
        val spreadAngle = (Random.nextFloat() * 2f - 1f) * (if (isAimingScope) weapon.baseSpread * 0.3f else weapon.baseSpread)
        val bulletRad = aimRad + spreadAngle

        val maxRange = if (weapon.isSniper) 700f else 450f
        var hitX = playerX + (cos(bulletRad) * maxRange).toFloat()
        var hitY = playerY + (sin(bulletRad) * maxRange).toFloat()

        // Check raycast against bots
        var hitBot: BotEntity? = null
        var isHeadshot = false
        var closestDist = maxRange

        for (bot in bots) {
            if (!bot.isAlive) continue
            val dist = distance(playerX, playerY, bot.x, bot.y)
            if (dist < closestDist) {
                // Vector to bot
                val toBotX = bot.x - playerX
                val toBotY = bot.y - playerY
                val botAngle = atan2(toBotY.toDouble(), toBotX.toDouble())
                val angleDiff = Math.abs(bulletRad - botAngle)
                // If aligned within angular tolerance
                if (angleDiff < 0.18) {
                    closestDist = dist
                    hitBot = bot
                    hitX = bot.x
                    hitY = bot.y
                    isHeadshot = angleDiff < 0.05
                }
            }
        }

        // Add tracer particle
        bulletParticles.add(
            BulletParticle(
                startX = playerX,
                startY = playerY,
                endX = hitX,
                endY = hitY,
                color = if (isHeadshot) Color(0xFFFFD700) else Color(0xFFFF9100),
                creationTime = now
            )
        )

        // Damage hit bot
        hitBot?.let { bot ->
            val damage = weapon.baseDamage * (if (isHeadshot) weapon.headshotMultiplier else 1.0f)
            damageDealt += damage
            damageBot(bot, damage, isHeadshot)
        }
    }

    private fun damageBot(bot: BotEntity, damage: Float, isHeadshot: Boolean) {
        bot.hp = (bot.hp - damage).coerceAtLeast(0f)
        if (bot.hp <= 0f) {
            eliminateBot(bot, "You (Tiger_One)", isPlayerKiller = true)
            killCount++
            if (isHeadshot) headshotKills++
            SoundManager.playButtonClick()
        }
    }

    private fun eliminateBot(bot: BotEntity, killerName: String, isPlayerKiller: Boolean) {
        bot.isAlive = false
        bot.hp = 0f
        alivePlayersCount = (alivePlayersCount - 1).coerceAtLeast(1)

        // Spawn death crate
        groundLoots.add(GroundLoot("crate_${bot.id}", bot.currentWeapon, 90, true, true, 2, true, 2, bot.x, bot.y))

        // Add kill feed
        killFeed.add(
            0,
            KillFeedItem(
                id = "kf_${System.currentTimeMillis()}_${bot.id}",
                killer = killerName,
                victim = bot.name,
                weapon = if (isPlayerKiller) activeWeapon.displayName else "Combat",
                isHeadshot = isPlayerKiller && activeWeapon.isSniper,
                isPlayerKiller = isPlayerKiller
            )
        )
        if (killFeed.size > 5) killFeed.removeLast()
    }

    fun reloadWeapon() {
        if (isReloading) return
        val currentMag = if (currentWeaponSlot == 1) magAmmo1 else magAmmo2
        val currentReserve = if (currentWeaponSlot == 1) reserveAmmo1 else reserveAmmo2
        val capacity = activeWeapon.magCapacity

        if (currentMag >= capacity || currentReserve <= 0) return

        isReloading = true
        SoundManager.playReload()

        // Asynchronous reload completion after 1.8 seconds
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            kotlinx.coroutines.delay(1800)
            val needed = capacity - currentMag
            val toLoad = needed.coerceAtMost(currentReserve)
            if (currentWeaponSlot == 1) {
                magAmmo1 += toLoad
                reserveAmmo1 -= toLoad
            } else {
                magAmmo2 += toLoad
                reserveAmmo2 -= toLoad
            }
            isReloading = false
        }
    }

    fun switchWeapon(slot: Int) {
        if (currentWeaponSlot != slot) {
            currentWeaponSlot = slot
            SoundManager.playReload()
        }
    }

    fun useMedkit() {
        if (medkitsCount > 0 && playerHp < maxHp) {
            medkitsCount--
            playerHp = (playerHp + 75f).coerceAtMost(maxHp)
            SoundManager.playZoneWarning()
        }
    }

    fun throwGrenade() {
        if (grenadesCount > 0) {
            grenadesCount--
            SoundManager.playButtonClick()
            val aimRad = Math.toRadians((playerRotationDeg - 90.0))
            val gx = playerX + (cos(aimRad) * 220f).toFloat()
            val gy = playerY + (sin(aimRad) * 220f).toFloat()

            // Detonate after 1.5s
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
                kotlinx.coroutines.delay(1500)
                SoundManager.playExplosion()
                screenShakeAmount = 14f
                explosionEffects.add(ExplosionEffect(gx, gy, 80f, System.currentTimeMillis()))
                for (bot in bots) {
                    if (bot.isAlive && distance(gx, gy, bot.x, bot.y) < 80f) {
                        damageBot(bot, 140f, isHeadshot = false)
                    }
                }
            }
        }
    }

    fun toggleVehicleEntry() {
        if (isDriving) {
            // Exit
            currentVehicle?.isDriverSeated = false
            currentVehicle = null
            SoundManager.playButtonClick()
        } else {
            // Find closest vehicle within 50 units
            val nearby = vehicles.firstOrNull { distance(playerX, playerY, it.x, it.y) < 55f }
            nearby?.let { v ->
                v.isDriverSeated = true
                currentVehicle = v
                SoundManager.playVehicleHorn()
            }
        }
    }

    fun honkHorn() {
        if (isDriving) {
            SoundManager.playVehicleHorn()
        }
    }

    fun pickupNearbyLoot() {
        val nearby = groundLoots.firstOrNull { distance(playerX, playerY, it.x, it.y) < 45f }
        nearby?.let { loot ->
            loot.weaponType?.let { w ->
                if (currentWeaponSlot == 1) {
                    primaryWeapon = w
                    magAmmo1 = w.magCapacity
                    reserveAmmo1 += 60
                } else {
                    secondaryWeapon = w
                    magAmmo2 = w.magCapacity
                    reserveAmmo2 += 60
                }
            }
            if (loot.ammoAmount > 0) {
                if (currentWeaponSlot == 1) reserveAmmo1 += loot.ammoAmount else reserveAmmo2 += loot.ammoAmount
            }
            if (loot.isMedkit) medkitsCount++
            if (loot.isArmor) playerArmor = 100f
            groundLoots.remove(loot)
            SoundManager.playButtonClick()
        }
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }
}
