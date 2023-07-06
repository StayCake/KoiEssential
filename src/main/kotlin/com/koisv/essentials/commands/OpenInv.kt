package com.koisv.essentials.commands

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.RootNode
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object OpenInv {
    fun register(node: RootNode) {
        node.executes {
            if (playerOrNull != null)
                player.sendMessage(Component.text("대상을 입력하세요."))
            else println("콘솔에서 사용 불가능 합니다.")
        }
        node.then("target" to KommandArgument.player()) {
            requires { requirements(playerOrNull) }
            executes { ctx ->
                val target : Player by ctx
                player.openInventory(target.inventory)
            }
        }
        node.then("ender") {
            then("target" to KommandArgument.player()) {
                requires { requirements(playerOrNull) }
                executes { ctx ->
                    val target : Player by ctx
                    player.openInventory(target.enderChest)
                }
            }
        }
    }

    private fun requirements(player: Player? = null): Boolean =
        (player != null) && player.hasPermission("admin.openinv")
}