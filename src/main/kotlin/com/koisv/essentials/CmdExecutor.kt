package com.koisv.essentials

import com.koisv.essentials.Main.Companion.back
import com.koisv.essentials.Main.Companion.backLoc
import com.koisv.essentials.Main.Companion.instance
import com.koisv.essentials.Main.Companion.sendMsg
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import kotlin.math.ceil
import kotlin.math.round

class CmdExecutor: CommandExecutor {

    private fun CommandSender.speedNotice(value: Int, fly: Boolean, target: Player? = null) {
        target?.sendMessage(sysPrefix
            .append(Component.text("관리자에 의해 ${if (fly) "비행" else "걷기"} 속도가 "))
            .append(Component.text("$value ").color(TextColor.color(255, 255, 0)))
            .append(Component.text("(으)로 설정되었습니다."))
        )

        sendMessage(sysPrefix
            .append(target?.displayName()?.append(Component.text("의 ")) ?: Component.text(""))
            .append(Component.text("${if (fly) "비행" else "걷기"} 속도가 "))
            .append(Component.text("$value ").color(TextColor.color(255, 255, 0)))
            .append(Component.text("(으)로 설정되었습니다."))
        )
    }

    override fun onCommand(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>?): Boolean {
        when (cmd.name) {
            "back" -> "kes.back"
            "hat" -> "kes.hat"
            "ke" -> "kes.control"
            "mem" -> "kes.gc"
            "openinv" -> "kes.openinv"
            "spawn" -> "kes.spawn"
            "speed" -> "kes.speed"
            else -> return false
        }.run {
            if (!sender.hasPermission(this)) {
                sender.sendMsg("권한이 부족합니다.")
                return true
            }
        }
        when (cmd.name) {
            "back" -> {
                val target = instance.server.onlinePlayers.find {
                    it.name == (args?.getOrNull(0) ?: return@find false)
                }
                if (target == null) {
                    if (sender is Player) {
                        val location = back.getLocation("${sender.uniqueId}")
                        if (location != null) sender.teleportAsync(location)
                        else sender.sendMsg("[!] 마지막 위치가 없습니다.", "F14B4B")
                    } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")
                } else {
                    if (!sender.hasPermission("kes.back.others"))
                        sender.sendMsg("권한이 부족합니다.")
                    else {
                        val location = back.getLocation("${target.uniqueId}")
                        if (location != null) {
                            target.teleportAsync(location)
                            sender.sendMsg("해당 유저를 마지막 위치로 이동시켰습니다.")
                        } else sender.sendMsg("[!] 해당 유저의 마지막 위치가 없습니다.", "F14B4B")
                    }
                }
            }

            "hat" ->
                if (sender !is Player) gLogger.info("[!] 플레이어 전용 명령어 입니다.")
                else {
                    sender.inventory.helmet = sender.inventory.itemInMainHand
                    sender.sendMsg("새 모자는 어떠신지요?")
                }

            "ke" ->
                when (args?.getOrNull(0)) {
                    "reload" ->
                        if (!sender.hasPermission("kes.control.reload"))
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")
                        else {
                            instance.reloadConfig()
                            if (!backLoc.canRead()) back.save(backLoc)
                            back.load(backLoc)
                            sender.sendMsg("리로드 완료!")
                        }
                    else -> { sender.sendMsg("/ke reload - 리로드") }
                }

            "mem" -> {
                val runtime = Runtime.getRuntime()
                val real = (runtime.totalMemory() - runtime.freeMemory()) / 1048576 // 실제 사용량
                val wait = runtime.freeMemory() / 1048576 // 대기 [추가 할당된] 사용량
                val alloc = (runtime.freeMemory() + runtime.totalMemory()) / 1048576 // [사용중인] 실제 할당량
                val add = (runtime.maxMemory() - (runtime.freeMemory() + runtime.totalMemory())) / 1048576 // [가용 가능한] 추가 여유량
                val max = runtime.maxMemory() / 1048576 // [지정된] 전체 가용량

                val report =
                    buildString {
                        append("${Bukkit.getVersion()}\n")
                        append("TPS : ${round(Bukkit.getTPS()[0] * 100).div(100)}\n")
                        append("접속자 : ${Bukkit.getOnlinePlayers().size} / ${Bukkit.getMaxPlayers()}명\n")
                        append("틱 타임 : ${round(Bukkit.getAverageTickTime() * 100).div(100)} ms\n")
                        append("버킷 : ${Bukkit.getBukkitVersion()}\n")
                        append("메모리 : 사용 $real MB | 대기 $wait MB | 할당 $alloc MB | 여유 $add MB | 지정 $max MB")
                    }
                sender.sendMsg(report)
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
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                        else sender.openInventory(sender.enderChest)
                    }
                    target == null -> sender.openInventory(sender.inventory)
                    else -> {
                        if (args?.getOrNull(1) == "ender") {
                            if (!sender.hasPermission("kes.openinv.others.ender"))
                                sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                            else sender.openInventory(target.enderChest)
                        }
                        else if (!sender.hasPermission("kes.openinv.others"))
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

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
                        else -> {
                            gLogger.info("[!] 플레이어 전용 명령어 입니다.")
                            return true
                        }
                    }
                }" else ""}")

                when (args?.getOrNull(0)) {
                    "set" -> when {
                        sender !is Player -> gLogger.info("[!] 플레이어 전용 명령어 입니다.")

                        !sender.hasPermission("kes.spawn.set") ->
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                        else -> {
                            spawnData.set("spawn${if (isWorldSeperated) ".${sender.location.world.name}" else ""}", sender.location)
                            sender.sendMsg(buildString {
                                append("${sender.location.x.toInt()}, ")
                                append("${sender.location.y.toInt()}, ")
                                append("${sender.location.z.toInt()}")
                                append("(으)로 스폰이 설정되었습니다.")
                            })
                            spawnData.save(File(instance.dataFolder, "config.yml"))
                        }
                    }

                    null ->
                        if (sender is Player) {
                            sender.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
                            sender.sendMsg("이동되었습니다.")
                        } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")

                    else -> when {
                        target == null ->
                            sender.sendMsg("[!] 대상을 찾을 수 없습니다!", "F14B4B")

                        !sender.hasPermission("kes.spawn.others") ->
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                        else -> {
                            target.sendMsg("관리자에 의해 이동되었습니다.")
                            target.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)

                            sender.sendMsg("이동 처리되었습니다.")
                        }
                    }
                }
            }

            "speed" -> {
                if (args == null) return false
                else if (args.isEmpty()) {
                    if (sender is Player) {
                        val speed = ((if (sender.isFlying) sender.flySpeed else sender.walkSpeed) * 10).toInt()
                        sender.sendMsg("현재 ${if (sender.isFlying) "비행" else "걷기"} 속도는 $speed 입니다.")
                    } else sender.sendMsg("/speed [속도] [w(alk)/f(ly)/대상] [w(alk)/f(ly)]", "F14B4B")
                    return true
                }

                val rawSpeed = args[0].toIntOrNull() ?: kotlin.run {
                    sender.sendMsg("[!] 올바른 속도 값을 입력해주세요.", "F14B4B")
                    return true
                }

                if (!(-10..10).contains(rawSpeed)) {
                    val overCount = if (rawSpeed < -10) "$rawSpeed < -10" else "$rawSpeed > 10"
                    sender.sendMsg("[!] 지정할 수 있는 값을 초과했습니다 : $overCount", "F14B4B")
                    return true
                }

                when {
                    rawSpeed == 0 ->
                        sender.sendMsg("[!] 움직일 수 없는 수치입니다. 사용에 주의 바랍니다.", "F14B4B")
                    rawSpeed < 0 ->
                        sender.sendMsg("[!] 음수 지정시 플레이어 조작이 반전됩니다! 사용에 주의 바랍니다.", "FF2F2F")
                }

                val applySpeed = rawSpeed.toFloat() / 10

                val target =
                    if (args.count() == 1) null
                    else instance.server.onlinePlayers.find { it.name == args[1] }

                when (args.count()) {
                    1 -> if (sender is Player) {
                        when {
                            sender.isFlying -> sender.flySpeed = applySpeed
                            applySpeed < 0 -> {
                                sender.sendMsg("[!] 걷기 상태에서는 음수 지정이 불가능합니다.", "F14B4B")
                                return true
                            }
                            else -> sender.walkSpeed = applySpeed
                        }
                        sender.speedNotice(rawSpeed, sender.isFlying)
                    } else gLogger.info("[!] 서버 사용시 대상 지정이 필요합니다.")

                    2 -> when {
                        sender is Player -> {
                            when {
                                args[1] == "f" || args[1] == "fly" -> {
                                    sender.flySpeed = applySpeed
                                    sender.speedNotice(rawSpeed, true)
                                }

                                args[1] == "w" || args[1] == "walk" -> {
                                    if (applySpeed < 0) {
                                        sender.sendMsg("[!] 걷기 상태에서는 음수 지정이 불가능합니다.", "F14B4B")
                                        return true
                                    }
                                    sender.walkSpeed = applySpeed
                                    sender.speedNotice(rawSpeed, false)
                                }

                                !sender.hasPermission("kes.speed.others") ->
                                    sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                                target == null ->
                                    sender.sendMsg("[!] 대상을 찾을 수 없습니다.", "F14B4B")

                                else -> {
                                    when {
                                        target.isFlying -> target.flySpeed = applySpeed
                                        applySpeed < 0 -> {
                                            sender.sendMsg("[!] 걷기 상태에서는 음수 지정이 불가능합니다.", "F14B4B")
                                            return true
                                        }
                                        else -> target.walkSpeed = applySpeed
                                    }
                                    sender.speedNotice(rawSpeed, target.isFlying, target)
                                }
                            }
                        }

                        !sender.hasPermission("kes.speed.others") ->
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                        target == null ->
                            sender.sendMsg("[!] 대상을 찾을 수 없습니다.", "F14B4B")

                        else -> {
                            when {
                                target.isFlying -> target.flySpeed = applySpeed
                                applySpeed < 0 -> {
                                    sender.sendMsg("[!] 걷기 상태에서는 음수 지정이 불가능합니다.", "F14B4B")
                                    return true
                                }
                                else -> target.walkSpeed = applySpeed
                            }
                            sender.speedNotice(rawSpeed, target.isFlying, target)
                        }
                    }

                    3 ->
                        if (target == null)
                            sender.sendMsg("[!] 대상을 찾을 수 없습니다.", "F14B4B")

                        else if (!sender.hasPermission("kes.speed.others"))
                            sender.sendMsg("[!] 권한이 부족합니다.", "F14B4B")

                        else when (args[2]) {
                            "f", "fly" -> {
                                target.flySpeed = applySpeed
                                sender.speedNotice(rawSpeed, true, target)
                            }
                            "w", "walk" -> {
                                if (applySpeed < 0) {
                                    sender.sendMsg("[!] 걷기 상태에서는 음수 지정이 불가능합니다.", "F14B4B")
                                    return true
                                }
                                target.walkSpeed = applySpeed
                                sender.speedNotice(rawSpeed, false, target)
                            }
                            else -> sender.sendMsg("[!] 잘못된 값입니다 - 값은 [w]alk 또는 [f]ly 이어야 합니다.", "F14B4B")
                        }

                    else -> sender.sendMsg("[!] 너무 많은 값을 입력했습니다 : ${args.count()}/3 개", "F14B4B")
                }
            }

            else -> return false
        }
        return true
    }
}