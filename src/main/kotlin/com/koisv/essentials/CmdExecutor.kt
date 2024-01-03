package com.koisv.essentials

import com.koisv.essentials.Main.Companion.back
import com.koisv.essentials.Main.Companion.backLoc
import com.koisv.essentials.Main.Companion.instance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import kotlin.math.ceil

class CmdExecutor: CommandExecutor {

    private fun CommandSender.speedNotice(value: Int, fly: Boolean, target: Player? = null) {
        target?.sendMessage(
            Component.text("관리자에 의해 ${if (fly) "비행" else "걷기"} 속도가 ").append(
                Component.text("$value ")
                    .color(TextColor.color(255, 255, 0))
            ).append(Component.text("(으)로 설정되었습니다."))
        )

        val prefix = target?.displayName()?.append(Component.text("의 "))
            ?: Component.text("")
        sendMessage(
            prefix.append(
                Component.text("${if (fly) "비행" else "걷기"} 속도가 ")
            ).append(
                Component.text("$value ")
                    .color(TextColor.color(255, 255, 0))
            ).append(Component.text("(으)로 설정되었습니다."))
        )
    }

    override fun onCommand(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>?): Boolean {
        when (cmd.name) {
            "back" -> "kes.back"
            "hat" -> "kes.hat"
            "openinv" -> "kes.openinv"
            "spawn" -> "kes.spawn"
            "speed" -> "kes.speed"
            else -> return false
        }.run {
            if (!sender.hasPermission(this)) {
                sender.sendMessage(Component.text("[!] 권한이 부족합니다."))
                return true
            }
        }
        when (cmd.name) {
            "back" -> {
                val target = instance.server.onlinePlayers.find { it.name == (args?.getOrNull(0) ?: return@find false) }
                if (target == null) {
                    if (sender is Player) {
                        val location = back.getLocation("${sender.uniqueId}")
                        if (location != null) sender.teleportAsync(location)
                        else sender.sendMessage(Component.text("마지막 위치가 없습니다."))
                    } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")
                } else {
                    if (!sender.hasPermission("kes.back.others"))
                        sender.sendMessage(Component.text("[!] 권한이 부족합니다."))
                    else {
                        val location = back.getLocation("${target.uniqueId}")
                        if (location != null) {
                            target.teleportAsync(location)
                            sender.sendMessage(Component.text("해당 유저를 마지막 위치로 이동시켰습니다."))
                        } else sender.sendMessage(Component.text("[!] 해당 유저의 마지막 위치가 없습니다."))
                    }
                }
            }

            "hat" -> {
                if (sender !is Player)
                    gLogger.info("[!] 플레이어 전용 명령어 입니다.")
                else {
                    sender.inventory.helmet = sender.inventory.itemInMainHand
                    sender.sendMessage(Component.text("새 모자는 어떠신지요?"))
                }
            }

            "ke" -> {
                when (args?.getOrNull(0)) {
                    "reload" -> {
                        instance.reloadConfig()
                        if (!backLoc.canRead()) back.save(backLoc)
                        back.load(backLoc)
                        sender.sendMessage(Component.text("리로드 완료!"))
                    }
                    else -> { sender.sendMessage(Component.text("/ke reload - 리로드")) }
                }
            }

            "mem" -> {
                val runtime = Runtime.getRuntime()
                val report = """
                            ==========> ${Bukkit.getVersion()} <==========
                            TPS : ${Bukkit.getTPS()[0]}
                            접속자 : ${Bukkit.getOnlinePlayers().size} / ${Bukkit.getMaxPlayers()}명
                            틱 타임 : ${Bukkit.getAverageTickTime()} ms
                            버킷 : ${Bukkit.getBukkitVersion()}
                            메모리 : ${
                    (runtime.totalMemory() - runtime.freeMemory()) / 1048576
                } / ${runtime.maxMemory() / 1048576} MB
                        """.trimIndent()

                sender.sendMessage(Component.text(report))
            }

            "openinv" -> {
                val target = instance
                    .server.onlinePlayers.find {
                        it.name == (args?.getOrNull(0) ?: return@find false)
                    }
                when {
                    sender !is Player -> gLogger.info("[!] 플레이어 전용 명령어 입니다.")
                    args?.getOrNull(0) == "ender" -> {
                        if (!sender.hasPermission("kes.openinv.ender"))
                            sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                        else sender.openInventory(sender.enderChest)
                    }
                    target == null -> sender.openInventory(sender.inventory)
                    else -> {
                        if (args?.getOrNull(1) == "ender") {
                            if (!sender.hasPermission("kes.openinv.others.ender"))
                                sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                            else sender.openInventory(target.enderChest)
                        }
                        else if (!sender.hasPermission("kes.openinv.others"))
                            sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                        else sender.openInventory(target.inventory)
                    }
                }
            }

            "spawn" -> {
                val spawnData = instance.config
                val isWorldSeperated = spawnData.getBoolean("worldSep")
                val target = instance
                    .server.onlinePlayers.find {
                        it.name == (args?.getOrNull(0) ?: return@find false)
                    }
                val spawn = spawnData.getLocation("spawn${if (isWorldSeperated) ".${
                    when {
                        target != null -> target.location.world.name
                        sender is Player -> sender.location.world.name
                        else -> null
                    }
                }" else ""}")

                when (args?.getOrNull(0)) {
                    "set" -> when {
                        sender !is Player -> gLogger.info("[!] 플레이어 전용 명령어 입니다.")

                        !sender.hasPermission("kes.spawn.set") ->
                            sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                        else -> {
                            spawnData.set("spawn${if (isWorldSeperated) ".${sender.location.world.name}" else ""}", sender.location)
                            sender.sendMessage(Component.text(
                                """${sender.location.x.toInt()}, 
                                                |${sender.location.y.toInt()}, 
                                                |${sender.location.z.toInt()}
                                                |(으)로 스폰이 설정되었습니다.""".trimMargin()
                            ))
                            spawnData.save(File(instance.dataFolder, "config.yml"))
                        }
                    }

                    null ->
                        if (sender is Player) {
                            sender.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
                            sender.sendMessage(Component.text("이동되었습니다."))
                        } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")

                    else -> when {
                        target == null ->
                            sender.sendMessage(Component.text("[!] 대상을 찾을 수 없습니다!"))

                        !sender.hasPermission("kes.spawn.force") ->
                            sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                        else -> {
                            target.sendMessage(Component.text("관리자에 의해 이동되었습니다."))
                            target.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)

                            sender.sendMessage(Component.text("이동 처리되었습니다."))
                        }
                    }
                }
            }

            "speed" -> {
                if (args == null || args.isEmpty()) {
                    sender.sendMessage(Component.text("/speed [속도] [w(alk)/f(ly)/대상] [w(alk)/f(ly)]"))
                    return true
                }
                val rawSpeed = args[0].toIntOrNull() ?: kotlin.run {
                    sender.sendMessage("[!] 올바른 속도 값을 입력해주세요.")
                    return true
                }

                val applySpeed = (ceil(rawSpeed.toFloat() / 2) * 0.2).toFloat()
                val target =
                    if (args.count() == 1) null
                    else instance.server.onlinePlayers.find { it.name == args[1] }

                when (args.count()) {
                    1 -> if (sender is Player) {
                        if (sender.isFlying) sender.flySpeed = applySpeed else sender.walkSpeed = applySpeed
                        sender.speedNotice(rawSpeed, sender.isFlying)
                    } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")

                    2 -> {
                        when {
                            sender is Player -> {
                                when {
                                    args[1] == "f" || args[1] == "fly" -> {
                                        sender.flySpeed = applySpeed
                                        sender.speedNotice(rawSpeed, true)
                                    }

                                    args[1] == "w" || args[1] == "walk" -> {
                                        sender.walkSpeed = applySpeed
                                        sender.speedNotice(rawSpeed, false)
                                    }

                                    !sender.hasPermission("kes.speed.others") ->
                                        sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                                    target == null ->
                                        sender.sendMessage(Component.text("[!] 대상을 찾을 수 없습니다."))

                                    else -> {
                                        if (target.isFlying) target.flySpeed = applySpeed else target.walkSpeed = applySpeed
                                        sender.speedNotice(rawSpeed, target.isFlying, target)
                                    }
                                }
                            }

                            !sender.hasPermission("kes.speed.others") ->
                                sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                            target == null ->
                                sender.sendMessage(Component.text("[!] 대상을 찾을 수 없습니다."))

                            else -> {
                                if (target.isFlying) target.flySpeed = applySpeed else target.walkSpeed = applySpeed
                                sender.speedNotice(rawSpeed, target.isFlying, target)
                            }
                        }
                    }

                    3 -> {
                        if (target == null)
                            sender.sendMessage(Component.text("[!] 대상을 찾을 수 없습니다."))

                        else if (!sender.hasPermission("kes.speed.others"))
                            sender.sendMessage(Component.text("[!] 권한이 부족합니다."))

                        else when (args[2]) {
                            "f", "fly" -> {
                                target.flySpeed = applySpeed
                                sender.speedNotice(rawSpeed, true, target)
                            }
                            "w", "walk" -> {
                                target.walkSpeed = applySpeed
                                sender.speedNotice(rawSpeed, false, target)
                            }
                            else -> sender.sendMessage(
                                Component.text("[!] 잘못된 값입니다 - 값은 [w]alk 또는 [f]ly 이어야 합니다.")
                            )
                        }
                    }

                    else -> {
                        sender.sendMessage(
                            Component.text("[!] 너무 많은 값을 입력했습니다 - ${args.count()}/3 개")
                        )
                    }
                }
            }

            else -> return false
        }
        return true
    }
}