package ru.ilezzov.pluginblank.database;

public class Tables {
    public static final String playersTable = """
            CREATE TABLE IF NOT EXISTS players (
                uuid CHAR(36) PRIMARY KEY
            )
            """;

}
