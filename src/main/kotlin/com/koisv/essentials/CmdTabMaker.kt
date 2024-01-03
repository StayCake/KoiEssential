package com.koisv.essentials

import com.koisv.essentials.Main.Companion.instance
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CmdTabMaker : TabCompleter {
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<out String>?): MutableList<String>? {
        val players = instance.server.onlinePlayers.map { it.name }.toMutableList()

        return when (cmd.name) {
            "back" ->
                if ((args?.count() ?: 0) == 1 && sender.hasPermission("kes.back.others")) players
                else mutableListOf()

            "ke" ->
                if ((args?.count() ?: 0) == 1 && sender.hasPermission("kes.control.reload")) mutableListOf("reload")
                else mutableListOf()

            "openinv" -> {
                if (sender.hasPermission("kes.openinv"))
                    when (args?.count() ?: 0) {
                        1 -> {
                            val search = players.find {
                                it.lowercase().startsWith(args?.getOrNull(0)?.lowercase() ?: "[!]")
                            }

                            val finalList = mutableListOf<String>()
                            if (sender.hasPermission("kes.openinv.ender")) finalList.add("ender")
                            if (search != null && sender.hasPermission("kes.openinv.others")) finalList.add(search)
                            finalList
                        }
                        2 ->
                            if (sender.hasPermission("kes.openinv.others.ender")) mutableListOf("ender")
                            else mutableListOf()
                        else -> mutableListOf()
                    }
                else mutableListOf()
            }

            "spawn" -> {
                if ((args?.count() ?: 0) == 1) {
                    if (sender.hasPermission("kes.spawn.set")) players.add("set")
                    if (sender.hasPermission("kes.spawn.others")) players
                    else mutableListOf()
                }
                else mutableListOf()
            }

            "speed" -> {
                if (!sender.hasPermission("kes.speed")) return mutableListOf()
                val typeStrings = mutableListOf("fly", "walk").toMutableList()
                val rawSpeed = args?.getOrNull(0)?.toIntOrNull()
                val target = instance.server.onlinePlayers.find {
                    it.name == (args?.getOrNull(1) ?: return@find false)
                }

                if (rawSpeed != null) {
                    if (!(-10..10).contains(rawSpeed)) return mutableListOf()
                    when (args.count()) {
                        2 -> {
                            val search = players.find { it.lowercase().startsWith(args[1].lowercase()) }
                            when {
                                args[1].isBlank() -> when {
                                    sender is Player -> {
                                        if (sender.hasPermission("kes.speed.others")) typeStrings.addAll(players)
                                        if (sender.isFlying) typeStrings else typeStrings.reversed().toMutableList()
                                    }
                                    !sender.hasPermission("kes.speed.others") -> mutableListOf()
                                    else -> players
                                }
                                search != null ->
                                    if (sender.hasPermission("kes.speed.others")) mutableListOf(search)
                                    else mutableListOf()

                                "fly".startsWith(args[1]) -> mutableListOf("fly")
                                "walk".startsWith(args[1]) -> mutableListOf("walk")
                                else -> mutableListOf()
                            }
                        }
                        3 -> {
                            when {
                                !sender.hasPermission("kes.speed.others") -> mutableListOf()
                                target == null -> mutableListOf()
                                target.isFlying && args[2].isBlank() -> typeStrings
                                !target.isFlying && args[2].isBlank() -> typeStrings.reversed().toMutableList()
                                "fly".startsWith(args[2]) -> mutableListOf("fly")
                                "walk".startsWith(args[2]) -> mutableListOf("walk")
                                else -> mutableListOf()
                            }
                        }
                        else -> mutableListOf()
                    }
                } else mutableListOf((-10).toString(), 10.toString())
            }

            else -> null
        }
    }
}