package ru.ilezzov.pluginBlank.file;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import ru.ilezzov.pluginBlank.database.DatabaseType;

import java.util.concurrent.TimeUnit;

public class DatabaseFile extends OkaeriConfig {
    @Comment("Database Connection Settings / Настройки подключения к базе данных")
    public Database database = new Database();

    @Comment({
            "",
            "Cache settings",
            "Настройки кеширования"
    })
    public Cache cache = new Cache();

    public static class Database extends OkaeriConfig {

        @Comment({
                "Database type / Тип базы данных.",
                "Available values / Допустимые значения: POSTGRESQL, MYSQL, SQLITE, MARIADB"
        })
        public DatabaseType type = DatabaseType.SQLITE;

        public String host = "localhost";
        public int port = 3306;

        @CustomKey("database-name")
        public String databaseName = "database";

        public String username = "root";
        public String password = "root";

        @Comment({
                "",
                "Should I use SSL connections / Использовать ли SSL для подключения"
        })
        @CustomKey("use-ssl")
        public boolean useSsl = false;

        @Comment({
                "",
                "SQLITE settings"
        })
        public Sqlite sqlite = new Sqlite();

        @Comment({
                "",
                "Optimized HikariCP connection pool settings / Оптимизированные настройки пула соединений HikariCP"
        })
        @CustomKey("pool-settings")
        public PoolSettings poolSettings = new PoolSettings();
    }

    public static class Sqlite extends OkaeriConfig {

        @Comment("Database file name / Имя файла")
        @CustomKey("file-name")
        public String fileName = "storage.db";
    }

    public static class PoolSettings extends OkaeriConfig {

        @Comment({
                "Maximum number of simultaneously active connections in the pool",
                "Максимальное количество одновременно активных соединений в пуле"
        })
        @CustomKey("maximum-pool-size")
        public int maximumPoolSize = 10;

        @Comment({
                "",
                "Minimum number of empty connections held in memory",
                "Минимальное количество удерживаемых в памяти пустых соединений"
        })
        @CustomKey("minimum-idle")
        public int minimumIdle = 5;

        @Comment({
                "",
                "Timeout for waiting to receive a connection from the pool (in milliseconds)",
                "Таймаут ожидания получения соединения из пула (в миллисекундах)"
        })
        @CustomKey("connection-timeout")
        public long connectionTimeout = 30000;

        @Comment({
                "",
                "Maximum connection lifetime in the pool (in milliseconds)",
                "Максимальное время жизни соединения в пуле (в миллисекундах)"
        })
        @CustomKey("maximum-lifetime")
        public long maximumLifetime = 1800000;

        @Comment({
                "",
                "The time after which a idle connection will be closed (in milliseconds)",
                "Время, через которое простаивающее соединение будет закрыто (в миллисекундах)"
        })
        @CustomKey("idle-timeout")
        public long idleTimeout = 600000;
    }

    public static class Cache extends OkaeriConfig {

        @Comment({
                "Cache settings for player data (PlayerData)",
                "Настройки кэша для данных игроков (PlayerData)"
        })
        @CustomKey("player-data")
        public PlayerData playerData = new PlayerData();
    }

    public static class PlayerData extends OkaeriConfig {

        @Comment({
                "The lifetime of data in the cache after the LAST access to it (reading or writing)",
                "Время жизни данных в кэше после ПОСЛЕДНЕГО обращения к ним (чтения или записи)"
        })
        @CustomKey("expire-after-access")
        public ExpireAfterAccess expireAfterAccess = new ExpireAfterAccess();

        @Comment({
                "",
                "The maximum number of objects that can be stored in RAM at the same time",
                "Максимальное количество объектов, одновременно хранящихся в оперативной памяти"
        })
        @CustomKey("maximum-size")
        public int maximumSize = 5000;
    }

    public static class ExpireAfterAccess extends OkaeriConfig {
        public int value = 15;

        @Comment("TimeUnit: SECONDS, MINUTES, HOURS, DAYS")
        public TimeUnit unit = TimeUnit.MINUTES;
    }
}
