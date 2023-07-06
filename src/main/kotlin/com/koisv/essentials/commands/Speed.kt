package com.koisv.essentials.commands

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.RootNode
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import kotlin.math.ceil

object Speed {
    fun register(node: RootNode) {
        node.then("value" to KommandArgument.int(1,10)) {
            requires { hasPermission(4,"admin.speed") }
            executes { ctx ->
                if (playerOrNull != null) {
                    val value: Int by ctx
                    when (player.isFlying) {
                        true -> {
                            val speed = (value * 0.1).toFloat()
                            player.flySpeed = speed
                            notice(value, player.isFlying, player)
                        }
                        false -> {
                            val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                            player.walkSpeed = speed
                            notice(value, player.isFlying, player)
                        }
                    }
                } else {
                    println("대상을 입력하세요.")
                }
            }
            then("target" to KommandArgument.player()) {
                requires { hasPermission(4,"admin.speed") }
                executes { ctx ->
                    val value: Int by ctx
                    val target : Player by ctx
                    when (target.isFlying) {
                        true -> {
                            val speed = (value * 0.1).toFloat()
                            target.flySpeed = speed
                            notice(value, target.isFlying, player, target)
                        }
                        false -> {
                            val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                            target.walkSpeed = speed
                            notice(value, target.isFlying, player, target)
                        }
                    }
                }
                then("fly") {
                    requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                    executes { ctx ->
                        val value: Int by ctx
                        val target : Player by ctx
                        val speed = (value * 0.1).toFloat()
                        target.flySpeed = speed
                        notice(value, true, player, target)
                    }
                }
                then("walk") {
                    requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                    executes { ctx ->
                        val value: Int by ctx
                        val target : Player by ctx
                        val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                        target.walkSpeed = speed
                        notice(value, false, player, target)
                    }
                }
            }
            then("fly") {
                requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                executes { ctx ->
                    val value : Int by ctx
                    val speed = (value * 0.1).toFloat()
                    player.flySpeed = speed
                    notice(value, true, player)
                }
            }
            then("walk") {
                requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                executes { ctx ->
                    val value : Int by ctx
                    val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                    player.walkSpeed = speed
                    notice(value, false, player)
                }
            }
        }
        node.requires { hasPermission(4,"admin.speed") }
        node.executes {
            if (playerOrNull != null)
                player.sendMessage(Component.text("값을 입력하세요."))
            else println("대상을 입력하세요.")
        }
    }

    private fun notice(value: Int, fly: Boolean, sender: Player, target: Player? = null) {
        target?.sendMessage(
            Component.text("관리자에 의해 ${if (fly) "비행" else "걷기"} 속도가 ").append(
                Component.text("$value ")
                    .color(TextColor.color(255, 255, 0))
            ).append(Component.text("(으)로 설정되었습니다."))
        )
        sender.sendMessage(
            Component.text("${if (fly) "비행" else "걷기"} 속도가 ").append(
                Component.text("$value ")
                    .color(TextColor.color(255, 255, 0))
            ).append(Component.text("(으)로 설정되었습니다."))
        )
    }
}