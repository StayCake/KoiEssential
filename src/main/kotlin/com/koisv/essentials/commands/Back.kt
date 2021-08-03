package com.koisv.essentials.commands

import com.koisv.essentials.Main
import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

object Back {

    private fun getBack() : YamlConfiguration {
        return Main.back
    }

    fun register(node: LiteralNode) {
        node.requires { playerOrNull != null && hasPermission(4,"admin.back") }
        node.executes {
            val location = getBack().getLocation("${player.uniqueId}")
            if (location != null) player.teleportAsync(location) else player.msg("마지막 위치가 없습니다.")
        }
        node.then("target" to KommandArgument.player()){
            requires { hasPermission(4,"admin.back") }
            executes { ctx ->
                val target : Player by ctx
                val location = getBack().getLocation("${target.uniqueId}")
                if (location != null) {
                    target.teleportAsync(location)
                    player.msg("해당 유저를 마지막 위치로 이동시켰습니다.")
                } else player.msg("해당 유저의 마지막 위치가 없습니다.")
            }
        }
    }
}