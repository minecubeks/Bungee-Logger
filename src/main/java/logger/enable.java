package logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import net.md_5.bungee.api.plugin.Plugin;

public class enable {
    private Connection connection;
    public void onEnable(Plugin plugin) {
        plugin.getLogger().info("Starting Bungge Logger by XAP3X#8831");
        // Check if config.json exist
        File configFile = new File("./plugins/Logger/config.json");
        if (!configFile.exists()) {
            plugin.getLogger().info("config.json doesn't exist, creating one..");
            try {
                String defaultConfig = "{ \"host\": \"localhost\", \"port\": 3306, \"database\": \"servers_info\", \"username\": \"root\", \"password\": \"password\" }";
                Files.createDirectories(Paths.get("./plugins/Logger"));
                Files.writeString(Paths.get("./plugins/Logger/config.json"), defaultConfig);
            } catch (IOException e) {
                System.err.println("Failed to create config file, error: " + e.getMessage());
                return;
            }
        } else {
            // Load config.json
            JsonObject config;
            try {
                Gson gson = new Gson();
                config = gson.fromJson(new FileReader(plugin.getDataFolder() + "/config.json"), JsonObject.class);
                String host = config.get("host").getAsString();
                int port = config.get("port").getAsInt();
                String database = config.get("database").getAsString();
                String username = config.get("username").getAsString();
                String password = config.get("password").getAsString();
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().severe("Failed to load MySQL driver, error:: " + e.getMessage());
                    return;
                }
                try {
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to connect to MySQL DB, error: " + e.getMessage());
                    return;
                }

                // MySQL query
                try {
                    Statement statement = connection.createStatement();
                    // Checking if table exist
                    ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS table_exists FROM information_schema.tables WHERE table_name = 'proxy'");
                    //If not exist create and insert values
                    if (resultSet.next() && resultSet.getInt("table_exists") <= 0) {
                        statement.execute("CREATE TABLE IF NOT EXISTS proxy (Last_Launch VARCHAR(45) PRIMARY KEY NOT NULL, Last_End VARCHAR(45) NOT NULL, status INT NOT NULL)");
                        statement.execute("INSERT INTO `proxy` (`Last_Launch`, `Last_End`, `status`) VALUES ('0000-00-00 00:00:00', '0000-00-00 00:00:00', 0)");
                    }
                    // Updating last launch
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String datum = currentDateTime.format(formatter);
                    statement.execute(String.format("UPDATE `proxy` SET `Last_Launch` = '%s'", datum));
                    statement.execute("UPDATE `proxy` SET `status` = 1");
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to execute a MySQL query, error: " + e.getMessage());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to load and read the config.json file, error: " + e.getMessage());
            }
        }
    }
}
