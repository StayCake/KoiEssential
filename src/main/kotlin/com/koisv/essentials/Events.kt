package com.koisv.essentials

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerTeleportEvent
import java.io.File

class Events : Listener {

    private fun getBack() : YamlConfiguration {
        return Main.back
    }

    private fun getBackloc() : File {
        return Main.backloc
    }

    private fun getConfig() : FileConfiguration {
        return Main.instance.config
    }

    @EventHandler
    private fun chat(e:AsyncChatEvent) {
        val prefix = chat?.getPlayerPrefix(e.player)
        val playername = (e.player.displayName() as TextComponent).content()
        e.isCancelled = true
        for (p in Bukkit.getOnlinePlayers()) {
            p.sendMessage(e.player.uniqueId,"${prefix?.replace("&","§")}§r$playername ≫ ${(e.message() as TextComponent).content()}")
        }
    }

    @EventHandler
    private fun ping(e:PaperServerListPingEvent) {
        e.version = "KoiSV 1.17.1"
        if (e.client.protocolVersion != e.protocolVersion) {
            e.motd(Component.text("올바르지 않은 버전입니다!\n1.17.1 버전을 사용해 주세요."))
        } else {
            e.motd(Component.text("이서버는이미정신줄을놓고개막장으로건설된서버이오니다른유저분들은병맛으로즐겨주시기바랍니다")
                .color(TextColor.color(Color.YELLOW.asRGB())))
        }
    }

    @EventHandler
    private fun respawn(e:PlayerRespawnEvent) {
        val location = getConfig().getLocation("spawn")
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
    private fun backsave(e:PlayerTeleportEvent) {
        if (!e.isCancelled) {
            getBack().set("${e.player.uniqueId}", e.from)
            getBack().save(getBackloc())
            getBack().load(getBackloc())
        }
    }

    @EventHandler
    private fun blockexplode(e:BlockExplodeEvent) {
        if (!getConfig().getBoolean("explode")) e.isCancelled = true
    }

    @EventHandler
    private fun entityexplode(e:EntityExplodeEvent) {
        if (!getConfig().getBoolean("explode")) e.isCancelled = true
    }

    @EventHandler
    private fun entitydamage(e:EntityDamageEvent) {
        if (
            (e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
            || e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            && !getConfig().getBoolean("explode")
        ) e.isCancelled = true
    }
}