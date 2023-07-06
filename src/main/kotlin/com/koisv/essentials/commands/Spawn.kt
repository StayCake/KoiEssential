package com.koisv.essentials.commands

import com.koisv.essentials.Main
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.RootNode
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File

object Spawn {

    private fun getConfig() : FileConfiguration {
        return Main.instance.config
    }

    fun register(node: RootNode) {
        node.requires { playerOrNull != null && hasPermission(4,"kes.spawn") }
        node.then("set") {
            requires { playerOrNull != null && hasPermission(4,"admin.setspawn") }
            executes {
                val sepWorld = getConfig().getBoolean("worldSep")
                getConfig().set("spawn${if (sepWorld) ".${player.location.world.name}" else ""}", player.location)
                player.sendMessage(Component.text(
                    "${player.location.x.toInt()},${player.location.y.toInt()},${player.location.z.toInt()}(으)로 스폰이 설정되었습니다."
                ))
                getConfig().save(File(Main.instance.dataFolder, "config.yml"))
            }
        }
        node.then("target" to KommandArgument.player()) {
            requires { hasPermission(4,"kes.spawn") }
            executes { ctx ->
                val sepWorld = getConfig().getBoolean("worldSep")
                val target : Player by ctx
                val spawn = getConfig().getLocation("spawn${if (sepWorld) ".${player.location.world.name}" else ""}")
                target.sendMessage(Component.text("관리자에 의해 이동되었습니다."))
                target.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
                if (playerOrNull != null) 
                    player.sendMessage(Component.text("이동 처리되었습니다."))
                else println("이동 처리되었습니다.")
            }
        }
        node.executes {
            val sepWorld = getConfig().getBoolean("worldSep")
            val spawn = getConfig().getLocation("spawn${if (sepWorld) ".${player.location.world.name}" else ""}")
            player.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
            player.sendMessage(Component.text("이동되었습니다."))
        }
    }
}