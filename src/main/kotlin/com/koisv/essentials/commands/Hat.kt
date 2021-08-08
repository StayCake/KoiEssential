package com.koisv.essentials.commands

import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.node.LiteralNode

object Hat {
    fun register(node: LiteralNode) {
        node.requires { playerOrNull != null && hasPermission(4,"admin.hat") }
        node.executes {
            player.inventory.helmet = player.inventory.itemInMainHand
            player.msg("새 모자는 어떠신지요?")
        }
    }
}