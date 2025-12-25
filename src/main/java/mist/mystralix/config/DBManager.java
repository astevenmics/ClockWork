package mist.mystralix.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {

    private static HikariDataSource dataSource;

    public static void buildConnectionPool() {

        HikariConfig config = new HikariConfig();

        Dotenv dotenv = Dotenv.load();
        String dbName = dotenv.get("DB_NAME");
        String dbPort = dotenv.get("DB_PORT");
        String dbURL = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String jdbcURL = "jdbc:mysql://" + dbURL + ":" + dbPort + "/" + dbName + "?useSSL=false&serverTimezone=UTC";
        config.setJdbcUrl(jdbcURL);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);

        config.addDataSourceProperty("cachePrepStmts", "true");           // Cache prepared statements
        config.addDataSourceProperty("prepStmtCacheSize", "250");          // Max cached statements
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");     // Max SQL length cacheable
        config.addDataSourceProperty("useServerPrepStmts", "true");        // MySQL server-side statements
        config.addDataSourceProperty("useLocalSessionState", "true");      // Avoid round-trips on session state
        config.addDataSourceProperty("rewriteBatchedStatements", "true");  // Enables batching optimizations
        config.addDataSourceProperty("cacheResultSetMetadata", "true");    // Cache column metadata
        config.addDataSourceProperty("cacheServerConfiguration", "true");  // Cache config info
        config.addDataSourceProperty("elideSetAutoCommits", "true");       // Reduce auto-commit overhead
        config.addDataSourceProperty("maintainTimeStats", "false");        // Faster performance, no tracking

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setMaxLifetime(1800000);

        config.setPoolName("ClockWork MySQL Pool");

        config.setLeakDetectionThreshold(2000);

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}