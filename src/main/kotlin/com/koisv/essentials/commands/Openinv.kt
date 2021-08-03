package com.koisv.essentials.commands

import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.entity.Player

object Openinv {
    fun register(node: LiteralNode) {
        node.executes {
            if (playerOrNull != null) player.msg("대상을 입력하세요.") else println("콘솔에서 사용 불가능 합니다.")
        }
        node.then("target" to KommandArgument.player()) {
            requires { playerOrNull != null && hasPermission(4,"admin.openinv") }
            executes { ctx ->
                val target : Player by ctx
                player.openInventory(target.inventory)
            }
        }
        node.then("ender") {
            then("target" to KommandArgument.player()) {
                requires { playerOrNull != null && hasPermission(4,"admin.openinv") }
                executes { ctx ->
                    val target : Player by ctx
                    player.openInventory(target.enderChest)
                }
            }
        }
    }
}