package com.koisv.essentials.commands

import com.koisv.essentials.Main
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

object Spawn {

    private fun getConfig() : FileConfiguration {
        return Main.instance.config
    }

    fun register(node:LiteralNode) {
        node.requires { playerOrNull != null && hasPermission(4,"kes.spawn") }
        node.then("set") {
            requires { playerOrNull != null && hasPermission(4,"admin.setspawn") }
            executes {
                getConfig().set("spawn",player.location)
                player.msg("${player.location.x.toInt()},${player.location.y.toInt()},${player.location.z.toInt()}(으)로 스폰이 설정되었습니다.")
                getConfig().save(Main.instance.dataFolder["config.yml"])
                getConfig().load(Main.instance.dataFolder["config.yml"])
            }
        }
        node.then("target" to KommandArgument.player()) {
            requires { hasPermission(4,"kes.spawn") }
            executes { ctx ->
                val target : Player by ctx
                val spawn = getConfig().getLocation("spawn")
                target.msg("관리자에 의해 이동되었습니다.")
                target.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
                if (playerOrNull != null) player.msg("이동 처리되었습니다.") else println("이동 처리되었습니다.")
            }
        }
        node.executes {
            val spawn = getConfig().getLocation("spawn")
            player.teleportAsync(spawn ?: Bukkit.getWorlds()[0].spawnLocation)
            player.msg("이동되었습니다.")
        }
    }
}