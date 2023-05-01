package logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class disable {
    private Connection connection;
    public void onDisable(Plugin plugin) {
        plugin.getLogger().info("Disabling Bungge Logger");
        try {
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
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to load and read the config.json file, error: " + e.getMessage());
            }
            // Updating Last End
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String datum = currentDateTime.format(formatter);

            Statement statement = connection.createStatement();
            statement.execute(String.format("UPDATE `proxy` SET `Last_End` = '%s'", datum));
            statement.execute("UPDATE `proxy` SET `status` = 0");

            // Closing MySQL session
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to exit MySQL, error: " + e.getMessage());
        }
    }
}
