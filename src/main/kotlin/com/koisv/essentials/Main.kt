package com.koisv.essentials

import net.milkbowl.vault.chat.Chat
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Logger

var chat: Chat? = null

lateinit var gLogger: Logger

class Main : JavaPlugin() {

    companion object {
        lateinit var instance: Main
            private set
        lateinit var backLoc: File
            private set
        lateinit var back: YamlConfiguration
            private set
    }

    private fun setupChat(): Boolean {
        val rsp: RegisteredServiceProvider<Chat> = server.servicesManager.getRegistration(
            Chat::class.java
        ) ?: return false
        chat = rsp.provider
        return true
    }

    override fun onEnable() {

        if (!setupChat()) {
            println("[$name] - Vault 플러그인이 감지되지 않았습니다!")
            server.pluginManager.disablePlugin(this)
            return
        }

        println("[$name] - 가동 시작!")

        instance = this
        gLogger = logger

        server.pluginManager.registerEvents(Events(), this)
        saveDefaultConfig()

        backLoc = File(dataFolder, "back.yml")
        back = YamlConfiguration.loadConfiguration(backLoc)
        if (!backLoc.canRead()) {
            back.save(backLoc)
        }

        val commands = listOf("back", "hat", "ke", "mem", "openinv", "spawn", "speed")

        commands.forEach {
            getCommand(it)?.setExecutor(CmdExecutor())
            getCommand(it)?.tabCompleter = CmdTabMaker()
        }
    }

    override fun onDisable() {
        saveDefaultConfig()
        println("[$name] - 가동 중지.")
    }
}