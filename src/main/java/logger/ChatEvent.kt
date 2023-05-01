package logger

import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatEventListener(private val plugin: Plugin) : net.md_5.bungee.api.plugin.Listener {
    @EventHandler
    fun onChat(event: ChatEvent) {
        val sender = event.sender
        val message = event.message
        val sender2: ProxiedPlayer = event.sender as ProxiedPlayer
        val server: String = sender2.server.info.name
        val currentDateTime = LocalDateTime.now()
        val datum = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val path = Paths.get("./plugins/Logger/logs/Chat.txt")
        val path2 = Paths.get("./plugins/Logger/logs/Commands.txt")
        try {
            if (!Files.exists(path)) {
                Files.createFile(path)
            }
            if (!Files.exists(path2)) {
                Files.createFile(path2)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (!event.isCommand) {
            Files.write(path, "($datum) [$server] $sender typed: $message\n".toByteArray(), StandardOpenOption.APPEND)
        } else {
            Files.write(path2, "($datum) [$server] $sender executed: $message\n".toByteArray(), StandardOpenOption.APPEND)
        }
    }

}