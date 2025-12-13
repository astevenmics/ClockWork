package mist.mystralix.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DBManager {

    private static HikariDataSource dataSource;

    public static void buildConnectionPool() {

        HikariConfig config = new HikariConfig();

        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        config.setJdbcUrl("jdbc:mysql://localhost:" + dbPort + "/" + dbName);
        config.setUsername("root");
        config.setPassword(System.getenv("DB_PASSWORD"));

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