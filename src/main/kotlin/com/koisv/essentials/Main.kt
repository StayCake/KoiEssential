package com.koisv.essentials

import com.koisv.essentials.commands.*
import hazae41.minecraft.kutils.bukkit.msg
import hazae41.minecraft.kutils.get
import io.github.monun.kommand.kommand
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

var chat: Chat? = null

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set
        lateinit var backloc: File
            private set
        lateinit var back: YamlConfiguration
            private set
    }

    private fun setupChat(): Boolean {
        val rsp: RegisteredServiceProvider<Chat> = server.servicesManager.getRegistration(
            Chat::class.java
        ) ?: return true
        chat = rsp.provider
        return chat != null
    }

    override fun onEnable() {

        if (!setupChat()) {
            println(String.format("[%s] - Vault가 감지되지 않았습니다!", description.name))
            server.pluginManager.disablePlugin(this)
            return
        }

        println(String.format("[%s] - 가동 시작!", description.name))

        instance = this

        server.pluginManager.registerEvents(Events(), this)
        saveDefaultConfig()

        backloc = dataFolder["back.yml"]
        back = YamlConfiguration.loadConfiguration(backloc)
        if (!backloc.canRead()) {
            back.save(backloc)
        }

        kommand {
            register("hat") {
                Hat.register(this)
            }
            register("spawn","넴주","스폰") {
                Spawn.register(this)
            }
            register("speed","속도"){
                Speed.register(this)
            }
            register("openinv","인벤토리"){
                Openinv.register(this)
            }
            register("back"){
                Back.register(this)
            }
            register("mem"){
                requires { hasPermission(4,"admin.gc") }
                executes {
                    val runtime = Runtime.getRuntime()
                    val report = """
                            ==========> ${Bukkit.getVersion()} <==========
                            TPS : ${Bukkit.getTPS()}
                            접속자 : ${Bukkit.getOnlinePlayers().size} / ${Bukkit.getMaxPlayers()}명
                            틱 타임 : ${Bukkit.getAverageTickTime()} ms
                            버킷 : ${Bukkit.getBukkitVersion()}
                            메모리 : ${((runtime.totalMemory() - runtime.freeMemory())/(8/1024))} / ${(runtime.maxMemory()/(8/1024))} MB
                        """.trimIndent()
                    if (playerOrNull != null)
                        player.msg(report)
                    else
                        println(report)
                }
            }
            register("ke"){
                then("reload") {
                    requires { hasPermission(4,"admin.reload") }
                    executes {
                        reloadConfig()
                        if (!backloc.canRead()) {
                            back.save(backloc)
                        }
                        back.load(backloc)
                        if (sender is Player) sender.msg("리로드 완료!") else println("리로드 완료!")
                    }
                }
            }
        }
    }

    override fun onDisable() {
        saveDefaultConfig()
        println(String.format("[%s] - 가동 중지.", description.name))
    }
}