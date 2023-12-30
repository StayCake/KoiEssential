package com.koisv.essentials

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.koisv.essentials.Main.Companion.back
import com.koisv.essentials.Main.Companion.backLoc
import com.koisv.essentials.Main.Companion.instance
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent

class Events : Listener {
    private val config = instance.config

    @EventHandler
    private fun chat(e:AsyncChatEvent) {
        val prefix = chat?.getPlayerPrefix(e.player)
        val playerName = (e.player.displayName() as TextComponent).content()
        e.isCancelled = true
        val colorCode = Regex("&")
        for (p in Bukkit.getOnlinePlayers()) {
            p.sendMessage(Component.text(
                "${prefix?.let { colorCode.replace(it, "§") }}§r$playerName ≫ ${(e.message() as TextComponent).content()}"
            ))
        }
    }

    @EventHandler
    private fun ping(e:PaperServerListPingEvent) {
        e.version = "KoiSV 1.13-1.20"
        if (e.client.protocolVersion < 393) {
            e.motd(Component.text("올바르지 않은 버전입니다!\n1.13 버전 이상을 사용해 주세요."))
        } else {
            e.motd(Component.text("이서버는이미정신줄을놓고개막장으로건설된서버이오니다른유저분들은병맛으로즐겨주시기바랍니다")
                .color(TextColor.color(Color.YELLOW.asRGB())))
        }
    }

    @EventHandler
    private fun respawn(e:PlayerRespawnEvent) {
        val location = config.getLocation("spawn")
        if (location != null && !e.isBedSpawn && !e.isAnchorSpawn) e.respawnLocation = location
    }

    @EventHandler
    private fun join(e:PlayerJoinEvent) {
        e.player.flySpeed = 0.1F
        e.player.walkSpeed = 0.2F
        e.joinMessage(Component.text("[+] ${e.player.name}").color(TextColor.color(Color.YELLOW.asRGB())))
    }

    @EventHandler
    private fun quit(e:PlayerQuitEvent) {
        e.quitMessage(Component.text("[-] ${e.player.name}").color(TextColor.color(Color.YELLOW.asRGB())))
    }

    @EventHandler
    private fun backSave(e:PlayerTeleportEvent) {
        if (!e.isCancelled) {
            back.set(e.player.uniqueId.toString(), e.from)
            back.save(backLoc)
            back.load(backLoc)
        }
    }

    @EventHandler
    private fun blockExplode(e:BlockExplodeEvent) {
        if (!config.getBoolean("explode")) e.isCancelled = true
    }

    @EventHandler
    private fun entityExplode(e:EntityExplodeEvent) {
        if (!config.getBoolean("explode")) e.isCancelled = true
    }

    @EventHandler
    private fun entityDamage(e:EntityDamageEvent) {
        if (
            (e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
            || e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            && !config.getBoolean("explode")
        ) e.isCancelled = true
    }
}