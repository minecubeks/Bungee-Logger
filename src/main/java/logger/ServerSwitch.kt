package logger

import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ServerSwitchListener(plugin: Plugin) : net.md_5.bungee.api.plugin.Listener {
    @EventHandler
    fun onChat(event: ServerConnectEvent) {
        val player: ProxiedPlayer = event.player
        var oldServer = "null"
        val reason: ServerConnectEvent.Reason? = event.reason

        if (reason != null) {
            oldServer = if (reason.name == "JOIN_PROXY") {
                "null"
            } else {
                player.server.info.name
            }
        }
        val newServer: String = event.target.name

        val datum = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val path = Paths.get("./plugins/Logger/logs/ServerSwitch.txt")
        try {
            if (!Files.exists(path)) {
                Files.createFile(path)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (oldServer != "null") {
            Files.write(path, "($datum) $player [$oldServer >> $newServer]\n".toByteArray(), StandardOpenOption.APPEND)
        } else {
            Files.write(path, "($datum) $player joined to $newServer\n".toByteArray(), StandardOpenOption.APPEND)
        }
    }
}
