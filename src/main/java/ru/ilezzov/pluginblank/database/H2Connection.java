package ru.ilezzov.pluginblank.database;

import org.bukkit.entity.Player;
import ru.ilezzov.pluginblank.models.PluginPlayer;

import java.sql.*;
import java.util.UUID;

public class H2Connection implements DBConnection {
    private final String path;
    private final String URL_CONNECTION_TEMPLATE = "jdbc:sqlite:%s";

    private Connection connection;

    public H2Connection(final String path) {
        this.path = path;
    }

    @Override
    public void connect() throws SQLException {
        this.connection = DriverManager.getConnection(URL_CONNECTION_TEMPLATE.formatted(path));
        checkTables();
    }

    @Override
    public void checkTables() throws SQLException {
        this.connection.prepareStatement(Tables.playersTable).execute();
    }

    @Override
    public void updateUser(final Player player) throws SQLException {
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("UPDATE players SET uuid = ? WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getGameMode().name());
            preparedStatement.setString(2, player.getGameMode().name());
            preparedStatement.execute();
        }
    }

    @Override
    public void insertUser(final Player player) throws SQLException {
        try(final PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO players (uuid) VALUES (?)")) {
            preparedStatement.setObject(1, player.getUniqueId());

            preparedStatement.execute();
        }
    }

    @Override
    public void insertUser(final PluginPlayer player) throws SQLException {
        try(final PreparedStatement preparedStatement = this.connection.prepareStatement("INSERT INTO players (uuid) VALUES (?)")) {
            preparedStatement.setObject(1, player.getUniqueId());
            preparedStatement.execute();
        }
    }

    @Override
    public boolean checkUser(final UUID playerUniqueId) throws SQLException {
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT COUNT(*) FROM players WHERE uuid = ?")) {
            preparedStatement.setObject(1, playerUniqueId);

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;
        }
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public PluginPlayer getPlayer(final Player player) throws SQLException {
        try (final PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setObject(1, player.getUniqueId());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return new PluginPlayer(
                            UUID.fromString(resultSet.getString("uuid"))
                    );
                }
            }
            return null;
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}
