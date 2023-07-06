package com.koisv.essentials.commands

import io.github.monun.kommand.node.RootNode
import net.kyori.adventure.text.Component

object Hat {
    fun register(node: RootNode) {
        node.requires { playerOrNull != null && hasPermission(4,"admin.hat") }
        node.executes {
            player.inventory.helmet = player.inventory.itemInMainHand
            player.sendMessage(Component.text("새 모자는 어떠신지요?"))
        }
    }
}