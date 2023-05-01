package logger

import com.google.gson.Gson
import com.google.gson.JsonObject
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
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

class Enable(private val plugin: Plugin) {
    private var connection: Connection? = null
    fun onEnable(plugin: Plugin) {
        plugin.logger.info("Starting Bungee event Logger by XAP3X#8831")
        // Declaring date of startup
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val datum = currentDateTime.format(formatter)
        // Check if \plugins\logger exists
        try {
            if (!Files.exists(Paths.get("./plugins/Logger/"))) {
                // Creating .\plugins\logger\ directory and \logs
                Files.createDirectories(Paths.get("./plugins/Logger"))
                Files.createDirectories(Paths.get("./plugins/Logger/logs"))
            }
            val path = Paths.get("./plugins/Logger/startups.txt")
            if (!Files.exists(path)) {
                Files.createFile(path)
            }
            Files.write(path, "Started up at time: $datum\n".toByteArray(), StandardOpenOption.APPEND)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Check if config.json exist
        val configFile = File("./plugins/Logger/config.json")
        if (!configFile.exists()) {
            plugin.logger.info("config.json doesn't exist, creating one..")
            try {
                val defaultConfig = "{ \"host\": \"localhost\", \"port\": 3306, \"database\": \"servers_info\", \"username\": \"root\", \"password\": \"password\" }"
                Files.writeString(Paths.get("./plugins/Logger/config.json"), defaultConfig)
            } catch (e: IOException) {
                System.err.println("Failed to create config file, error: " + e.message)
            }
        } else {
            // Load config.json
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

                // MySQL query
                try {
                    val statement = connection?.createStatement()
                    // Checking if table exist
                    val resultSet = statement?.executeQuery("SELECT COUNT(*) AS table_exists FROM information_schema.tables WHERE table_name = 'proxy'")
                    //If not exist create and insert values
                    if (resultSet != null) {
                        if (resultSet.next() && resultSet.getInt("table_exists") <= 0) {
                            statement.execute("CREATE TABLE IF NOT EXISTS proxy (Last_Launch VARCHAR(45) PRIMARY KEY NOT NULL, Last_End VARCHAR(45) NOT NULL, status INT NOT NULL)")
                            statement.execute("INSERT INTO `proxy` (`Last_Launch`, `Last_End`, `status`) VALUES ('0000-00-00 00:00:00', '0000-00-00 00:00:00', 0)")
                        }
                    }
                    // Updating last launch
                    statement?.execute(String.format("UPDATE `proxy` SET `Last_Launch` = '%s'", datum))
                    statement?.execute("UPDATE `proxy` SET `status` = 1")
                } catch (e: SQLException) {
                    plugin.logger.severe("Failed to execute a MySQL query, error: " + e.message)
                }
            } catch (e: IOException) {
                plugin.logger.severe("Failed to load and read the config.json file, error: " + e.message)
            }
        }
    }
}