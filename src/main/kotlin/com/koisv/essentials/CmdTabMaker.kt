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
                if ((args?.count() ?: 0) == 1) players
                else mutableListOf()

            "ke" ->
                if ((args?.count() ?: 0) == 1) mutableListOf("reload")
                else mutableListOf()

            "openinv" -> {
                when (args?.count() ?: 0) {
                    1 -> {
                        val search = players.find {
                            it.lowercase().startsWith(args?.getOrNull(0)?.lowercase() ?: "[!]")
                        }
                        if (search != null)
                            mutableListOf(search, "ender")
                        else
                            mutableListOf("ender")
                    }
                    2 -> mutableListOf("ender")
                    else -> mutableListOf()
                }
            }

            "spawn" -> {
                if ((args?.count() ?: 0) == 1) {
                    players.add("set")
                    players
                }
                else mutableListOf()
            }

            "speed" -> {
                val typeStrings = mutableListOf("fly", "walk").toMutableList()
                val rawSpeed = args?.getOrNull(0)?.toIntOrNull()
                val target = instance.server.onlinePlayers.find {
                    it.name == (args?.getOrNull(1) ?: return@find false)
                }

                if (rawSpeed != null) {
                    if (!(0..10).contains(rawSpeed)) return mutableListOf()
                    when (args.count()) {
                        2 -> {
                            val search = players.find { it.lowercase().startsWith(args[1].lowercase()) }
                            when {
                                args[1].isBlank() ->
                                    if (sender is Player) {
                                        typeStrings.addAll(players)
                                        if (sender.isFlying) typeStrings else typeStrings.reversed().toMutableList()
                                    } else players
                                search != null -> mutableListOf(search)
                                "fly".startsWith(args[1]) -> mutableListOf("fly")
                                "walk".startsWith(args[1]) -> mutableListOf("walk")
                                else -> mutableListOf()
                            }
                        }
                        3 -> {
                            when {
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
                } else (0..10).map { it.toString() }.toMutableList()
            }

            else -> null
        }
    }
}