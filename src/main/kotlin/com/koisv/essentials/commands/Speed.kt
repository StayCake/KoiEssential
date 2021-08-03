package com.koisv.essentials.commands

import hazae41.minecraft.kutils.bukkit.msg
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.getValue
import io.github.monun.kommand.node.LiteralNode
import org.bukkit.entity.Player
import kotlin.math.ceil

object Speed {
    fun register(node: LiteralNode) {
        node.then("value" to KommandArgument.int(1,10)) {
            requires { hasPermission(4,"admin.speed") }
            executes { ctx ->
                if (playerOrNull != null) {
                    val value: Int by ctx
                    when (player.isFlying) {
                        true -> {
                            val speed = (value * 0.1).toFloat()
                            player.flySpeed = speed
                            player.msg("비행 속도가 §e$value §f(으)로 설정되었습니다.")
                        }
                        false -> {
                            val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                            player.walkSpeed = speed
                            player.msg("걷기 속도가 §e$value §f(으)로 설정되었습니다.")
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
                            target.msg("관리자에 의해 비행 속도가 §e$value §f(으)로 설정되었습니다.")
                            player.msg("비행 속도가 §e$value §f(으)로 설정되었습니다.")
                        }
                        false -> {
                            val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                            target.walkSpeed = speed
                            target.msg("관리자에 의해 걷기 속도가 §e$value §f(으)로 설정되었습니다.")
                            player.msg("걷기 속도가 §e$value §f(으)로 설정되었습니다.")
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
                        target.msg("관리자에 의해 비행 속도가 §e$value §f(으)로 설정되었습니다.")
                        player.msg("비행 속도가 §e$value §f(으)로 설정되었습니다.")
                    }
                }
                then("walk") {
                    requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                    executes { ctx ->
                        val value: Int by ctx
                        val target : Player by ctx
                        val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                        target.walkSpeed = speed
                        target.msg("관리자에 의해 걷기 속도가 §e$value §f(으)로 설정되었습니다.")
                        player.msg("걷기 속도가 §e$value §f(으)로 설정되었습니다.")
                    }
                }
            }
            then("fly") {
                requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                executes { ctx ->
                    val value : Int by ctx
                    val speed = (value * 0.1).toFloat()
                    player.flySpeed = speed
                    player.msg("비행 속도가 §e$value §f(으)로 설정되었습니다.")
                }
            }
            then("walk") {
                requires { playerOrNull != null && hasPermission(4,"admin.speed") }
                executes { ctx ->
                    val value : Int by ctx
                    val speed = (ceil(value.toFloat() / 2) * 0.2).toFloat()
                    player.walkSpeed = speed
                    player.msg("걷기 속도가 §e$value §f(으)로 설정되었습니다.")
                }
            }
        }
        node.requires { hasPermission(4,"admin.speed") }
        node.executes {
            if (playerOrNull != null) player.msg("값을 입력하세요.") else println("대상을 입력하세요.")
        }
    }
}