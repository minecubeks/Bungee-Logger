package logger

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.md_5.bungee.api.plugin.Plugin
import java.io.FileReader
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Disable(private val plugin: Plugin) {
    private var connection: Connection? = null
    fun onDisable(plugin: Plugin) {
        plugin.logger.info("Disabling Bungee Logger")
        // Declaring date of startup
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val datum = currentDateTime.format(formatter)
        // Log server shutdown to file shutdowns.txt
        try {
            val path = Paths.get("./plugins/Logger/logs/shutdowns.txt")
            if (!Files.exists(path)) {
                Files.createFile(path)
            }
            Files.write(path, "Shutdown at time: $datum\n".toByteArray(), StandardOpenOption.APPEND)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val config: JsonObject
            try {
                val gson = Gson()
                config = gson.fromJson(FileReader(plugin.dataFolder.toString() + "/config.json"), JsonObject::class.java)
                val host = config["host"].asString
                val port = config["port"].asInt
                val database = config["database"].asString
                val username = config["username"].asString
                val password = config["password"].asString
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver")
                } catch (e: ClassNotFoundException) {
                    plugin.logger.severe("Failed to load MySQL driver, error:: " + e.message)
                    return
                }
                connection = try {
                    DriverManager.getConnection("jdbc:mysql://$host:$port/$database", username, password)
                } catch (e: SQLException) {
                    plugin.logger.severe("Failed to connect to MySQL DB, error: " + e.message)
                    return
                }
            } catch (e: IOException) {
                plugin.logger.severe("Failed to load and read the config.json file, error: " + e.message)
            }
            // Updating Last End
            val statement = connection!!.createStatement()
            statement.execute(String.format("UPDATE `proxy` SET `Last_End` = '%s'", datum))
            statement.execute("UPDATE `proxy` SET `status` = 0")

            // Closing MySQL session
            if (connection != null) {
                connection!!.close()
            }
        } catch (e: SQLException) {
            plugin.logger.severe("Failed to exit MySQL, error: " + e.message)
        }
    }
}